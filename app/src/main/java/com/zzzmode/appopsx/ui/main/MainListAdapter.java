package com.zzzmode.appopsx.ui.main;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzzmode.appopsx.R;
import com.zzzmode.appopsx.ui.model.AppInfo;
import com.zzzmode.appopsx.ui.main.permission.AppPermissionActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zl on 2016/11/18.
 */

class MainListAdapter extends RecyclerView.Adapter<AppItemViewHolder> implements
        View.OnClickListener {

    List<AppInfo> appInfos = new ArrayList<>();

    void addItem(AppInfo info) {
        appInfos.add(info);
        notifyItemInserted(appInfos.size() - 1);
    }

    void showItems(List<AppInfo> infos) {
        appInfos.clear();
        if (infos != null) {
            appInfos.addAll(infos);
        }
        notifyDataSetChanged();
    }

    void clearItems() {
        appInfos.clear();
        notifyDataSetChanged();
    }

    void sortItems(Context context) {
        final int type = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt("pref_app_sort_type", 0);
        Comparator<AppInfo> comparator = null;
        switch (type) {
            case 0: // pass through
            case 1:
                comparator = new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo o1, AppInfo o2) {
                        return o2.appName.toLowerCase().compareTo(o1.appName.toLowerCase()) * (type * 2 - 1);
                    }
                };
                break;
            case 2:
                //按安装时间排序
                comparator = new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo o1, AppInfo o2) {
                        return Long.compare(o2.installTime, o1.installTime);
                    }
                };
                break;
            case 3:
                //按最后更新时间排序
                comparator = new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo o1, AppInfo o2) {
                        return Long.compare(Math.max(o2.installTime, o2.updateTime),
                                Math.max(o1.installTime, o1.updateTime));
                    }
                };
                break;
            default:
                break;
        }

        if (comparator != null) {
            Collections.sort(appInfos, comparator);
        }
        notifyDataSetChanged();
    }

    List<AppInfo> getAppInfos() {
        return appInfos;
    }

    @Override
    @NonNull
    public AppItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_app, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppItemViewHolder holder, int position) {
        AppInfo appInfo = appInfos.get(position);
        holder.bindData(appInfo);

        holder.tvName.setText(processText(appInfo.appName));
        holder.itemView.setTag(appInfo);
        holder.itemView.setOnClickListener(this);

    }

    protected CharSequence processText(String name) {
        return name;
    }

    @Override
    public int getItemCount() {
        return appInfos.size();
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof AppInfo) {

            Intent intent = new Intent(v.getContext(), AppPermissionActivity.class);
            intent.putExtra(AppPermissionActivity.EXTRA_APP, ((AppInfo) v.getTag()));
            v.getContext().startActivity(intent);
        }
    }


}
