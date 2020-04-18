package com.zzzmode.appopsx.ui.main.backup;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zzzmode.appopsx.R;
import com.zzzmode.appopsx.ui.constraint.AppOpsMode;
import com.zzzmode.appopsx.ui.core.Helper;
import com.zzzmode.appopsx.ui.model.AppInfo;
import com.zzzmode.appopsx.ui.model.PreAppInfo;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.ResourceCompletableObserver;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by zl on 2017/5/7.
 */

class ConfigPresenter {

    private static final String TAG = "ConfigPresenter";

    private IConfigView mView;

    private Context context;

    ConfigPresenter(Context context, IConfigView view) {
        this.mView = view;
        this.context = context;
    }


    void export(AppInfo[] appInfos) {
        final int max = appInfos.length;

        final AtomicInteger progress = new AtomicInteger();
        mView.showProgress(true, max);
        Helper.getAppsPermission(context, appInfos).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<PreAppInfo>() {
            @Override
            public void accept(@NonNull PreAppInfo appInfo) throws Exception {
                mView.setProgress(progress.incrementAndGet());
            }
        }).collect(new Callable<List<PreAppInfo>>() {
            @Override
            public List<PreAppInfo> call() throws Exception {
                return new ArrayList<PreAppInfo>();
            }
        }, new BiConsumer<List<PreAppInfo>, PreAppInfo>() {
            @Override
            public void accept(List<PreAppInfo> preAppInfos, PreAppInfo appInfo) throws Exception {
                preAppInfos.add(appInfo);
            }
        }).observeOn(Schedulers.io()).doAfterSuccess(new Consumer<List<PreAppInfo>>() {
            @Override
            public void accept(@NonNull List<PreAppInfo> preAppInfos) throws Exception {
                save2Local(preAppInfos);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<PreAppInfo>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull List<PreAppInfo> preAppInfos) {
                mView.showProgress(false, 0);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mView.showProgress(false, 0);
            }
        });
    }

    private void save2Local(List<PreAppInfo> preAppInfos) {
        //io thread

        final StringBuilder msg = new StringBuilder();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putOpt("t", System.currentTimeMillis());
            jsonObject.putOpt("v", 1);
            jsonObject.putOpt("s", preAppInfos.size());
            JSONArray pkgJsonArray = new JSONArray();
            for (PreAppInfo preAppInfo : preAppInfos) {
                JSONObject pkgObject = new JSONObject();
                pkgObject.putOpt("p", preAppInfo.getPackageName());
                String allowedOps = preAppInfo.getAllowedOps();
                if (!TextUtils.isEmpty(allowedOps)) {
                    pkgObject.putOpt("a", allowedOps);
                }
                String ignoredOps = preAppInfo.getIgnoredOps();
                if (!TextUtils.isEmpty(ignoredOps)) {
                    pkgObject.putOpt("i", ignoredOps);
                }
                String erroredOps = preAppInfo.getErroredOps();
                if (!TextUtils.isEmpty(erroredOps)) {
                    pkgObject.putOpt("e", erroredOps);
                }
                String defaultOps = preAppInfo.getDefaultOps();
                if (!TextUtils.isEmpty(defaultOps)) {
                    pkgObject.putOpt("d", defaultOps);
                }
                String foregroundOps = preAppInfo.getForegroudOps();
                if (!TextUtils.isEmpty(foregroundOps)) {
                    pkgObject.putOpt("f", foregroundOps);
                }
                pkgJsonArray.put(pkgObject);
            }
            jsonObject.putOpt("l", pkgJsonArray);
            File file = BFileUtils.saveBackup(context, jsonObject.toString());
            msg.append(context.getString(R.string.backup_success, file.getAbsoluteFile()));
        } catch (Exception e) {
            e.printStackTrace();
            msg.append("error").append(e.getMessage());
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (context != null) {
                    Toast.makeText(context, msg.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    List<RestoreModel> getRestoreFiles() {
        List<File> backFiles = BFileUtils.getBackFiles(context);
        if (backFiles != null && !backFiles.isEmpty()) {
            List<RestoreModel> models = new ArrayList<>();
            for (File backFile : backFiles) {
                RestoreModel model = readModel(backFile);
                if (model != null) {
                    models.add(model);
                }
            }

            Collections.sort(models, new Comparator<RestoreModel>() {
                @Override
                public int compare(RestoreModel o1, RestoreModel o2) {
                    return (o1.createTime < o2.createTime) ? 1 : ((o1.createTime == o2.createTime) ? 0 : -1);
                }
            });
            return models;
        }
        return null;
    }
/*
    void importBack(File file) {
        String s = BFileUtils.read2String(file);
        try {
            JSONObject jsonObject = new JSONObject(s);
            long time = jsonObject.optLong("time");
            int v = jsonObject.optInt("v");
            int size = jsonObject.optInt("size");
            JSONArray jsonArray = jsonObject.optJSONArray("opbacks");
            if (jsonArray != null && jsonArray.length() > 0) {
                int len = jsonArray.length();
                List<PreAppInfo> preAppInfos = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    JSONObject jo = jsonArray.optJSONObject(i);
                    if (jo != null) {
                        String pkg = jo.optString("pkg");
                        String ops = jo.optString("ops");
                        if (!TextUtils.isEmpty(pkg) && !TextUtils.isEmpty(ops)) {
                            preAppInfos.add(new PreAppInfo(pkg, ops));
                        }
                    }
                }
                //restoreOps(preAppInfos);
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.backup_file_lack, Toast.LENGTH_LONG).show();
        }
    }
 */


    private RestoreModel readModel(File file) {
        try {
            RestoreModel model = new RestoreModel();
            model.path = file.getAbsolutePath();
            model.fileName = file.getName();
            model.fileSize = file.length();

            String s = BFileUtils.read2String(file);
            JSONObject jsonObject = new JSONObject(s);
            model.createTime = jsonObject.optLong("t");
            model.version = jsonObject.optInt("v");
            model.size = jsonObject.optInt("s");
            JSONArray jsonArray = jsonObject.optJSONArray("l");
            if (jsonArray != null && jsonArray.length() > 0) {
                int len = jsonArray.length();
                List<PreAppInfo> preAppInfos = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    JSONObject jo = jsonArray.optJSONObject(i);
                    if (jo != null) {
                        String pkgName = jo.optString("p");
                        String allowedOps = jo.optString("a");
                        String ignoredOps = jo.optString("i");
                        String erroredOps = jo.optString("e");
                        String defaultOps = jo.optString("d");
                        String foregroundOps = jo.optString("f");
                        PreAppInfo preAppInfo = new PreAppInfo(pkgName);
                        if (!TextUtils.isEmpty(pkgName)) {
                            if (!TextUtils.isEmpty(allowedOps)) {
                                preAppInfo.setAllowedOps(allowedOps);
                            }
                            if (!TextUtils.isEmpty(ignoredOps)) {
                                preAppInfo.setIgnoredOps(ignoredOps);
                            }
                            if (!TextUtils.isEmpty(erroredOps)) {
                                preAppInfo.setErroredOps(erroredOps);
                            }
                            if (!TextUtils.isEmpty(defaultOps)) {
                                preAppInfo.setDefaultOps(defaultOps);
                            }
                            if (!TextUtils.isEmpty(foregroundOps)) {
                                preAppInfo.setForegroundOps(foregroundOps);
                            }
                        }
                        preAppInfos.add(preAppInfo);
                    }
                }
                model.preAppInfos = preAppInfos;
                return model;
            }
        } catch (Exception e) {
            if (file != null) {
                file.delete();
            }
        }
        return null;
    }

    void restoreOps(final RestoreModel model) {
        final int size = model.preAppInfos.size();
        final AtomicInteger progress = new AtomicInteger();
        mView.showProgress(true, size);

        Observable.fromIterable(model.preAppInfos).doOnNext(new Consumer<PreAppInfo>() {
            @Override
            public void accept(PreAppInfo preAppInfo) throws Exception {
                mView.setProgress(progress.incrementAndGet());
            }
        }).flatMapCompletable(new Function<PreAppInfo, CompletableSource>() {
            @Override
            public CompletableSource apply(@NonNull PreAppInfo preAppInfo) throws Exception {
                Completable allowedCompletable = Helper.setModes(context, preAppInfo.getPackageName(), AppOpsMode.MODE_ALLOWED, preAppInfo.getAllowedOpsList());
                Completable ignoredCompletable = Helper.setModes(context, preAppInfo.getPackageName(), AppOpsMode.MODE_IGNORED, preAppInfo.getIgnoredOpsList());
                Completable erroredCompletable = Helper.setModes(context, preAppInfo.getPackageName(), AppOpsMode.MODE_ERRORED, preAppInfo.getErroredOpsList());
                Completable defaultCompletable = Helper.setModes(context, preAppInfo.getPackageName(), AppOpsMode.MODE_DEFAULT, preAppInfo.getDefaultOpsList());
                Completable foregroundCompletable = Helper.setModes(context, preAppInfo.getPackageName(), AppOpsMode.MODE_FOREGROUND, preAppInfo.getForegroundOpsList());
                return allowedCompletable.andThen(ignoredCompletable).andThen(erroredCompletable).andThen(defaultCompletable).andThen(foregroundCompletable);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResourceCompletableObserver() {
            @Override
            public void onError(Throwable e) {
                progress.incrementAndGet();
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                mView.showProgress(false, 0);
                Toast.makeText(context, "恢复成功", Toast.LENGTH_LONG).show();
            }
        });
    }


}
