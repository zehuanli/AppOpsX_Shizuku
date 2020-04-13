package com.zzzmode.appopsx;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcelable;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.app.IAppOpsService;
import com.zzzmode.appopsx.common.OpEntry;
import com.zzzmode.appopsx.common.OpsResult;
import com.zzzmode.appopsx.common.PackageOps;
import com.zzzmode.appopsx.common.ReflectUtils;

import java.util.ArrayList;
import java.util.List;

import moe.shizuku.api.ShizukuBinderWrapper;
import moe.shizuku.api.ShizukuService;
import moe.shizuku.api.SystemServiceHelper;

public class ShizukuManager {
    private static final String TAG = "ShizukuManager";

    private static ShizukuManager sShizukuManager;

    private OpsxManager.Config mConfig;

    private boolean initialized = false;
    private IPackageManager packageManager;
    private IAppOpsService appOpsService;

    static ShizukuManager getInstance(OpsxManager.Config config) {
        if (sShizukuManager == null) {
            synchronized (ShizukuManager.class) {
                if (sShizukuManager == null) {
                    sShizukuManager = new ShizukuManager(config);
                }
            }
        }
        return sShizukuManager;
    }

    private ShizukuManager(OpsxManager.Config config) {
        mConfig = config;
        if (!ShizukuService.pingBinder()) {
            Toast.makeText(mConfig.context, "WARNING: Shizuku server not running", Toast.LENGTH_LONG).show();
        } else {
            packageManager = IPackageManager.Stub.asInterface(new ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package")));
            appOpsService = IAppOpsService.Stub.asInterface(new ShizukuBinderWrapper(SystemServiceHelper.getSystemService(Context.APP_OPS_SERVICE)));
            initialized = true;
        }
    }

    private void checkShizuku() {
        if (!ShizukuService.pingBinder()) {
            throw new RuntimeException("Shizuku server not running");
        }
        if (!initialized) {
            packageManager = IPackageManager.Stub.asInterface(new ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package")));
            appOpsService = IAppOpsService.Stub.asInterface(new ShizukuBinderWrapper(SystemServiceHelper.getSystemService(Context.APP_OPS_SERVICE)));
            initialized = true;
        }
    }

    OpsResult getOpsForPackage(String packageName, int userId) {
        checkShizuku();
        int uid = getPackageUid(packageName, userId);
        List<Parcelable> opsForPackage = appOpsService.getOpsForPackage(uid, packageName, null);
        ArrayList<PackageOps> packageOpses = new ArrayList<>();
        if (opsForPackage != null) {
            for (Parcelable o : opsForPackage) {
                PackageOps packageOps = ReflectUtils.opsConvert(o);
                packageOpses.add(packageOps);
            }
        } else {
            PackageOps packageOps = new PackageOps(packageName, uid, new ArrayList<OpEntry>());
            packageOpses.add(packageOps);
        }
        return new OpsResult(packageOpses,null);
    }

    OpsResult setOpsMode(String packageName, int opInt, int userId, int modeInt) {
        checkShizuku();
        int uid = getPackageUid(packageName, userId);
        appOpsService.setMode(opInt, uid, packageName, modeInt);
        return null;
    }

    OpsResult getPackagesForOps(int[] ops) {
        checkShizuku();
        List opsForPackage = appOpsService.getPackagesForOps(ops);
        ArrayList<PackageOps> packageOpses = new ArrayList<>();
        if (opsForPackage != null) {
            for (Object o : opsForPackage) {
                PackageOps packageOps = ReflectUtils.opsConvert(o);
                packageOpses.add(packageOps);
            }
        }
        return new OpsResult(packageOpses, null);
    }

    OpsResult resetAllModes(int userId, String packageName) {
        checkShizuku();
        appOpsService.resetAllModes(userId, packageName);
        return null;
    }


    private int getPackageUid(String packageName, int userId) {
        int uid = -1;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uid = packageManager.getPackageUid(packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES, userId);
            } else {
                uid = packageManager.getPackageUid(packageName, userId);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (uid == -1) {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0, userId);
                List<Class> paramsType = new ArrayList<>(2);
                paramsType.add(int.class);
                paramsType.add(int.class);
                List<Object> params = new ArrayList<>(2);
                params.add(userId);
                params.add(applicationInfo.uid);
                uid = (int) ReflectUtils.invokMethod(UserHandle.class, "getUid", paramsType, params);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return uid;
    }
}
