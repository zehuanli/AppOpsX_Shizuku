package com.zzzmode.appopsx.ui.core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;

import androidx.core.text.BidiFormatter;
import androidx.core.util.Pair;

import com.zzzmode.appopsx.BuildConfig;
import com.zzzmode.appopsx.R;
import com.zzzmode.appopsx.common.common.OpEntry;
import com.zzzmode.appopsx.common.common.PackageOps;
import com.zzzmode.appopsx.ui.constraint.AppOps;
import com.zzzmode.appopsx.ui.constraint.AppOpsMode;
import com.zzzmode.appopsx.ui.model.AppInfo;
import com.zzzmode.appopsx.ui.model.AppPermissions;
import com.zzzmode.appopsx.ui.model.OpEntryInfo;
import com.zzzmode.appopsx.ui.model.PermissionChildItem;
import com.zzzmode.appopsx.ui.model.PermissionGroup;
import com.zzzmode.appopsx.ui.model.PreAppInfo;
import com.zzzmode.appopsx.ui.main.permission.AppPermissionActivity;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.operators.single.SingleJust;
import io.reactivex.observers.ResourceSingleObserver;
import io.reactivex.schedulers.Schedulers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by zl on 2017/1/17.
 */

public class Helper {
    private static class PermGroupInfo {

        String title;
        String group;
        int icon;

        @Override
        public String toString() {
            return "PermGroupInfo{" +
                    "title='" + title + '\'' +
                    ", group='" + group + '\'' +
                    '}';
        }

        public PermGroupInfo(String title, String group, int icon) {
            this.title = title;
            this.group = group;
            this.icon = icon;
        }
    }

    private static final SparseIntArray ALWAYS_SHOWN_OP = new SparseIntArray();
    private static final Map<String, PermGroupInfo> PERMS_GROUPS = new HashMap<>();

    private static final PermGroupInfo OTHER_PERM_INFO = new PermGroupInfo(null,
            AppOps.CustomPermissionGroup.OTHER, R.drawable.perm_group_other);

