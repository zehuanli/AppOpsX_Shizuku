package com.zzzmode.appopsx.ui.main;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.zzzmode.appopsx.R;
import com.zzzmode.appopsx.ui.core.ShizukuManager;
import com.zzzmode.appopsx.ui.BaseActivity;
import com.zzzmode.appopsx.ui.core.Helper;
import com.zzzmode.appopsx.ui.core.LocalImageLoader;
import com.zzzmode.appopsx.ui.core.Users;
import com.zzzmode.appopsx.ui.main.backup.BackupActivity;
import com.zzzmode.appopsx.ui.main.group.PermissionGroupActivity;
import com.zzzmode.appopsx.ui.main.usagestats.PermsUsageStatsActivity;
import com.zzzmode.appopsx.ui.model.AppInfo;
import com.zzzmode.appopsx.ui.widget.CommonDivderDecorator;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "MainActivity";

    private static final String SHIZUKU_PERMISSION = "moe.shizuku.manager.permission.API_V23";
    private static final int SHIZUKU_REQUEST_CODE_PERMISSION_V3 = 1;

    private MainListAdapter adapter;

    private ProgressBar mProgressBar;
    private RecyclerView recyclerView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private SearchHandler mSearchHandler;

    private View containerApp, containerSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_name);

        Log.e(TAG, "onCreate --> ");
        mSearchHandler = new SearchHandler();

        mProgressBar = findViewById(R.id.progressBar);

        containerApp = findViewById(R.id.container_app);
        containerSearch = findViewById(R.id.container_search);

        mSearchHandler.initView(containerSearch);

        recyclerView = findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = findViewById(R.id.swiperefreshlayout);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new CommonDivderDecorator(getApplicationContext()));
        recyclerView.setItemAnimator(new RefactoredDefaultItemAnimator());

        adapter = new MainListAdapter();
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(false);
            }
        });

        if (checkSelfPermission(SHIZUKU_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            init();
        } else {
            requestPermissions(new String[]{SHIZUKU_PERMISSION}, SHIZUKU_REQUEST_CODE_PERMISSION_V3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SHIZUKU_REQUEST_CODE_PERMISSION_V3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Shizuku permission is required");
                builder.setPositiveButton("Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{SHIZUKU_PERMISSION}, SHIZUKU_REQUEST_CODE_PERMISSION_V3);
                    }
                });
                builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        }
    }

    private void init() {
        loadUsers();
//        loadData(true);
    }

    private void loadData(final boolean isFirst) {
        boolean showSysApp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("show_sysapp", false);
        Helper.getInstalledApps(getApplicationContext(), showSysApp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResourceObserver<AppInfo>() {
                    @Override
                    protected void onStart() {
                        super.onStart();
                        if (isFirst) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        adapter.clearItems();
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        adapter.addItem(appInfo);

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mProgressBar.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (isFirst) {
                            mSwipeRefreshLayout.setEnabled(true);
                        }
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                        invalidateOptionsMenu();
                    }

                    @Override
                    public void onComplete() {
                        adapter.sortItems(getApplicationContext());
                        mSearchHandler.setBaseData(adapter.getAppInfos());
                        mProgressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (isFirst) {
                            mSwipeRefreshLayout.setEnabled(true);
                        }

                        invalidateOptionsMenu();
                    }
                });
    }


    private void loadUsers() {
        Helper.getUsers(getApplicationContext(), true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<UserInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<UserInfo> userInfos) {
                        Users.getInstance().updateUsers(userInfos);
                        int selectedUserId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("user_id", 0);
                        for (UserInfo userInfo : Users.getInstance().getUsers()) {
                            if (userInfo.id == selectedUserId) {
                                switchUser(userInfo);
                                invalidateOptionsMenu();
                                return;
                            }
                        }
                        loadData(true);
                        invalidateOptionsMenu();
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadData(true);
                        invalidateOptionsMenu();
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                openSetting();
                return true;
            case R.id.action_permission_sort:
                openSortPermission();
                return true;
            case R.id.action_backup:
                openConfigPerms();
                return true;
            case R.id.action_stats:
                openUsageStats();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.ops_menu, menu);

        final MenuItem searchMenu = menu.findItem(R.id.action_search);
        final MenuItem settingsMenu = menu.findItem(R.id.action_setting);
        final MenuItem premsMenu = menu.findItem(R.id.action_permission_sort);

        menu.findItem(R.id.action_backup).setVisible(adapter != null && adapter.getItemCount() > 0);

        final Users users = Users.getInstance();
        if (users.isLoaded() && !users.getUsers().isEmpty()) {
            SubMenu userSub = menu.addSubMenu(R.id.action_users, Menu.NONE, Menu.NONE, R.string.action_users);
            userSub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
            OnMenuItemClickListener menuItemClickListener = new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    item.setChecked(true);
                    List<UserInfo> userInfos = Users.getInstance().getUsers();
                    for (UserInfo user : userInfos) {
                        if (user.id == item.getItemId() && users.getCurrentUid() != user.id) {
                            switchUser(user);
                            break;
                        }
                    }
                    return true;
                }
            };

            List<UserInfo> userInfos = users.getUsers();
            for (UserInfo user : userInfos) {
                MenuItem add = userSub.add(R.id.action_users, user.id, Menu.NONE, user.name);

                add.setCheckable(true);

                add.setChecked(user.id == users.getCurrentUid());

                add.setOnMenuItemClickListener(menuItemClickListener);
            }

            userSub.setGroupCheckable(R.id.action_users, true, true);

        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchMenu.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

        final View searchFrame = searchView.findViewById(androidx.appcompat.R.id.search_edit_frame);

        final int[] oldVisibility = {-1};

        searchFrame.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int currentVisibility = searchFrame.getVisibility();

                if (currentVisibility != oldVisibility[0]) {
                    if (currentVisibility == View.VISIBLE) {
                        containerApp.setVisibility(View.GONE);
                        containerSearch.setVisibility(View.VISIBLE);
                    } else {
                        containerApp.setVisibility(View.VISIBLE);
                        containerSearch.setVisibility(View.GONE);
                    }
                    oldVisibility[0] = currentVisibility;
                }

            }
        });

        return true;
    }

    private void openSetting() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void openSortPermission() {
        startActivity(new Intent(this, PermissionGroupActivity.class));
    }

    private void openConfigPerms() {
        Intent intent = new Intent(this, BackupActivity.class);
        intent.putParcelableArrayListExtra(BackupActivity.EXTRA_APPS,
                new ArrayList<>(adapter.getAppInfos()));
        startActivity(intent);
    }

    private void openUsageStats() {
        startActivity(new Intent(this, PermsUsageStatsActivity.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSearchHandler.handleWord(newText);
        return true;
    }


    private void switchUser(UserInfo user) {
        getSupportActionBar().setSubtitle(user.name);
        Users.getInstance().setCurrentLoadUser(user);

        ShizukuManager.getInstance(getApplicationContext()).setUserHandleId(user.id);
        LocalImageLoader.clear();
        loadData(true);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("user_id", user.id).apply();
    }
}
