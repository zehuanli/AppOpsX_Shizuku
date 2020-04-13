package com.zzzmode.appopsx;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.PermissionInfo;
import android.content.pm.UserInfo;
import android.os.Build;
import android.os.IUserManager;
import android.os.Parcelable;
import android.os.RemoteException;
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

import moe.shizuku.api.ShizukuApiConstants;
import moe.shizuku.api.ShizukuBinderWrapper;
import moe.shizuku.api.ShizukuService;
import moe.shizuku.api.SystemServiceHelper;

public class ShizukuManager {
    private static final String TAG = "ShizukuManager";

    private static ShizukuManager sShizukuManager;

    private Context mContext;

    private boolean initialized = false;
    private IPackageManager packageManager;
    private IAppOpsService appOpsService;
    private IUserManager userManager;

    static ShizukuManager getInstance(Context context) {
        if (sShizukuManager == null) {
            synchronized (ShizukuManager.class) {
                if (sShizukuManager == null) {
                    sShizukuManager = new ShizukuManager(context);
                }
            }
        }
        return sShizukuManager;
    }

    private ShizukuManager(Context context) {
        mContext = context;
        if (!ShizukuService.pingBinder()) {
            Toast.makeText(mContext, "WARNING: Shizuku server not running", Toast.LENGTH_LONG).show();
        } else if (mContext.checkSelfPermission(ShizukuApiConstants.PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "WARNING: Shizuku permission not granted", Toast.LENGTH_LONG).show();
        } else {
            initializeSystemService();
        }
    }

    private void checkShizuku() {
        if (!ShizukuService.pingBinder()) {
            throw new RuntimeException("Shizuku server not running");
        }
        if (mContext.checkSelfPermission(ShizukuApiConstants.PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("Shizuku permission not granted");
        }
        if (!initialized) {
            initializeSystemService();
        }
    }

    private void initializeSystemService() {
        packageManager = IPackageManager.Stub.asInterface(new ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package")));
        appOpsService = IAppOpsService.Stub.asInterface(new ShizukuBinderWrapper(SystemServiceHelper.getSystemService(Context.APP_OPS_SERVICE)));
        userManager = IUserManager.Stub.asInterface(new ShizukuBinderWrapper(SystemServiceHelper.getSystemService(Context.USER_SERVICE)));
        initialized = true;
    }

    PackageInfo getPackageInfo(String packageName, int flags, int userId) throws RemoteException {
        checkShizuku();
        return packageManager.getPackageInfo(packageName, flags, userId);
    }

    PermissionInfo getPermissionInfo(String permissionName, String packageName, int flags) throws RemoteException {
        checkShizuku();
        return packageManager.getPermissionInfo(permissionName, packageName, flags);
    }

    List<PackageInfo> getInstalledPackages(int flags, int uid) throws RemoteException {
        checkShizuku();
        return packageManager.getInstalledPackages(flags, uid).getList();
    }

    List<UserInfo> getUsers(boolean excludeDying) {
        checkShizuku();
        return userManager.getUsers(excludeDying);
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
        return new OpsResult(new ArrayList<PackageOps>(), null);
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
        return new OpsResult(new ArrayList<PackageOps>(), null);
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