    static {
        for (int op : AppOps.ALWAYS_SHOWN_OP) {
            ALWAYS_SHOWN_OP.put(op, op);
        }

        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.LOCATION, new PermGroupInfo(null, AppOps.CustomPermissionGroup.LOCATION, R.drawable.perm_group_location));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.STORAGE, new PermGroupInfo(null, AppOps.CustomPermissionGroup.STORAGE, R.drawable.perm_group_storage));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.CALENDAR, new PermGroupInfo(null, AppOps.CustomPermissionGroup.CALENDAR, R.drawable.perm_group_calendar));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.CALLING, new PermGroupInfo(null, AppOps.CustomPermissionGroup.CALLING, R.drawable.perm_group_phone_calls));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.PHONE, new PermGroupInfo(null, AppOps.CustomPermissionGroup.PHONE, R.drawable.perm_group_device));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.CAMERA, new PermGroupInfo(null, AppOps.CustomPermissionGroup.CAMERA, R.drawable.perm_group_camera));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.SMS, new PermGroupInfo(null, AppOps.CustomPermissionGroup.SMS, R.drawable.perm_group_sms));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.MODULE, new PermGroupInfo(null, AppOps.CustomPermissionGroup.MODULE, R.drawable.perm_group_sensors));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.CONTACT, new PermGroupInfo(null, AppOps.CustomPermissionGroup.CONTACT, R.drawable.perm_group_contacts));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.MICROPHONE, new PermGroupInfo(null, AppOps.CustomPermissionGroup.MICROPHONE, R.drawable.perm_group_microphone));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.AUDIO, new PermGroupInfo(null, AppOps.CustomPermissionGroup.AUDIO, R.drawable.perm_group_audio));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.SETTINGS, new PermGroupInfo(null, AppOps.CustomPermissionGroup.SETTINGS, R.drawable.perm_group_settings));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.ACCESSIBILITY, new PermGroupInfo(null, AppOps.CustomPermissionGroup.ACCESSIBILITY, R.drawable.perm_group_accessibility_new));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.SERVICE, new PermGroupInfo(null, AppOps.CustomPermissionGroup.SERVICE, R.drawable.perm_group_service));
        PERMS_GROUPS.put(AppOps.CustomPermissionGroup.OTHER, new PermGroupInfo(null, AppOps.CustomPermissionGroup.OTHER, R.drawable.perm_group_other));
    }

    private static final String TAG = "Helper";

    private static final Map<String, Integer> sPermI18N = new HashMap<String, Integer>() {{
        put("POST_NOTIFICATION", R.string.permlab_POST_NOTIFICATION);
        put("READ_CLIPBOARD", R.string.permlab_READ_CLIPBOARD);
        put("WRITE_CLIPBOARD", R.string.permlab_WRITE_CLIPBOARD);
        put("TURN_ON_SCREEN", R.string.permlab_TURN_ON_SCREEN);
        put("RUN_IN_BACKGROUND", R.string.permlab_RUN_IN_BACKGROUND);
        put("MONITOR_LOCATION", R.string.permlab_MONITOR_LOCATION);
        put("MONITOR_HIGH_POWER_LOCATION", R.string.permlab_MONITOR_HIGH_POWER_LOCATION);
        put("NEIGHBORING_CELLS", R.string.permlab_NEIGHBORING_CELLS);
        put("PLAY_AUDIO", R.string.permlab_PLAY_AUDIO);
        put("AUDIO_MASTER_VOLUME", R.string.permlab_AUDIO_MASTER_VOLUME);
        put("AUDIO_VOICE_VOLUME", R.string.permlab_AUDIO_VOICE_VOLUME);
        put("AUDIO_RING_VOLUME", R.string.permlab_AUDIO_RING_VOLUME);
        put("AUDIO_MEDIA_VOLUME", R.string.permlab_AUDIO_MEDIA_VOLUME);
        put("AUDIO_ALARM_VOLUME", R.string.permlab_AUDIO_ALARM_VOLUME);
        put("AUDIO_NOTIFICATION_VOLUME", R.string.permlab_AUDIO_NOTIFICATION_VOLUME);
        put("AUDIO_BLUETOOTH_VOLUME", R.string.permlab_AUDIO_BLUETOOTH_VOLUME);
        put("TOAST_WINDOW", R.string.permlab_TOAST_WINDOW);
        put("ACTIVATE_VPN", R.string.permlab_ACTIVATE_VPN);
        put("TAKE_AUDIO_FOCUS", R.string.permlab_TAKE_AUDIO_FOCUS);
        put("ACCESS_PHONE_DATA", R.string.permlab_ACCESS_MOBLIE_NETWORK_DATA);
        put("ACCESS_WIFI_NETWORK", R.string.permlab_ACCESS_WIFI_NETWORK_DATA);

    }};


    public static void updateShortcuts(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            getInstalledApps(context, false)
                    .concatMap(new Function<List<AppInfo>, ObservableSource<AppInfo>>() {
                        @Override
                        public ObservableSource<AppInfo> apply(List<AppInfo> appInfos) throws Exception {
                            return Observable.fromIterable(appInfos);
                        }
                    })
                    .filter(new Predicate<AppInfo>() {
                        @Override
                        public boolean test(AppInfo info) throws Exception {
                            return !BuildConfig.APPLICATION_ID.equals(info.packageName);
                        }
                    })
                    .collect(new Callable<List<AppInfo>>() {
                        @Override
                        public List<AppInfo> call() throws Exception {
                            return new ArrayList<AppInfo>();
                        }
                    }, new BiConsumer<List<AppInfo>, AppInfo>() {
                        @Override
                        public void accept(List<AppInfo> appInfos, AppInfo info) throws Exception {
                            appInfos.add(info);
                        }
                    })
                    .map(new Function<List<AppInfo>, List<AppInfo>>() {
                        @Override
                        public List<AppInfo> apply(List<AppInfo> appInfos) throws Exception {
                            Collections.sort(appInfos, new Comparator<AppInfo>() {
                                @Override
                                public int compare(AppInfo o1, AppInfo o2) {
                                    return o1.time > o2.time ? -1 : 1;
                                }
                            });
                            return appInfos;
                        }
                    })
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResourceSingleObserver<List<AppInfo>>() {
                        @Override
                        public void onSuccess(List<AppInfo> value) {
                            try {
                                updateShortcuts(context, value);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });

        }
    }

    private static void updateShortcuts(Context context, List<AppInfo> items) {
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
        List<ShortcutInfo> shortcutInfoList = new ArrayList<>();
        int max = shortcutManager.getMaxShortcutCountPerActivity();
        for (int i = 0; i < max && i < items.size(); i++) {
            AppInfo appInfo = items.get(i);
            ShortcutInfo.Builder shortcut = new ShortcutInfo.Builder(context, appInfo.packageName);
            shortcut.setShortLabel(appInfo.appName);
            shortcut.setLongLabel(appInfo.appName);

            shortcut.setIcon(
                    Icon.createWithBitmap(drawableToBitmap(LocalImageLoader.getDrawable(context, appInfo))));

            Intent intent = new Intent(context, AppPermissionActivity.class);
            intent.putExtra(AppPermissionActivity.EXTRA_APP_PKGNAME, appInfo.packageName);
            intent.putExtra(AppPermissionActivity.EXTRA_APP_NAME, appInfo.appName);
            intent.setAction(Intent.ACTION_DEFAULT);
            shortcut.setIntent(intent);

            shortcutInfoList.add(shortcut.build());
        }
        shortcutManager.setDynamicShortcuts(shortcutInfoList);
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public static Single<AppInfo> getAppInfo(final Context context, String pkgName) {
        return SingleJust.just(pkgName)
                .map(new Function<String, AppInfo>() {
                    @Override
                    public AppInfo apply(String s) throws Exception {
                        PackageManager packageManager = context.getPackageManager();
                        PackageInfo packageInfo = packageManager.getPackageInfo(s, 0);

                        AppInfo info = new AppInfo();
                        info.packageName = packageInfo.packageName;
                        info.appName = BidiFormatter.getInstance()
                                .unicodeWrap(packageInfo.applicationInfo.loadLabel(packageManager)).toString();
                        info.time = Math.max(packageInfo.lastUpdateTime, packageInfo.firstInstallTime);
                        info.installTime = packageInfo.firstInstallTime;
                        info.updateTime = packageInfo.lastUpdateTime;
                        info.applicationInfo = packageInfo.applicationInfo;

                        LocalImageLoader.initAdd(context, info);
                        return info;
                    }
                });
    }

    public static Observable<List<AppInfo>> getInstalledApps(final Context context,
                                                             final boolean loadSysapp) {

        return Observable.create(new ObservableOnSubscribe<List<AppInfo>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<AppInfo>> e) throws Exception {
                PackageManager packageManager = context.getPackageManager();
                int uid = Users.getInstance().getCurrentUid();
                List<PackageInfo> installedPackages = null;
                installedPackages = ShizukuManager.getInstance(context).getInstalledPackages(0, uid);

                List<AppInfo> ret = new ArrayList<AppInfo>();
                for (PackageInfo installedPackage : installedPackages) {
                    if (loadSysapp
                            || (installedPackage.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        AppInfo info = new AppInfo();
                        info.packageName = installedPackage.packageName;
                        info.appName = BidiFormatter.getInstance()
                                .unicodeWrap(installedPackage.applicationInfo.loadLabel(packageManager)).toString();
                        info.time = Math
                                .max(installedPackage.lastUpdateTime, installedPackage.firstInstallTime);
                        info.installTime = installedPackage.firstInstallTime;
                        info.updateTime = installedPackage.lastUpdateTime;
                        info.applicationInfo = installedPackage.applicationInfo;
                        LocalImageLoader.initAdd(context, info);
                        //some of the app name is empty.
                        if (TextUtils.isEmpty(info.appName)) {
                            info.appName = info.packageName;
                        }
                        ret.add(info);
                    }
                }
                e.onNext(ret);
                e.onComplete();
            }
        });
    }

    // For exporting to the backup file
    public static Observable<PreAppInfo> getAppsPermission(final Context context, AppInfo[] appInfos) {
        return Observable.fromArray(appInfos)
                .map(new Function<AppInfo, List<PackageOps>>() {
                    @Override
                    public List<PackageOps> apply(@NonNull AppInfo _appInfo) throws Exception {
                        return ShizukuManager.getInstance(context).getOpsForPackage(_appInfo.packageName);
                    }
                })
                .retry(5, new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        return throwable instanceof IOException || throwable instanceof NullPointerException;
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::fromIterable)
                .map(packageOpsAddAlwaysShownPerm(context))
                .map(new Function<PackageOps, PreAppInfo>() {
                    @Override
                    public PreAppInfo apply(PackageOps packageOps) throws Exception {
                        PreAppInfo appInfo = new PreAppInfo(packageOps.getPackageName());
                        StringBuilder allowedOpsSb = new StringBuilder();
                        StringBuilder ignoredOpsSb = new StringBuilder();
                        StringBuilder erroredOpsSb = new StringBuilder();
                        StringBuilder defaultOpsSb = new StringBuilder();
                        StringBuilder foregroundOpsSb = new StringBuilder();
                        List<OpEntry> ops = packageOps.getOps();
                        if (ops != null) {
                            for (OpEntry op : ops) {
                                switch (op.getMode()) {
                                    case AppOpsMode.MODE_ALLOWED:
                                        allowedOpsSb.append(op.getOp()).append(',');
                                        break;
                                    case AppOpsMode.MODE_IGNORED:
                                        ignoredOpsSb.append(op.getOp()).append(',');
                                        break;
                                    case AppOpsMode.MODE_ERRORED:
                                        erroredOpsSb.append(op.getOp()).append(',');
                                        break;
                                    case AppOpsMode.MODE_DEFAULT:
                                        defaultOpsSb.append(op.getOp()).append(',');
                                        break;
                                    case AppOpsMode.MODE_FOREGROUND:
                                        foregroundOpsSb.append(op.getOp()).append(',');
                                        break;
                                }
                            }
                        }
                        int allowedLen = allowedOpsSb.length();
                        if (allowedLen > 0 && allowedOpsSb.charAt(allowedLen - 1) == ',') {
                            allowedOpsSb.deleteCharAt(allowedLen - 1);
                        }
                        int ignoredLen = ignoredOpsSb.length();
                        if (ignoredLen > 0 && ignoredOpsSb.charAt(ignoredLen - 1) == ',') {
                            ignoredOpsSb.deleteCharAt(ignoredLen - 1);
                        }
                        int errorLen = erroredOpsSb.length();
                        if (errorLen > 0 && erroredOpsSb.charAt(errorLen - 1) == ',') {
                            erroredOpsSb.deleteCharAt(errorLen - 1);
                        }
                        int defaultLen = defaultOpsSb.length();
                        if (defaultLen > 0 && defaultOpsSb.charAt(defaultLen - 1) == ',') {
                            defaultOpsSb.deleteCharAt(defaultLen - 1);
                        }
                        int foregroundLen = foregroundOpsSb.length();
                        if (foregroundLen > 0 && foregroundOpsSb.charAt(foregroundLen - 1) == ',') {
                            foregroundOpsSb.deleteCharAt(foregroundLen - 1);
                        }
                        appInfo.setAllowedOps(allowedOpsSb.toString());
                        appInfo.setIgnoredOps(ignoredOpsSb.toString());
                        appInfo.setErroredOps(erroredOpsSb.toString());
                        appInfo.setDefaultOps(defaultOpsSb.toString());
                        appInfo.setForegroundOps(foregroundOpsSb.toString());
                        return appInfo;
                    }
                });
    }

    public static Observable<List<OpEntryInfo>> getAppPermission(final Context context,
                                                                 final String packageName) {
        return getAppPermission(context, packageName, false);
    }


    public static Observable<List<OpEntryInfo>> getAppPermission(final Context context,
                                                                 final String packageName, final boolean alwaysShownPerm) {
        return Observable
                .create(new ObservableOnSubscribe<List<PackageOps>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<PackageOps>> e) throws Exception {
                        List<PackageOps> opsForPackage = ShizukuManager.getInstance(context).getOpsForPackage(packageName);
                        e.onNext(opsForPackage);
                        e.onComplete();
                    }
                })
                .retry(5, new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        return throwable instanceof IOException || throwable instanceof NullPointerException;
                    }
                })
                .flatMap(Observable::fromIterable)
                .compose(new ObservableTransformer<PackageOps, PackageOps>() {
                    @Override
                    public ObservableSource<PackageOps> apply(Observable<PackageOps> upstream) {
                        if (alwaysShownPerm) {
                            return upstream.map(packageOpsAddAlwaysShownPerm(context));
                        } else {
                            return upstream;
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(packageOpsToOpEntryInfo(context))
                .map(new Function<List<OpEntryInfo>, List<OpEntryInfo>>() {
                    @Override
                    public List<OpEntryInfo> apply(@NonNull List<OpEntryInfo> opEntryInfos) throws Exception {
                        return sortPermsFunction(context, opEntryInfos);
                    }
                });
    }

    private static Function<PackageOps, PackageOps> packageOpsAddAlwaysShownPerm(final Context context) {
        return new Function<PackageOps, PackageOps>() {
            @Override
            public PackageOps apply(PackageOps packageOps) throws Exception {
                SparseIntArray existedOps = new SparseIntArray();
                List<OpEntry> opEntries = packageOps.getOps();
                for (OpEntry op : opEntries) {
                    existedOps.put(op.getOp(), op.getOp());
                }
                for (int i = 0; i < ALWAYS_SHOWN_OP.size(); i++) {
                    int opInt = ALWAYS_SHOWN_OP.keyAt(i);
                    if (existedOps.indexOfKey(opInt) < 0) {
                        try {
                            int mode = ShizukuManager.getInstance(context).checkOperation(opInt, packageOps.getPackageName());
                            OpEntry op = new OpEntry(opInt, mode, 0, 0, 0, 0, null);
                            opEntries.add(op);
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
                return packageOps;
            }
        };
    }

    private static Function<PackageOps, List<OpEntryInfo>> packageOpsToOpEntryInfo(final Context context) {
        return new Function<PackageOps, List<OpEntryInfo>>() {
            @Override
            public List<OpEntryInfo> apply(PackageOps packageOps) throws Exception {
                PackageManager pm = context.getPackageManager();
                List<OpEntry> ops = packageOps.getOps();
                List<OpEntryInfo> opEntryInfoList = new ArrayList<>();
                if (ops != null) {
                    SparseIntArray hasOp = new SparseIntArray();
                    for (OpEntry op : ops) {
                        OpEntryInfo opEntryInfo = opEntry2Info(op, context, pm);
                        if (opEntryInfo != null) {
                            hasOp.put(op.getOp(), op.getOp());
                            opEntryInfoList.add(opEntryInfo);
                        }
                    }
                }
                return opEntryInfoList;
            }
        };
    }

    private static OpEntryInfo opEntry2Info(OpEntry op, Context context, PackageManager pm) {
        OpEntryInfo opEntryInfo = new OpEntryInfo(op);
        /*
        if (OtherOp.isOtherOp(op.getOp())) {
            opEntryInfo.opName = OtherOp.getOpName(op.getOp());
            opEntryInfo.opPermsName = OtherOp.getOpPermName(op.getOp());
        }
         */
        if (opEntryInfo.opEntry != null) {
            try {
                PermissionInfo permissionInfo = pm.getPermissionInfo(opEntryInfo.opPermsName, 0);
                opEntryInfo.opPermsLab = String.valueOf(permissionInfo.loadLabel(pm));
                opEntryInfo.opPermsDesc = String.valueOf(permissionInfo.loadDescription(pm));
            } catch (PackageManager.NameNotFoundException e) {
                //ignore
            }

            if (opEntryInfo.opPermsLab == null) {
                Integer resId = sPermI18N.get(opEntryInfo.opName);
                if (resId != null) {
                    opEntryInfo.opPermsLab = context.getString(resId);
                    opEntryInfo.opPermsDesc = opEntryInfo.opName;
                }
            }

            return opEntryInfo;
        }
        return null;
    }

    static Observable<AppPermissions> getAllAppPermissions(final Context context, final boolean loadSysapp) {
        return getAllAppPermissions(context, loadSysapp, false);
    }

    static Observable<AppPermissions> getAllAppPermissions(final Context context, final boolean loadSysapp, final boolean alwaysShownPerm) {
        return Observable
                .create(new ObservableOnSubscribe<List<PackageOps>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<PackageOps>> e) throws Exception {
                        List<PackageOps> opsForPackage = ShizukuManager.getInstance(context).getPackagesForOps(null);
                        e.onNext(opsForPackage);
                        e.onComplete();
                    }
                })
                .retry(5, new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        return throwable instanceof IOException || throwable instanceof NullPointerException;
                    }
                })
                .flatMap(Observable::fromIterable)
                .compose(new ObservableTransformer<PackageOps, PackageOps>() {
                    @Override
                    public ObservableSource<PackageOps> apply(Observable<PackageOps> upstream) {
                        if (alwaysShownPerm) {
                            return upstream.map(packageOpsAddAlwaysShownPerm(context));
                        } else {
                            return upstream;
                        }
                    }
                })
                .toList()
                .toObservable()
                .map(new Function<List<PackageOps>, Map<String, PackageOps>>() {
                    @Override
                    public Map<String, PackageOps> apply(List<PackageOps> result) throws Exception {
                        Map<String, PackageOps> map = new HashMap<>();
                        for (PackageOps packageOps : result) {
                            map.put(packageOps.getPackageName(), packageOps);
                        }
                        return map;
                    }
                })
                .flatMap(new Function<Map<String, PackageOps>, ObservableSource<List<AppPermissions>>>() {
                    public ObservableSource<List<AppPermissions>> apply(final Map<String, PackageOps> result) throws Exception {
                        return getInstalledApps(context, loadSysapp).map(
                                new Function<List<AppInfo>, List<AppPermissions>>() {
                                    @Override
                                    public List<AppPermissions> apply(List<AppInfo> appInfos) throws Exception {
                                        List<AppPermissions> list = new ArrayList<AppPermissions>();
                                        PackageManager pm = context.getPackageManager();
                                        if (appInfos != null) {
                                            for (AppInfo appInfo : appInfos) {
                                                AppPermissions p = new AppPermissions();
                                                p.appInfo = appInfo;
                                                PackageOps packageOps = result.get(appInfo.packageName);
                                                if (packageOps != null) {
                                                    List<OpEntry> ops = packageOps.getOps();
                                                    if (ops != null) {
                                                        List<OpEntryInfo> opEntryInfos = new ArrayList<>();
                                                        SparseIntArray hasOp = new SparseIntArray();
                                                        for (OpEntry op : ops) {
                                                            OpEntryInfo opEntryInfo = opEntry2Info(op, context, pm);
                                                            if (opEntryInfo != null) {
                                                                hasOp.put(op.getOp(), op.getOp());
                                                                opEntryInfos.add(opEntryInfo);
                                                            }
                                                        }
                                                        p.opEntries = opEntryInfos;
                                                        list.add(p);
                                                    }
                                                }
                                            }
                                        }
                                        return list;
                                    }
                                });
                    }
                })
                .flatMap(Observable::fromIterable);
    }


    public static Single<List<Pair<AppInfo, OpEntryInfo>>> getPermsUsageStatus(final Context context,
                                                                               final boolean loadSysapp) {
        return getAllAppPermissions(context, loadSysapp)
                .collect(new Callable<List<Pair<AppInfo, OpEntryInfo>>>() {
                             @Override
                             public List<Pair<AppInfo, OpEntryInfo>> call() throws Exception {
                                 return new ArrayList<Pair<AppInfo, OpEntryInfo>>();
                             }
                         }
                        , new BiConsumer<List<Pair<AppInfo, OpEntryInfo>>, AppPermissions>() {
                            @Override
                            public void accept(List<Pair<AppInfo, OpEntryInfo>> pairs, AppPermissions appPermissions) {
                                if (appPermissions.opEntries != null) {
                                    for (OpEntryInfo opEntry : appPermissions.opEntries) {
                                        //被调用过并且允许的才加入列表
                                        //超过一个月的记录不显示
                                        long time = opEntry.opEntry.getTime();
                                        long now = System.currentTimeMillis();
                                        if (time > 0 && (now - time < 60 * 60 * 24 * 31 * 1000L) && opEntry.isAllowOrForeground()) {
                                            joinOpEntryInfo(opEntry, context);
                                            pairs.add(Pair.create(appPermissions.appInfo, opEntry));
                                        }
                                    }
                                }
                            }
                        })
                .flatMapObservable(new Function<List<Pair<AppInfo, OpEntryInfo>>, ObservableSource<Pair<AppInfo, OpEntryInfo>>>() {
                                       @Override
                                       public ObservableSource<Pair<AppInfo, OpEntryInfo>> apply(@NonNull List<Pair<AppInfo, OpEntryInfo>> pairs)
                                               throws Exception {
                                           return Observable.fromIterable(pairs);
                                       }
                                   }
                ).toSortedList(new Comparator<Pair<AppInfo, OpEntryInfo>>() {
                    @Override
                    public int compare(Pair<AppInfo, OpEntryInfo> t0,
                                       Pair<AppInfo, OpEntryInfo> t1) {
                        return Long.compare(t1.second.opEntry.getTime(), t0.second.opEntry.getTime());
                    }
                });
    }

    public static Single<List<PermissionGroup>> getPermissionGroup(final Context context,
                                                                   final boolean loadSysapp, final boolean showIgnored, final boolean alwaysShownPerm) {
        return getAllAppPermissions(context, loadSysapp, alwaysShownPerm)
                .collect(new Callable<Map<Integer, List<AppPermissions>>>() {
                             @Override
                             public Map<Integer, List<AppPermissions>> call() throws Exception {
                                 return new HashMap<>();
                             }
                         }
                        , new BiConsumer<Map<Integer, List<AppPermissions>>, AppPermissions>() {
                            @Override
                            public void accept(Map<Integer, List<AppPermissions>> map, AppPermissions app)
                                    throws Exception {
                                if (app.opEntries != null && app.hasPermissions()) {
                                    for (OpEntryInfo opEntryInfo : app.opEntries) {
                                        if (opEntryInfo.opEntry != null) {
                                            List<AppPermissions> appPermissionses = map.get(opEntryInfo.opEntry.getOp());
                                            if (appPermissionses == null) {
                                                appPermissionses = new ArrayList<>();
                                            }
                                            appPermissionses.add(app);
                                            map.put(opEntryInfo.opEntry.getOp(), appPermissionses);
                                        }
                                    }
                                }
                            }
                        })
                .map(new Function<Map<Integer, List<AppPermissions>>, List<PermissionGroup>>() {
                    @Override
                    public List<PermissionGroup> apply(Map<Integer, List<AppPermissions>> map)
                            throws Exception {
                        List<PermissionGroup> groups = new ArrayList<PermissionGroup>();
                        Set<Entry<Integer, List<AppPermissions>>> entries = map.entrySet();
                        for (Entry<Integer, List<AppPermissions>> entry : entries) {
                            PermissionGroup group = new PermissionGroup();
                            group.opInt = entry.getKey();
                            List<AppPermissions> value = entry.getValue();
                            group.count = value.size();
                            group.apps = new ArrayList<PermissionChildItem>();
                            for (AppPermissions appPermissions : value) {
                                PermissionChildItem item = new PermissionChildItem();
                                item.appInfo = appPermissions.appInfo;
                                boolean skip = false;
                                if (appPermissions.opEntries != null) {
                                    for (OpEntryInfo opEntryInfo : appPermissions.opEntries) {
                                        if (group.opInt == opEntryInfo.opEntry.getOp()) {
                                            item.opEntryInfo = opEntryInfo;
                                            if (opEntryInfo.opEntry.getMode() == AppOpsMode.MODE_ALLOWED
                                                    || opEntryInfo.opEntry.getMode() == AppOpsMode.MODE_FOREGROUND) {
                                                group.grants += 1;
                                            } else if (!showIgnored) {
                                                skip = true;
                                            }
                                            group.opName = opEntryInfo.opName;
                                            group.opPermsName = opEntryInfo.opPermsName;
                                            group.opPermsDesc = opEntryInfo.opPermsDesc;
                                            group.opPermsLab = opEntryInfo.opPermsLab;
                                            break;
                                        }
                                    }
                                }
                                if (!skip) {
                                    group.apps.add(item);
                                }
                            }
                            Collections.sort(group.apps, new Comparator<PermissionChildItem>() {
                                @Override
                                public int compare(PermissionChildItem o1, PermissionChildItem o2) {
                                    return Long.compare(o2.opEntryInfo.opEntry.getTime(), o1.opEntryInfo.opEntry.getTime());
                                }
                            });
                            groups.add(group);
                        }
                        return groups;
                    }
                })
                .map(new Function<List<PermissionGroup>, List<PermissionGroup>>() {
                    @Override
                    public List<PermissionGroup> apply(List<PermissionGroup> permissionGroups)
                            throws Exception {
                        Map<String, List<PermissionGroup>> groups = new HashMap<String, List<PermissionGroup>>();
                        PackageManager pm = context.getPackageManager();
                        for (PermissionGroup permissionGroup : permissionGroups) {
                            String groupS = AppOps.OP_PERMISSION_GROUP_MAP.get(permissionGroup.opInt);
                            if (groupS == null && permissionGroup.opPermsName != null) {
                                try {
                                    PermissionInfo permissionInfo = pm
                                            .getPermissionInfo(permissionGroup.opPermsName, PackageManager.GET_META_DATA);
                                    groupS = permissionInfo.group;
                                } catch (Exception e) {
                                    //ignore
                                }
                            }
                            PermGroupInfo permGroupInfo = null;
                            if (groupS != null) {
                                permGroupInfo = PERMS_GROUPS.get(groupS);
                            }
                            if (permGroupInfo == null) {
                                permGroupInfo = OTHER_PERM_INFO;
                            }
                            permissionGroup.icon = permGroupInfo.icon;
                            permissionGroup.group = permGroupInfo.group;
                            List<PermissionGroup> value = groups.get(permissionGroup.group);
                            if (value == null) {
                                value = new ArrayList<PermissionGroup>();
                            }
                            value.add(permissionGroup);

                            groups.put(permissionGroup.group, value);
                        }
                        return reSort(AppOps.PERMISSION_GROUP_ORDER, groups);
                    }
                });
    }


    private static List<PermissionGroup> reSort(List<String> groupNames,
                                                Map<String, List<PermissionGroup>> groups) {
        List<PermissionGroup> ret = new LinkedList<PermissionGroup>();
        for (String groupName : groupNames) {
            List<PermissionGroup> permissionGroups = groups.get(groupName);
            if (permissionGroups != null) {
                ret.addAll(permissionGroups);
            }
        }
        return ret;
    }

    public static Completable setMode(final Context context, final String pkgName, final OpEntryInfo opEntryInfo) {
        return Completable
                .create(new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(CompletableEmitter e) throws Exception {
                        ShizukuManager.getInstance(context).setOpsMode(pkgName, opEntryInfo.opEntry.getOp(), opEntryInfo.mode);
                        e.onComplete();
                    }
                })
                .retry(5, new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        return throwable instanceof IOException || throwable instanceof NullPointerException;
                    }
                });
    }


    public static Completable setModes(final Context context, final String pkgName, final int opMode, List<Integer> ops) {
        return Observable.fromIterable(ops)
                .flatMapCompletable(new Function<Integer, CompletableSource>() {
                    @Override
                    public CompletableSource apply(Integer integer) throws Exception {
                        ShizukuManager.getInstance(context).setOpsMode(pkgName, integer, opMode);
                        return Completable.complete();
                    }
                })
                .retry(5, new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        return throwable instanceof IOException
                                || throwable instanceof NullPointerException;
                    }
                });
    }

    public static Completable resetMode(final Context context, final String pkgName) {
        return Completable
                .create(new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(CompletableEmitter e) throws Exception {
                        ShizukuManager.getInstance(context).resetAllModes(pkgName);
                        e.onComplete();
                    }
                })
                .retry(5, new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        return throwable instanceof IOException || throwable instanceof NullPointerException;
                    }
                });
    }

    public static Single<SparseIntArray> autoDisable(final Context context, String pkg) {

        return SingleJust.just(pkg).map(new Function<String, SparseIntArray>() {
            @Override
            public SparseIntArray apply(String s) throws Exception {

                List<OpEntryInfo> opEntryInfos = getAppPermission(context, s).blockingFirst();

                SparseIntArray canIgnored = new SparseIntArray();//可以忽略的op
                if (opEntryInfos != null && !opEntryInfos.isEmpty()) {
                    for (OpEntryInfo opEntryInfo : opEntryInfos) {
                        int op = opEntryInfo.opEntry.getOp();
                        canIgnored.put(op, op);
                    }
                }

                SparseIntArray list = new SparseIntArray();
                SparseIntArray allowedIgnoreOps = getAllowedIgnoreOps(context);

                if (allowedIgnoreOps != null && allowedIgnoreOps.size() > 0) {
                    int size = allowedIgnoreOps.size();
                    for (int i = 0; i < size; i++) {
                        int op = allowedIgnoreOps.keyAt(i);
                        if (canIgnored.indexOfKey(op) >= 0 || ALWAYS_SHOWN_OP.indexOfKey(op) >= 0) {
                            //
                            list.put(op, op);
                        }
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    try {
                        int op = list.keyAt(i);
                        ShizukuManager.getInstance(context).setOpsMode(s, op, AppOpsMode.MODE_IGNORED);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
                return list;
            }
        });
//        return SingleJust.create(new SingleOnSubscribe<SparseIntArray>() {
//            @Override
//            public void subscribe(SingleEmitter<SparseIntArray> e) throws Exception {
//                List<OpEntryInfo> opEntryInfos = getAppPermission(context, pkg).blockingFirst();
//
//                SparseIntArray canIgnored = new SparseIntArray();//可以忽略的op
//                if (opEntryInfos != null && !opEntryInfos.isEmpty()) {
//                    for (OpEntryInfo opEntryInfo : opEntryInfos) {
//                        int op = opEntryInfo.opEntry.getOp();
//                        canIgnored.put(op, op);
//                    }
//                }
//
//
//                SparseIntArray list = new SparseIntArray();
//                SparseIntArray allowedIgnoreOps = getAllowedIgnoreOps(context);
//
//                if (allowedIgnoreOps != null && allowedIgnoreOps.size() > 0) {
//                    int size = allowedIgnoreOps.size();
//                    for (int i = 0; i < size; i++) {
//                        int op = allowedIgnoreOps.keyAt(i);
//                        if (canIgnored.indexOfKey(op) >= 0 || NO_PERM_OP.indexOfKey(op) >= 0) {
//                            //
//                            list.put(op, op);
//                        }
//                    }
//                }
//                for (int i = 0; i < list.size(); i++) {
//                    try {
//                        int op = list.keyAt(i);
//                        ShizukuManager.getInstance(context).setOpsMode(pkg, op, AppOpsManager.MODE_IGNORED);
//                    } catch (Exception ee) {
//                        ee.printStackTrace();
//                    }
//                }
//                e.onSuccess(list);
//            }
//        });
    }


    private static final SparseArray<OpEntryInfo> sOpEntryInfo = new SparseArray<>();
    private static final SparseIntArray sAllOps = new SparseIntArray();
    private static final List<OpEntryInfo> sOpEntryInfoList = new ArrayList<>();

    public static List<OpEntryInfo> getLocalOpEntryInfos(Context context) {
        if (sOpEntryInfoList.isEmpty()) {
            List<Integer> sOpToSwitch = AppOps.sOpToSwitch;
            List<String> sOpNames = AppOps.sOpNames;
            List<String> sOpPerms = AppOps.sOpPerms;
            int len = sOpPerms.size();
            PackageManager pm = context.getPackageManager();
            for (int i = 0; i < len; i++) {
                OpEntry entry = new OpEntry(sOpToSwitch.get(i), AppOpsMode.MODE_ALLOWED, 0, 0, 0, 0, null);
                OpEntryInfo opEntryInfo = new OpEntryInfo(entry);
                opEntryInfo.opName = sOpNames.get(i);
                try {
                    PermissionInfo permissionInfo = pm.getPermissionInfo(sOpPerms.get(i), 0);
                    opEntryInfo.opPermsLab = String.valueOf(permissionInfo.loadLabel(pm));
                    opEntryInfo.opPermsDesc = String.valueOf(permissionInfo.loadDescription(pm));
                } catch (PackageManager.NameNotFoundException e) {
                    //ignore
                    Integer resId = sPermI18N.get(opEntryInfo.opName);
                    if (resId != null) {
                        opEntryInfo.opPermsLab = context.getString(resId);
                        opEntryInfo.opPermsDesc = opEntryInfo.opName;
                    } else {
                        opEntryInfo.opPermsLab = opEntryInfo.opName;
                    }
                }
                sOpEntryInfo.put(entry.getOp(), opEntryInfo);
                sAllOps.put(entry.getOp(), entry.getOp());
                sOpEntryInfoList.add(opEntryInfo);
            }
        }
        return new ArrayList<OpEntryInfo>(sOpEntryInfoList);
    }


    public static SparseIntArray getAllowedIgnoreOps(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String result = sp.getString("auto_perm_templete", context.getString(R.string.default_ignored));
        SparseIntArray ret = new SparseIntArray();
        String[] split = result.split(",");
        for (String s : split) {
            try {
                int op = Integer.parseInt(s);
                ret.put(op, op);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static Function<List<AppInfo>, List<AppInfo>> getSortComparator(final Context context) {
        return new Function<List<AppInfo>, List<AppInfo>>() {
            @Override
            public List<AppInfo> apply(List<AppInfo> appInfos) throws Exception {
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

                return appInfos;
            }
        };
    }


    public static List<OpEntryInfo> sortPermsFunction(Context context,
                                                      List<OpEntryInfo> opEntryInfos) {
        //resort
        Map<String, List<OpEntryInfo>> sMap = new HashMap<>();
        for (OpEntryInfo opEntryInfo : opEntryInfos) {
            if (opEntryInfo != null) {
                joinOpEntryInfo(opEntryInfo, context);
                List<OpEntryInfo> infos = sMap.get(opEntryInfo.groupName);
                if (infos == null) {
                    infos = new ArrayList<>();
                    sMap.put(opEntryInfo.groupName, infos);
                }
                infos.add(opEntryInfo);
            }
        }

        List<OpEntryInfo> infoList = new ArrayList<OpEntryInfo>();
        for (String string : AppOps.PERMISSION_GROUP_ORDER) {
            List<OpEntryInfo> infos = sMap.get(string);
            if (infos != null) {
                infoList.addAll(infos);
            }
        }

        return infoList;
    }

    private static void joinOpEntryInfo(OpEntryInfo opEntryInfo, Context context) {
        String groupS = AppOps.OP_PERMISSION_GROUP_MAP.get(opEntryInfo.opEntry.getOp());
        try {
            if (groupS == null && opEntryInfo.opPermsName != null) {
                PermissionInfo permissionInfo = context.getPackageManager()
                        .getPermissionInfo(opEntryInfo.opPermsName, PackageManager.GET_META_DATA);
                groupS = permissionInfo.group;
            }
        } catch (Exception e) {
            //ignore
        }
        PermGroupInfo permGroupInfo = null;
        if (groupS != null) {
            permGroupInfo = PERMS_GROUPS.get(groupS);
        }

        if (permGroupInfo == null) {
            permGroupInfo = OTHER_PERM_INFO;
        }
        opEntryInfo.icon = permGroupInfo.icon;
        opEntryInfo.groupName = permGroupInfo.group;

    }

    public static Single<List<OpEntryInfo>> groupByMode(final Context context,
                                                        List<OpEntryInfo> list) {

        return Observable.fromIterable(list)
                .collect(new Callable<List<OpEntryInfo>[]>() {
                             @Override
                             public List<OpEntryInfo>[] call() throws Exception {
                                 return new List[2];
                             }
                         }
                        , new BiConsumer<List<OpEntryInfo>[], OpEntryInfo>() {
                            @Override
                            public void accept(List<OpEntryInfo>[] lists, OpEntryInfo opEntryInfo) throws Exception {
                                if (opEntryInfo != null) {
                                    int idx = opEntryInfo.mode == AppOpsMode.MODE_ALLOWED ? 0 : 1;
                                    List<OpEntryInfo> list = lists[idx];
                                    if (list == null) {
                                        list = new ArrayList<OpEntryInfo>();
                                        lists[idx] = list;
                                    }
                                    list.add(opEntryInfo);
                                }
                            }
                        })
                .map(new Function<List<OpEntryInfo>[], List<OpEntryInfo>>() {
                    @Override
                    public List<OpEntryInfo> apply(@NonNull List<OpEntryInfo>[] lists) throws Exception {

                        List<OpEntryInfo> ret = new ArrayList<OpEntryInfo>();
                        if (lists != null) {
                            for (List<OpEntryInfo> list : lists) {
                                if (list != null) {
                                    ret.addAll(Helper.sortPermsFunction(context, list));
                                }
                            }
                        }
                        return ret;
                    }
                });
    }


    public static Single<List<UserInfo>> getUsers(final Context context, final boolean excludeDying) {
        return Single.create(new SingleOnSubscribe<List<UserInfo>>() {
            @Override
            public void subscribe(SingleEmitter<List<UserInfo>> emitter) throws Exception {
                emitter.onSuccess(ShizukuManager.getInstance(context).getUsers(excludeDying));
            }
        });
    }

}
