package com.zzzmode.appopsx.ui.main.permission;

import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.zzzmode.appopsx.R;
import com.zzzmode.appopsx.ui.constraint.AppOpsMode;
import com.zzzmode.appopsx.ui.model.OpEntryInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zl on 2016/11/18.
 */
class AppPermissionAdapter extends RecyclerView.Adapter<AppPermissionAdapter.ViewHolder> {
    private PermPresenter permPresenter;

    private List<OpEntryInfo> datas = new ArrayList<>();

    private boolean showPermDesc;
    private boolean showOpName;
    private boolean showPermTime;

    AppPermissionAdapter(PermPresenter permPresenter) {
        this.permPresenter = permPresenter;
    }

    void setShowConfig(boolean showPermDesc, boolean showOpName, boolean showPermTime) {
        this.showPermDesc = showPermDesc;
        this.showOpName = showOpName;
        this.showPermTime = showPermTime;
    }

    void setDatas(List<OpEntryInfo> datas) {
        this.datas = datas;
    }

    List<OpEntryInfo> getDatas() {
        return datas;
    }

    void updateItem(OpEntryInfo info) {
        if (datas != null && info != null) {
            int i = datas.indexOf(info);
            if (i != -1 && i < datas.size()) {
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_permission_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OpEntryInfo opEntryInfo = datas.get(position);
        // holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(holder);

        if (opEntryInfo != null) {
            holder.icon.setImageResource(opEntryInfo.icon);
            if (opEntryInfo.opPermsLab != null) {
                holder.title.setText(opEntryInfo.opPermsLab);
            } else {
                holder.title.setText(opEntryInfo.opName);
            }

            if (showOpName && opEntryInfo.opName != null) {
                holder.summary.setVisibility(View.VISIBLE);
                holder.summary.setText(opEntryInfo.opName);
            } else {
                holder.summary.setVisibility(View.GONE);
            }

            if (showPermDesc && opEntryInfo.opPermsDesc != null) {
                holder.summary.setVisibility(View.VISIBLE);
                holder.summary.setText(opEntryInfo.opPermsDesc);
            } else {
                if (!showOpName) {
                    holder.summary.setVisibility(View.GONE);
                }
            }

            if (showPermTime) {
                long time = opEntryInfo.opEntry.getTime();
                long rejectTime = opEntryInfo.opEntry.getRejectTime();
                StringBuilder sb = new StringBuilder();
                if (time > 0) {
                    sb.append(DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_TIME));
                }
                if (rejectTime > 0) {
                    if (time > 0) {
                        sb.append("\n");
                    }
                    sb.append(holder.itemView.getContext().getString(R.string.reject_prefix));
                    sb.append(DateUtils.getRelativeTimeSpanString(rejectTime, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_TIME));
                }
                if (sb.length() > 0) {
                    holder.lastTime.setText(sb.toString());
                } else {
                    holder.lastTime.setText(R.string.never_used);
                }
                holder.lastTime.setVisibility(View.VISIBLE);
            } else {
                holder.lastTime.setVisibility(View.GONE);
            }

            holder.opEntryInfo = opEntryInfo;
            holder.permPresenter = permPresenter;
            holder.spinner.setSelection(AppOpsMode.OP_MODE_OPTION_INDEX_MAP.getOrDefault(opEntryInfo.mode, 0));
            holder.initialized = true;
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {
        private OpEntryInfo opEntryInfo;
        private PermPresenter permPresenter;
        private boolean initialized = false;
        private List<String> op_modes;
        private Map<String, Integer> op_key_mode_map;

        ImageView icon;
        TextView title;
        TextView summary;
        TextView lastTime;
        Spinner spinner;

        ViewHolder(View itemView) {
            super(itemView);

            op_modes = Arrays.asList(itemView.getContext().getResources().getStringArray(R.array.op_modes));
            op_key_mode_map = new HashMap<String, Integer>() {{
                put(op_modes.get(0), AppOpsMode.MODE_ALLOWED);
                put(op_modes.get(1), AppOpsMode.MODE_IGNORED);
                put(op_modes.get(2), AppOpsMode.MODE_FOREGROUND);
            }};

            icon = itemView.findViewById(R.id.img_group);
            title = itemView.findViewById(android.R.id.title);
            summary = itemView.findViewById(android.R.id.summary);
            lastTime = itemView.findViewById(R.id.last_time);
            spinner = itemView.findViewById(R.id.spinner);
            spinner.setAdapter(new ArrayAdapter(itemView.getContext(), android.R.layout.simple_spinner_dropdown_item, op_modes));
            spinner.setOnItemSelectedListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spinner.performClick();
                }
            });
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!initialized) {
                return;
            }
            if (opEntryInfo != null && permPresenter != null) {
                int selectedMode = op_key_mode_map.get(parent.getItemAtPosition(position));
                if (selectedMode != opEntryInfo.mode) {
                    opEntryInfo.mode = selectedMode;
                    permPresenter.setMode(opEntryInfo);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
