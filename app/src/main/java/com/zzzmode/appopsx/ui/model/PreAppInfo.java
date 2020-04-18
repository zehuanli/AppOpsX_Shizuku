package com.zzzmode.appopsx.ui.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zl on 2017/4/30.
 */

public class PreAppInfo {

    private String packageName;
    private String allowedOps;
    private String ignoredOps;
    private String erroredOps;
    private String defaultOps;
    private String foregroudOps;

    public PreAppInfo(String packageName, String allowedOps, String ignoredOps, String erroredOps, String defaultOps, String foregroudOps) {
        this.packageName = packageName;
        this.allowedOps = allowedOps;
        this.ignoredOps = ignoredOps;
        this.erroredOps = erroredOps;
        this.defaultOps = defaultOps;
        this.foregroudOps = foregroudOps;
    }

    public PreAppInfo(String packageName) {
        this.packageName = packageName;
    }

    public void setAllowedOps(String allowedOps) {
        this.allowedOps = allowedOps;
    }

    public void setIgnoredOps(String ignoredOps) {
        this.ignoredOps = ignoredOps;
    }

    public void setErroredOps(String erroredOps) {
        this.erroredOps = erroredOps;
    }

    public void setDefaultOps(String defaultOps) {
        this.defaultOps = defaultOps;
    }

    public void setForegroundOps(String foregroudOps) {
        this.foregroudOps = foregroudOps;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAllowedOps() {
        return allowedOps;
    }

    public String getIgnoredOps() {
        return ignoredOps;
    }

    public String getErroredOps() {
        return erroredOps;
    }

    public String getDefaultOps() {
        return defaultOps;
    }

    public String getForegroudOps() {
        return foregroudOps;
    }

    public List<Integer> getAllowedOpsList() {
        List<Integer> ops = new ArrayList<>();
        if (!TextUtils.isEmpty(allowedOps)) {
            String[] split = allowedOps.split(",");
            for (String s : split) {
                try {
                    ops.add(Integer.valueOf(s));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return ops;
    }

    public List<Integer> getIgnoredOpsList() {
        List<Integer> ops = new ArrayList<>();
        if (!TextUtils.isEmpty(ignoredOps)) {
            String[] split = ignoredOps.split(",");
            for (String s : split) {
                try {
                    ops.add(Integer.valueOf(s));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return ops;
    }

    public List<Integer> getErroredOpsList() {
        List<Integer> ops = new ArrayList<>();
        if (!TextUtils.isEmpty(erroredOps)) {
            String[] split = erroredOps.split(",");
            for (String s : split) {
                try {
                    ops.add(Integer.valueOf(s));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return ops;
    }

    public List<Integer> getDefaultOpsList() {
        List<Integer> ops = new ArrayList<>();
        if (!TextUtils.isEmpty(defaultOps)) {
            String[] split = defaultOps.split(",");
            for (String s : split) {
                try {
                    ops.add(Integer.valueOf(s));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return ops;
    }

    public List<Integer> getForegroundOpsList() {
        List<Integer> ops = new ArrayList<>();
        if (!TextUtils.isEmpty(foregroudOps)) {
            String[] split = foregroudOps.split(",");
            for (String s : split) {
                try {
                    ops.add(Integer.valueOf(s));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return ops;
    }


    @Override
    public String toString() {
        return "PreAppInfo{" +
                "packageName='" + packageName + '\'' +
                ", allowedOps='" + allowedOps + '\'' +
                ", ignoredOps='" + ignoredOps + '\'' +
                ", erroredOps='" + erroredOps + '\'' +
                ", defaultOps='" + defaultOps + '\'' +
                ", foregroudOps='" + foregroudOps + '\'' +
                '}';
    }
}
