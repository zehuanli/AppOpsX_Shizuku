package com.zzzmode.appopsx.ui.model;

/**
 * Created by zl on 2017/1/18.
 */

public class PermissionChildItem {

    public AppInfo appInfo;
    public OpEntryInfo opEntryInfo;

  public PermissionChildItem() {
  }

    public PermissionChildItem(AppInfo appInfo, OpEntryInfo opEntryInfo) {
        this.appInfo = appInfo;
        this.opEntryInfo = opEntryInfo;
    }
}
