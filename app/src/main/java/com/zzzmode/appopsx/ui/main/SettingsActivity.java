package com.zzzmode.appopsx.ui.main;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.zzzmode.appopsx.BuildConfig;
import com.zzzmode.appopsx.R;
import com.zzzmode.appopsx.ui.BaseActivity;
import com.zzzmode.appopsx.ui.core.Helper;
import com.zzzmode.appopsx.ui.core.LangHelper;
import com.zzzmode.appopsx.ui.model.OpEntryInfo;
import com.zzzmode.appopsx.ui.widget.NumberPickerPreference;

import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.single.SingleJust;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

/**
 * Created by zl on 2017/1/16.
 */

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.menu_setting);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flag_container, new MyPreferenceFragment()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public static class MyPreferenceFragment extends PreferenceFragmentCompat implements
            Preference.OnPreferenceClickListener {

        private Preference mPrefAppSort;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);

            findPreference("ignore_premission").setOnPreferenceClickListener(this);
            findPreference("show_sysapp").setOnPreferenceClickListener(this);

            findPreference("project").setOnPreferenceClickListener(this);

            findPreference("opensource_licenses").setOnPreferenceClickListener(this);
            findPreference("help").setOnPreferenceClickListener(this);
            findPreference("translate").setOnPreferenceClickListener(this);

            Preference version = findPreference("version");
            version.setSummary(BuildConfig.VERSION_NAME);
            version.setOnPreferenceClickListener(this);

            Preference appLanguage = findPreference("pref_app_language");
            appLanguage.setOnPreferenceClickListener(this);
            appLanguage.setSummary(
                    getResources().getStringArray(R.array.languages)[LangHelper.getLocalIndex(getContext())]);

            findPreference("acknowledgments")
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            StringBuilder sb = new StringBuilder();
                            String[] stringArray = getResources().getStringArray(R.array.acknowledgments_list);
                            for (String s : stringArray) {
                                sb.append(s).append('\n');
                            }
                            sb.deleteCharAt(sb.length() - 1);
                            showTextDialog(R.string.acknowledgments_list, sb.toString());
                            return true;
                        }
                    });

            findPreference("ignore_premission_templete")
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            showPremissionTemplete();
                            return true;
                        }
                    });

            mPrefAppSort = findPreference("pref_app_sort_type");
            mPrefAppSort.setSummary(getString(R.string.app_sort_type_summary,
                    getResources().getStringArray(R.array.app_sort_type)[PreferenceManager
                            .getDefaultSharedPreferences(getActivity()).getInt(mPrefAppSort.getKey(), 0)]));
            mPrefAppSort.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showAppSortDialog(preference);
                    return true;
                }
            });

            findPreference("show_about")
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            showAbout();
                            return true;
                        }
                    });

            findPreference("pref_app_daynight_mode")
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {

                            return true;
                        }
                    });
        }


        private void setAllPreferencesToAvoidHavingExtraSpace(Preference preference) {
            if (preference != null) {
                preference.setIconSpaceReserved(false);
                if (preference instanceof PreferenceGroup) {
                    PreferenceGroup group = ((PreferenceGroup) preference);
                    int count = group.getPreferenceCount();
                    for (int i = 0; i < count; i++) {
                        setAllPreferencesToAvoidHavingExtraSpace(group.getPreference(i));
                    }
                }
            }
        }

        @Override
        public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
            super.setPreferenceScreen(preferenceScreen);
            setAllPreferencesToAvoidHavingExtraSpace(preferenceScreen);
        }

        @SuppressLint("RestrictedApi")
        @Override
        protected Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
            return new PreferenceGroupAdapter(preferenceScreen) {
                @SuppressLint("RestrictedApi")
                public void onPreferenceHierarchyChange(Preference preference) {
                    setAllPreferencesToAvoidHavingExtraSpace(preference);
                    super.onPreferenceHierarchyChange(preference);
                }
            };
        }

        @Override
        public void onDisplayPreferenceDialog(Preference preference) {
            if (preference instanceof NumberPickerPreference) {
                DialogFragment fragment = NumberPickerPreference.
                        NumberPickerPreferenceDialogFragmentCompat.newInstance(preference.getKey());
                fragment.setTargetFragment(this, 0);
                fragment.show(getFragmentManager(),
                        "NumberPickerPreferenceDialogFragment");
            } else {
                super.onDisplayPreferenceDialog(preference);
            }
        }

        private void showPremissionTemplete() {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.auto_ignore_permission_title);
            List<OpEntryInfo> localOpEntryInfos = Helper.getLocalOpEntryInfos(getActivity());
            int size = localOpEntryInfos.size();
            CharSequence[] items = new CharSequence[size];

            boolean[] selected = new boolean[size];

            for (int i = 0; i < size; i++) {
                OpEntryInfo opEntryInfo = localOpEntryInfos.get(i);
                items[i] = opEntryInfo.opPermsLab;
                selected[i] = false; //默认关闭
            }

            initCheckd(selected);

            final SparseBooleanArray choiceResult = new SparseBooleanArray();
            for (int i = 0; i < selected.length; i++) {
                choiceResult.put(i, selected[i]);
            }

            saveChoice(choiceResult);

            builder
                    .setMultiChoiceItems(items, selected, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            choiceResult.put(which, isChecked);
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveChoice(choiceResult);
                }
            });
            builder.show();
        }

        private void initCheckd(boolean[] localChecked) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String result = sp
                    .getString("auto_perm_templete", getActivity().getString(R.string.default_ignored));
            String[] split = result.split(",");
            for (String s : split) {
                try {
                    int i = Integer.parseInt(s);
                    localChecked[i] = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void saveChoice(SparseBooleanArray choiceResult) {
            StringBuilder sb = new StringBuilder();
            int size = choiceResult.size();
            for (int i = 0; i < size; i++) {
                if (choiceResult.get(i)) {
                    sb.append(i).append(',');
                }
            }
            String s = sb.toString();
            if (!TextUtils.isEmpty(s)) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                sp.edit().putString("auto_perm_templete", s).apply();
            }
        }

        private void showTextDialog(int title, String text) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }

        private void showAppSortDialog(final Preference preference) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.app_sort_type_title);

            final int[] selected = new int[1];
            selected[0] = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getInt(preference.getKey(), 0);
            builder.setSingleChoiceItems(R.array.app_sort_type, selected[0],
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selected[0] = which;
                        }
                    });

            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                            .putInt(preference.getKey(), selected[0]).apply();
                    mPrefAppSort.setSummary(getString(R.string.app_sort_type_summary,
                            getResources().getStringArray(R.array.app_sort_type)[selected[0]]));
                }
            });
            builder.show();
        }

        private void showLanguageDialog(final Preference preference) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.app_language);
            final int[] selected = new int[1];
            int defSelected = LangHelper.getLocalIndex(getContext());
            builder.setSingleChoiceItems(R.array.languages, defSelected,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selected[0] = which;
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int index = selected[0];
                    String language;
                    if (index == 0) {
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                                .remove(preference.getKey()).apply();
                    } else {
                        language = getResources().getStringArray(R.array.languages_key)[index];
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                                .putString(preference.getKey(), language).apply();
                    }
                    preference.setSummary(getResources().getStringArray(R.array.languages)[index]);
                    switchLanguage();
                }
            });
            builder.show();
        }


        private void switchLanguage() {
            LangHelper.updateLanguage(getContext());
            LangHelper.updateLanguage(getContext().getApplicationContext());

            Intent it = new Intent(getActivity(), MainActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(it);
            getActivity().finish();
        }

        private void showAbout() {
            SingleJust.create(new SingleOnSubscribe<String>() {
                @Override
                public void subscribe(SingleEmitter<String> e) throws Exception {
                    Context context = getActivity();
                    StringBuilder sb = new StringBuilder();
                    try {
                        PackageInfo packageInfo = getActivity().getPackageManager()
                                .getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                        sb.append("GitCommitId: ").append(packageInfo.applicationInfo.metaData.getString("GIT_COMMIT_ID"));
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                    e.onSuccess(sb.toString());
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(String value) {
                            showTextDialog(R.string.show_about, value);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });

        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            Intent intent;
            switch (key) {
                case "opensource_licenses":
                    intent = new Intent(getContext(), HtmlActionActivity.class);
                    intent.putExtra(Intent.EXTRA_TITLE, preference.getTitle());
                    intent.putExtra(HtmlActionActivity.EXTRA_URL, "file:///android_res/raw/licenses.html");
                    getActivity().startActivity(intent);
                    break;
                case "help":
                    intent = new Intent(getContext(), HtmlActionActivity.class);
                    intent.putExtra(Intent.EXTRA_TITLE, preference.getTitle());
                    intent.putExtra(HtmlActionActivity.EXTRA_URL, "file:///android_res/raw/help.html");
                    getActivity().startActivity(intent);
                    break;
                case "pref_app_language":
                    showLanguageDialog(preference);
                    break;
                default:
                    return false;
            }
            return true;
        }
    }
}
