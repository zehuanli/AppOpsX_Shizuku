package com.zzzmode.appopsx.ui.constraint;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppOpsMode {
    public static final int MODE_ALLOWED = 0;
    public static final int MODE_IGNORED = 1;
    public static final int MODE_ERRORED = 2;
    public static final int MODE_DEFAULT = 3;
    public static final int MODE_FOREGROUND = 4;
    public static final List<Integer> DISPLAY_MODES;

    private static final Integer[] _DISPLAY_MODES = {MODE_ALLOWED, MODE_IGNORED, MODE_FOREGROUND};

    static {
        DISPLAY_MODES = Collections.unmodifiableList(Arrays.asList(_DISPLAY_MODES));
    }
}
