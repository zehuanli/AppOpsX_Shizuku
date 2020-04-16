package com.zzzmode.appopsx.ui.model;

import com.zzzmode.appopsx.common.OpEntry;
import com.zzzmode.appopsx.ui.constraint.AppOps;
import com.zzzmode.appopsx.ui.constraint.AppOpsMode;

import java.util.List;

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
      List<String> sOpNames = AppOps.sOpNames;
      if (sMaxLength == null && sOpNames != null) {
        sMaxLength = sOpNames.size();
      }

      if (opEntry.getOp() < sMaxLength) {
        List<String> sOpPerms = AppOps.sOpPerms;

        if (sOpNames != null) {
          this.opName = sOpNames.get(opEntry.getOp());
        }
        if (sOpPerms != null) {
          this.opPermsName = sOpPerms.get(opEntry.getOp());
        }



      }
    }
  }

  public boolean isAllowOrForeground() {
    return this.mode == AppOpsMode.MODE_ALLOWED || this.mode == AppOpsMode.MODE_FOREGROUND;
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
