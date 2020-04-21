package com.zzzmode.appopsx.ui.core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.UserInfo;
import android.os.IUserManager;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.app.IAppOpsService;
import com.zzzmode.appopsx.R;
import com.zzzmode.appopsx.common.OpEntry;
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
    private int userId;

    private boolean initialized = false;
    private IPackageManager packageManager;
    private IAppOpsService appOpsService;
    private IUserManager userManager;

    public static ShizukuManager getInstance(Context context) {
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
        userId = Process.myUid() / 100000;
        if (!ShizukuService.pingBinder()) {
            showWarning(mContext.getString(R.string.shizuku_warning_not_running));
        } else if (mContext.checkSelfPermission(ShizukuApiConstants.PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            showWarning(mContext.getString(R.string.shizuku_warning_no_permission));
        } else {
            initializeSystemService();
        }
    }

    private void showWarning(String text) {
        try {
            Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
            Log.w(TAG, text);
        } catch (Exception ignored) {
        }
    }

    private void checkShizuku() {
        if (!ShizukuService.pingBinder()) {
            throw new RuntimeException(mContext.getString(R.string.shizuku_warning_not_running));
        }
        if (mContext.checkSelfPermission(ShizukuApiConstants.PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException(mContext.getString(R.string.shizuku_warning_no_permission));
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

    public void setUserHandleId(int userId) {
        this.userId = userId;
    }

    public int checkOperation(int opInt, String packageName) throws IllegalArgumentException {
        int uid = getPackageUid(packageName, userId);
        return appOpsService.checkOperation(opInt, uid, packageName);
    }

    public PackageInfo getPackageInfo(String packageName, int flags) throws RemoteException {
        return getPackageInfo(packageName, flags, userId);
    }

    public PackageInfo getPackageInfo(String packageName, int flags, int userId) throws RemoteException {
        checkShizuku();
        return packageManager.getPackageInfo(packageName, flags, userId);
    }

    public PermissionInfo getPermissionInfo(String permissionName, String packageName, int flags) throws RemoteException {
        checkShizuku();
        return packageManager.getPermissionInfo(permissionName, packageName, flags);
    }

    public List<PackageInfo> getInstalledPackages(int flags, int userId) throws RemoteException {
        checkShizuku();
        return packageManager.getInstalledPackages(flags, userId).getList();
    }

    public List<UserInfo> getUsers(boolean excludeDying) {
        checkShizuku();
        return userManager.getUsers(excludeDying);
    }

    /**
     * Return list only contains one PackageOps
     */
    public List<PackageOps> getOpsForPackage(String packageName) {
        return getOpsForPackage(packageName, userId);
    }

    private List<PackageOps> getOpsForPackage(String packageName, int userId) {
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
        return packageOpses;
    }

    public void setOpsMode(String packageName, int opInt, int modeInt) {
        setOpsMode(packageName, opInt, userId, modeInt);
    }

    public void setOpsMode(String packageName, int opInt, int userId, int modeInt) {
        checkShizuku();
        int uid = getPackageUid(packageName, userId);
        appOpsService.setMode(opInt, uid, packageName, modeInt);
    }

    public List<PackageOps> getPackagesForOps(int[] ops) throws RemoteException {
        checkShizuku();
        List opsForPackage = appOpsService.getPackagesForOps(ops);
        ArrayList<PackageOps> packageOpses = new ArrayList<>();
        if (opsForPackage != null) {
            for (Object o : opsForPackage) {
                PackageOps packageOps = ReflectUtils.opsConvert(o);
                if (getPackageUid(packageOps.getPackageName(), userId) == -1) {
                    continue;
                }
                packageOpses.add(packageOps);
            }
        }
        return packageOpses;
    }

    public void resetAllModes(String packageName) {
        resetAllModes(userId, packageName);
    }

    public void resetAllModes(int userId, String packageName) {
        checkShizuku();
        appOpsService.resetAllModes(userId, packageName);
    }


    private int getPackageUid(String packageName, int userId) {
        int uid = -1;
        try {
            uid = packageManager.getPackageUid(packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES, userId);
        } catch (Throwable ignored) {
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
            } catch (Throwable ignored) {
            }
        }
        return uid;
    }
}
