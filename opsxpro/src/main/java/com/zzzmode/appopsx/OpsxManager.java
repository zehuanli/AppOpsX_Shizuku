package com.zzzmode.appopsx;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.zzzmode.android.opsxpro.BuildConfig;
import com.zzzmode.appopsx.common.CallerResult;
import com.zzzmode.appopsx.common.ClassCaller;
import com.zzzmode.appopsx.common.OpEntry;
import com.zzzmode.appopsx.common.OpsCommands;
import com.zzzmode.appopsx.common.OpsResult;
import com.zzzmode.appopsx.common.PackageOps;
import com.zzzmode.appopsx.common.SystemServiceCaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import moe.shizuku.api.ShizukuBinderWrapper;
import moe.shizuku.api.ShizukuService;
import moe.shizuku.api.SystemServiceHelper;

/**
 * Created by zl on 2016/11/13.
 */

public class OpsxManager {

  private static final String TAG = "OpsxManager";

  private ShizukuManager mShizukuManager;

  private int userId;

  public OpsxManager(Context context) {
    userId = Process.myUid() / 100000;
    mShizukuManager = ShizukuManager.getInstance(context);
  }

  public void setUserHandleId(int uid) {
    this.userId = uid;
  }

  public PackageInfo getPackageInfo(String packageName, int flags, int userId) throws RemoteException {
    return mShizukuManager.getPackageInfo(packageName, flags, userId);
  }

    public PermissionInfo getPermissionInfo(String permissionName, String packageName, int flags) throws RemoteException {
    return mShizukuManager.getPermissionInfo(permissionName, packageName, flags);
  }

  public List<PackageInfo> getInstalledPackages(int flags, int uid) throws RemoteException {
    return mShizukuManager.getInstalledPackages(flags, uid);
  }

  public List<UserInfo> getUsers(boolean excludeDying) {
    return mShizukuManager.getUsers(excludeDying);
  }

  public OpsResult getOpsForPackage(final String packageName) {
    return mShizukuManager.getOpsForPackage(packageName, userId);
  }

  public OpsResult getPackagesForOps(int[] ops) {
    return mShizukuManager.getPackagesForOps(ops);
  }

  public OpsResult setOpsMode(String packageName, int opInt, int modeInt) {
    return mShizukuManager.setOpsMode(packageName, opInt, userId, modeInt);
  }

  public OpsResult resetAllModes(String packageName) {
    return mShizukuManager.resetAllModes(userId, packageName);
  }

  public OpsResult disableAllPermission(final String packageName) throws Exception {
    OpsResult opsForPackage = getOpsForPackage(packageName);
    if (opsForPackage != null) {
      if (opsForPackage.getException() == null) {
        List<PackageOps> list = opsForPackage.getList();
        if (list != null && !list.isEmpty()) {
          for (PackageOps packageOps : list) {
            List<OpEntry> ops = packageOps.getOps();
            if (ops != null) {
              for (OpEntry op : ops) {
                if (op.getMode() != AppOpsManager.MODE_IGNORED) {
                  setOpsMode(packageName, op.getOp(), AppOpsManager.MODE_IGNORED);
                }
              }
            }
          }
        }
      } else {
        throw new Exception(opsForPackage.getException());
      }
    }
    return opsForPackage;
  }
}
