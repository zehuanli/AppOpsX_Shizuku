package com.zzzmode.appopsx.ui.model;

import android.app.AppOpsManager;
import com.zzzmode.appopsx.common.FixCompat;
import com.zzzmode.appopsx.common.OpEntry;
import com.zzzmode.appopsx.ui.core.AppConstraint;

/**
 * Created by zl on 2016/11/18.
 */

public class OpEntryInfo {

  private static Integer sMaxLength = null;

  public OpEntry opEntry;
  public String opName;
  public String opPermsName;
  public String opPermsLab;
  public String opPermsDesc;
  public int mode;
  public int icon;
  public String groupName;

  public OpEntryInfo(OpEntry opEntry) {
    if (opEntry != null) {
      this.opEntry = opEntry;
      this.mode = opEntry.getMode();
      String[] sOpNames = FixCompat.sOpNames();
      if (sMaxLength == null && sOpNames != null) {
        sMaxLength = sOpNames.length;
      }

      if (opEntry.getOp() < sMaxLength) {
        String[] sOpPerms = FixCompat.sOpPerms();

        if (sOpNames != null) {
          this.opName = sOpNames[opEntry.getOp()];
        }
        if (sOpPerms != null) {
          this.opPermsName = sOpPerms[opEntry.getOp()];
        }



      }
    }
  }

  public boolean isAllowOrForeground() {
    return this.mode == AppConstraint.MODE_ALLOWED || this.mode == AppConstraint.MODE_FOREGROUND;
  }

  @Override
  public String toString() {
    return "OpEntryInfo{" +
        ", opName='" + opName + '\'' +
        ", opPermsName='" + opPermsName + '\'' +
        ", opPermsLab='" + opPermsLab + '\'' +
        ", mode=" + mode +
        '}';
  }
}
