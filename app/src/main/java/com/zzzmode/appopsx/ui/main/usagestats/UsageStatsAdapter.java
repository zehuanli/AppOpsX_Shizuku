package com.zzzmode.appopsx.ui.main.usagestats;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.zzzmode.appopsx.R;
import com.zzzmode.appopsx.ui.core.Helper;
import com.zzzmode.appopsx.ui.core.LocalImageLoader;
import com.zzzmode.appopsx.ui.main.permission.OpSpinnerAdapter;
import com.zzzmode.appopsx.ui.main.permission.AppPermissionActivity;
import com.zzzmode.appopsx.ui.model.PermissionChildItem;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zl on 2017/8/16.
 */
class UsageStatsAdapter extends RecyclerView.Adapter<UsageStatsAdapter.ViewHolder> implements OnClickListener {
    private static final String TAG = "UsageStatsAdapter";

    private List<PermissionChildItem> mDatas = new ArrayList<>();

    void showItems(List<PermissionChildItem> value) {
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

        PermissionChildItem permissionChildItem = mDatas.get(position);

        LocalImageLoader.load(holder.imgIcon, permissionChildItem.appInfo);

        holder.tvName.setText(permissionChildItem.appInfo.appName);
        holder.imgPerm.setImageResource(permissionChildItem.opEntryInfo.icon);

        long time = permissionChildItem.opEntryInfo.opEntry.getTime();
        if (time > 0) {
            holder.tvLastTime.setText(DateUtils
                    .getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_TIME));
        } else {
            holder.tvLastTime.setText(R.string.never_used);
        }
        holder.tvPermName.setText(permissionChildItem.opEntryInfo.opPermsLab);
        holder.itemView.setTag(holder);
        holder.itemView.setOnClickListener(this);

        holder.permissionChildItem = permissionChildItem;
        holder.spinner.setSelection(holder.opSpinnerAdapter.getPositionByOpMode(permissionChildItem.opEntryInfo.mode));
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
            PermissionChildItem permissionChildItem = mDatas.get(holder.getAdapterPosition());

            Intent intent = new Intent(view.getContext(), AppPermissionActivity.class);
            intent.putExtra(AppPermissionActivity.EXTRA_APP, permissionChildItem.appInfo);
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
        private PermissionChildItem permissionChildItem;
        private OpSpinnerAdapter opSpinnerAdapter;
        private boolean initialized = false;

        ImageView imgPerm;
        ImageView imgIcon;
        TextView tvName;
        TextView tvLastTime;
        TextView tvPermName;
        Spinner spinner;

        ViewHolder(View itemView) {
            super(itemView);

            imgIcon = itemView.findViewById(R.id.app_icon);
            tvName = itemView.findViewById(R.id.app_name);
            spinner = itemView.findViewById(R.id.spinner);
            tvLastTime = itemView.findViewById(R.id.perm_last_time);
            tvPermName = itemView.findViewById(R.id.perm_name);
            imgPerm = itemView.findViewById(R.id.img_group);
            spinner = itemView.findViewById(R.id.spinner);
            opSpinnerAdapter = new OpSpinnerAdapter(itemView.getContext(), android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(opSpinnerAdapter);
            spinner.setOnItemSelectedListener(this);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!initialized) {
                return;
            }
            if (permissionChildItem.opEntryInfo != null) {
                int selectedMode = opSpinnerAdapter.getOpMode(position);
                if (selectedMode != permissionChildItem.opEntryInfo.mode) {
                    permissionChildItem.opEntryInfo.mode = selectedMode;
                    Helper.setMode(itemView.getContext(), permissionChildItem.appInfo.packageName, permissionChildItem.opEntryInfo)
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
