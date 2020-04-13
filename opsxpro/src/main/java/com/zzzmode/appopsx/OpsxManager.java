package com.zzzmode.appopsx;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Process;
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

  private Context mContext;

  // private LocalServerManager mLocalServerManager;
  private ShizukuManager mShizukuManager;

  private int mUserHandleId;

  private int userId;

  private static String pkgName;

  private ApiSupporter apiSupporter;

  public OpsxManager(Context context) {
    this(context, new Config());
  }

  public OpsxManager(Context context, Config config) {
    mContext = context;
    config.context = mContext;
    mUserHandleId = Process.myUid() / 100000; //android.os.UserHandle.myUserId()
    // SConfig.init(context, mUserHandleId);
    userId = mUserHandleId;
    // mLocalServerManager = LocalServerManager.getInstance(config);
    mShizukuManager = ShizukuManager.getInstance(config);
    // apiSupporter = new ApiSupporter(mLocalServerManager);
    apiSupporter = new ApiSupporter();
    pkgName = context.getPackageName();
  }

  public void setUserHandleId(int uid) {
    this.userId = uid;
  }

  public Config getConfig() {
    // return mLocalServerManager.getConfig();
    return new Config();
  }

  public OpsResult getOpsForPackage(final String packageName) {
    return mShizukuManager.getOpsForPackage(packageName, userId);
  }


  private OpsResult wrapOps(OpsCommands.Builder builder) throws Exception {
    Bundle bundle = new Bundle();
    bundle.putParcelable("args",builder);
    // ClassCaller classCaller = new ClassCaller(pkgName,AppOpsHandler.class.getName(),bundle);
    // CallerResult result = mLocalServerManager.execNew(classCaller);
    // Bundle replyBundle = result.getReplyBundle();
    // return replyBundle.getParcelable("return");
    // TODO: Implement this
    return new OpsResult(new ArrayList<PackageOps>(), null);
  }

  public OpsResult getPackagesForOps(int[] ops, boolean reqNet) {
    return mShizukuManager.getPackagesForOps(ops);
  }

  public OpsResult setOpsMode(String packageName, int opInt, int modeInt) {
    return mShizukuManager.setOpsMode(packageName, opInt, userId, modeInt);
  }

  public OpsResult resetAllModes(String packageName) {
    return mShizukuManager.resetAllModes(userId, packageName);
  }

  public ApiSupporter getApiSupporter() {
    return apiSupporter;
  }

//  public void destory() {
//    if (mLocalServerManager != null) {
//      mLocalServerManager.stop();
//    }
//  }

  public boolean isRunning() {
    // return mLocalServerManager != null && mLocalServerManager.isRunning();
    // TODO: Implement this
    return true;
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


  public void closeBgServer() {
//    if(mLocalServerManager != null){
//      mLocalServerManager.closeBgServer();
//      mLocalServerManager.stop();
//    }
    // TODO: Implement unlink binder, if necessary
  }

  public static boolean isEnableSELinux() {
    return AssetsUtils.isEnableSELinux();
  }

  public static class Config {

    public boolean allowBgRunning = false;
    public String logFile;
    public boolean printLog = false;
    public boolean useAdb = false;
    public boolean rootOverAdb = false;
    public String adbHost = "127.0.0.1";
    public int adbPort = 5555;
    Context context;
  }
}
