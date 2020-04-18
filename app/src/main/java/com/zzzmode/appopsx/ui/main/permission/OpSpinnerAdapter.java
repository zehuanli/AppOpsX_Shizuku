package com.zzzmode.appopsx.ui.main.permission;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zzzmode.appopsx.R;
import com.zzzmode.appopsx.ui.constraint.AppOpsMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    // allOpNames reads from the array "op_modes", which is ordered along with the ops modes
    private List<String> allOpNames;
    // displayOpNames and displayOpModes contain the actual items in the spinner list and honor its order
    private List<String> displayOpNames = new ArrayList<>();
    private List<Integer> displayOpModes = new ArrayList<>();

    public OpSpinnerAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
        setDefaultMode();
    }

    public int getOpMode(int position) {
        return displayOpModes.get(position);
    }

    public int getPositionByOpMode(@Nullable int opMode) {
        // FIXME: [Workground] Reset modes every time, to fit the group view in which holders do not change across groups
        setDefaultMode();
        if (!displayOpModes.contains(opMode)) {
            if (opMode >= AppOpsMode.MODE_ALLOWED && opMode <= AppOpsMode.MODE_FOREGROUND) {
                String opName = allOpNames.get(opMode);
                displayOpModes.add(opMode);
                displayOpNames.add(opName);
                add(opName);
            } else {
                // Should never reach here
                throw new RuntimeException("Unknown mode selected");
            }
        }
        return displayOpModes.indexOf(opMode);
    }

    private void setDefaultMode() {
        if (allOpNames == null) {
            allOpNames = Arrays.asList(context.getResources().getStringArray(R.array.op_modes));
        }
        displayOpNames.clear();
        displayOpModes.clear();
        for(int mode : AppOpsMode.DISPLAY_MODES) {
            displayOpNames.add(allOpNames.get(mode));
            displayOpModes.add(mode);
        }
        clear();
        addAll(displayOpNames);
    }

    /*
    @Override
    public boolean isEnabled(int position) {
        return AppOpsMode.DISPLAY_MODES.contains(displayOpModes.get(position));
    }
     */
}
