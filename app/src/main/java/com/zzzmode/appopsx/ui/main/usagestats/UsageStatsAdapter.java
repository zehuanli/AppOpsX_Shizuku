package com.zzzmode.appopsx.ui.main.usagestats;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.zzzmode.appopsx.R;
import com.zzzmode.appopsx.common.OtherOp;
import com.zzzmode.appopsx.ui.core.AppConstraint;
import com.zzzmode.appopsx.ui.core.Helper;
import com.zzzmode.appopsx.ui.core.LocalImageLoader;
import com.zzzmode.appopsx.ui.model.AppInfo;
import com.zzzmode.appopsx.ui.model.OpEntryInfo;
import com.zzzmode.appopsx.ui.main.permission.AppPermissionActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zl on 2017/8/16.
 */
class UsageStatsAdapter extends RecyclerView.Adapter<UsageStatsAdapter.ViewHolder> implements OnClickListener {
    private static final String TAG = "UsageStatsAdapter";

    private List<Pair<AppInfo, OpEntryInfo>> mDatas = new ArrayList<>();

    void showItems(List<Pair<AppInfo, OpEntryInfo>> value) {
        mDatas.clear();
        if (value != null) {
            mDatas.addAll(value);
        }
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_usagestats_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Pair<AppInfo, OpEntryInfo> pair = mDatas.get(position);

        LocalImageLoader.load(holder.imgIcon, pair.first);

        holder.tvName.setText(pair.first.appName);
        holder.imgPerm.setImageResource(pair.second.icon);

        long time = pair.second.opEntry.getTime();
        if (time > 0) {
            holder.tvLastTime.setText(DateUtils
                    .getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_TIME));
        } else {
            holder.tvLastTime.setText(R.string.never_used);
        }
        if (OtherOp.isSupportCount()) {
            holder.tvPermName.setText(holder.tvPermName.getResources().getString(R.string.perms_count, pair.second.opPermsLab, pair.second.opEntry.getAllowedCount()));
        } else {
            holder.tvPermName.setText(pair.second.opPermsLab);
        }
        holder.itemView.setTag(holder);
        holder.itemView.setOnClickListener(this);

        holder.pair = pair;
        holder.spinner.setSelection(AppConstraint.OP_MODE_INDEX_MAP.getOrDefault(pair.second.mode, 0));
        holder.initialized = true;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag instanceof ViewHolder) {
            ViewHolder holder = ((ViewHolder) tag);
            Pair<AppInfo, OpEntryInfo> pair = mDatas.get(holder.getAdapterPosition());

            Intent intent = new Intent(view.getContext(), AppPermissionActivity.class);
            intent.putExtra(AppPermissionActivity.EXTRA_APP, pair.first);
            view.getContext().startActivity(intent);
        }
    }

  /*
  @Override
  public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
    Object tag = compoundButton.getTag();
    if(tag instanceof ViewHolder){
      ViewHolder holder = ((ViewHolder) tag);
      Pair<AppInfo, OpEntryInfo> pair = mDatas.get(holder.getAdapterPosition());

      Helper.setMode(compoundButton.getContext(),pair.first.packageName,pair.second,b)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe();

    }
  }
   */

    static class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {
        private Pair<AppInfo, OpEntryInfo> pair;
        private boolean initialized = false;
        private List<String> op_modes;
        private Map<String, Integer> op_key_mode_map;

        ImageView imgPerm;
        ImageView imgIcon;
        TextView tvName;
        TextView tvLastTime;
        TextView tvPermName;
        Spinner spinner;

        ViewHolder(View itemView) {
            super(itemView);

            op_modes = Arrays.asList(itemView.getContext().getResources().getStringArray(R.array.op_modes));
            op_key_mode_map = new HashMap<String, Integer>() {{
                put(op_modes.get(0), AppConstraint.MODE_ALLOWED);
                put(op_modes.get(1), AppConstraint.MODE_IGNORED);
                put(op_modes.get(2), AppConstraint.MODE_FOREGROUND);
            }};

            imgIcon = itemView.findViewById(R.id.app_icon);
            tvName = itemView.findViewById(R.id.app_name);
            spinner = itemView.findViewById(R.id.spinner);
            tvLastTime = itemView.findViewById(R.id.perm_last_time);
            tvPermName = itemView.findViewById(R.id.perm_name);
            imgPerm = itemView.findViewById(R.id.img_group);
            spinner = itemView.findViewById(R.id.spinner);
            spinner.setAdapter(new ArrayAdapter(itemView.getContext(), android.R.layout.simple_spinner_dropdown_item, op_modes));
            spinner.setOnItemSelectedListener(this);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!initialized) {
                return;
            }
            if (pair.second != null) {
                int selectedMode = op_key_mode_map.get(parent.getItemAtPosition(position));
                if (selectedMode != pair.second.mode) {
                    pair.second.mode = selectedMode;
                    Helper.setMode(itemView.getContext(), pair.first.packageName, pair.second)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
