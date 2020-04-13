package com.zzzmode.appopsx.ui.main.group;

import android.app.AppOpsManager;
import android.content.Context;
import android.preference.PreferenceManager;
import com.zzzmode.appopsx.common.OpsResult;
import com.zzzmode.appopsx.ui.core.AppConstraint;
import com.zzzmode.appopsx.ui.core.Helper;
import com.zzzmode.appopsx.ui.model.PermissionChildItem;
import com.zzzmode.appopsx.ui.model.PermissionGroup;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.observers.ResourceSingleObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

/**
 * Created by zl on 2017/7/17.
 */

class PermGroupPresenter {

  private static final String TAG = "PermGroupPresenter";
  private IPermGroupView mView;

  private Context context;

  private ResourceSingleObserver<List<PermissionGroup>> subscriber;

  private boolean loadSuccess = false;

  PermGroupPresenter(IPermGroupView mView, Context context) {
    this.mView = mView;
    this.context = context;
  }


  void loadPerms() {
    boolean showSysApp = PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean("show_sysapp", false);

    boolean showIgnored = PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean("key_g_show_ignored", false);
    
    subscriber = new ResourceSingleObserver<List<PermissionGroup>>() {
      @Override
      public void onSuccess(List<PermissionGroup> value) {
        loadSuccess = true;
        mView.showList(value);
      }

      @Override
      public void onError(Throwable e) {
        mView.showError(e);
      }

    };

    Helper.getPermissionGroup(context, showSysApp, showIgnored)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber);

  }

  boolean isLoadSuccess(){
    return loadSuccess;
  }

  void changeMode(final int groupPosition, final int childPosition,
                  final PermissionChildItem info, final int prevMode) {
    Helper.setMode(context, info.appInfo.packageName, info.opEntryInfo)
        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new ResourceObserver<OpsResult>() {
          @Override
          public void onNext(OpsResult value) {
            if (prevMode != info.opEntryInfo.mode && (prevMode == AppConstraint.MODE_ALLOWED || info.opEntryInfo.mode == AppConstraint.MODE_ALLOWED)) {
              mView.changeTitle(groupPosition, childPosition, info.opEntryInfo.mode == AppConstraint.MODE_ALLOWED);
            }
          }

          @Override
          public void onError(Throwable e) {
            try {
              mView.refreshItem(groupPosition, childPosition);
            } catch (Exception e2) {
              e2.printStackTrace();
            }
          }

          @Override
          public void onComplete() {

          }
        });
  }


  void destroy() {
    if(subscriber != null && !subscriber.isDisposed()) {
      subscriber.dispose();
    }
  }
}
