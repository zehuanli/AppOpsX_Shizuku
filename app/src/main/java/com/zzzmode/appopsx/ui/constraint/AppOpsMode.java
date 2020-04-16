package com.zzzmode.appopsx.ui.constraint;

import java.util.HashMap;
import java.util.Map;

public class AppOpsMode {
    public static final int MODE_ALLOWED = 0;
    public static final int MODE_IGNORED = 1;
    public static final int MODE_ERRORED = 2;
    public static final int MODE_DEFAULT = 3;
    public static final int MODE_FOREGROUND = 4;

    public static final Map<Integer, Integer> OP_MODE_OPTION_INDEX_MAP = new HashMap<Integer, Integer>() {{
        put(MODE_ALLOWED, 0);
        put(MODE_IGNORED, 1);
        put(MODE_FOREGROUND, 2);
    }};
}
