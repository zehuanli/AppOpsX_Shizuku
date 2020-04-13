package com.zzzmode.appopsx.ui.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;
import com.zzzmode.appopsx.OpsxManager;
import com.zzzmode.appopsx.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by zl on 2016/11/19.
 */

public class AppOpsx {
  private static OpsxManager sManager;

  public static OpsxManager getInstance(Context context) {
    if (sManager == null) {
      synchronized (AppOpsx.class) {
        if (sManager == null) {
          sManager = new OpsxManager(context.getApplicationContext());
        }
      }
    }
    return sManager;
  }

  public static String about(Context context) {
    StringBuilder sb = new StringBuilder();
    try {
      PackageInfo packageInfo = context.getPackageManager()
          .getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
      sb.append("GitCommitId: ").append(packageInfo.applicationInfo.metaData.getString("GIT_COMMIT_ID"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return sb.toString();
  }
}
