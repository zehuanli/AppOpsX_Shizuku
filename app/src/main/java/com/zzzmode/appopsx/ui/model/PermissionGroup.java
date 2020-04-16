package com.zzzmode.appopsx.ui.model;

import java.util.List;

public class PermissionGroup {

  public String group;
  public int opInt;
  public String opName;
  public String opPermsName;
  public String opPermsLab;
  public String opPermsDesc;
  public int grants;
  public int count;
  public int icon;
  public List<PermissionChildItem> apps;

  @Override
  public String toString() {
    return "PermissionGroup{" +
        "opInt='" + opInt + '\'' +
        "opName='" + opName + '\'' +
        ", opPermsName='" + opPermsName + '\'' +
        ", opPermsLab='" + opPermsLab + '\'' +
        ", opPermsDesc='" + opPermsDesc + '\'' +
        ", grants=" + grants +
        ", count=" + count +
        ", apps=" + apps +
        '}';
  }
}
