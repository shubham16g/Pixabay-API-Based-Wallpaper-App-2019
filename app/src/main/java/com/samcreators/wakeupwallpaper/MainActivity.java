package com.samcreators.wakeupwallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import static android.Manifest.*;

public class MainActivity extends AppCompatActivity implements HomeFragment.ViewAllCatClicked {

    private static final int REQUEST_PERMISSION_KEY = 1;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navView;
    BottomNavigationView bottomNavigationView;
    NonSwipeableViewPager mainPager;
    RelativeLayout splashLayout;
    SearchView searchView;
    MenuItem menuItem;
    long backPressedTime;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        splashLayout = findViewById(R.id.splash);
        drawerLayout = findViewById(R.id.main_drawer);
        toolbar = findViewById(R.id.main_toolbar);
        navView = findViewById(R.id.nav_view);
        mainPager = findViewById(R.id.main_pager);
        bottomNavigationView = findViewById(R.id.bottom_nav);

        setSupportActionBar(toolbar);
        setupDrawer(this, drawerLayout, toolbar, navView);
        splashLayout.animate().alpha(0f).setStartDelay(2000).setDuration(400);

        IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReciver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(NetworkStateChangeReciver.IS_NETWORK_AVAILABLE, false);
                if (!isNetworkAvailable) {
                    bottomNavigationView.setVisibility(View.GONE);
                    mainPager.setVisibility(View.GONE);
                    toolbar.setTitle("OFFLINE");
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    mainPager.setVisibility(View.VISIBLE);
                    int id = bottomNavigationView.getSelectedItemId();
                    bottomNavigationView.setSelectedItemId(id);
                }
            }
        }, intentFilter);

//        Set ViewPager (mainPager)
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), 3, 4);
        mainPager.setAdapter(pagerAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] PERMISSIONS = {permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE};
            if (getApplicationContext().checkCallingOrSelfPermission(PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS, REQUEST_PERMISSION_KEY);
            }
        }

        if (!internetIsConnected()){
            bottomNavigationView.setVisibility(View.GONE);
            mainPager.setVisibility(View.GONE);
            toolbar.setTitle("OFFLINE");
        }

//        Click on items of BottomNavigation (bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        mainPager.setCurrentItem(0, false);
                        toolbar.setTitle("HOME");
                        break;
                    case R.id.popular:
                        mainPager.setCurrentItem(1, false);
                        toolbar.setTitle("POPULAR");
                        break;
                    case R.id.categories:
                        mainPager.setCurrentItem(2, false);
                        toolbar.setTitle("CATEGORIES");
                        break;
                    case R.id.recent:
                        mainPager.setCurrentItem(3, false);
                        toolbar.setTitle("RECENT");
                        break;
                }
                return true;
            }
        });
    }

    public static void setupDrawer(final Activity activity, final DrawerLayout drawerLayout, Toolbar toolbar, NavigationView navView) {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.downloads:
                        Intent intent1 = new Intent(activity, MoreItemActivity.class);
                        intent1.putExtra("title", "DOWNLOADS");
                        activity.startActivity(intent1);
                        break;
                    case R.id.gallery:
                        Intent intent2 = new Intent(activity, MoreItemActivity.class);
                        intent2.putExtra("title", "GALLERY");
                        activity.startActivity(intent2);
                        break;
                    case R.id.offline_generator:
                        activity.startActivity(new Intent(activity, RandomGenerateActivity.class));
                        break;
                    case R.id.settings:
                        Toast.makeText(activity, "Settings will available in next version", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.share_app:
                        Toast.makeText(activity, "Share is not available before launch on Play Store", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.about:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"));
                        Toast.makeText(activity, "We show App's website page when completed", Toast.LENGTH_SHORT).show();
                        activity.startActivity(intent);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search..");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                hideKeyboard(MainActivity.this);
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query", s);
                intent.putExtra("extra", "&q=" + s);
                startActivity(intent);
                searchView.setIconified(true);
                menuItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean internetIsConnected() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
            else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /*@Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }
        else {
            Snackbar.make(mainPager,"Press back again to exit",Snackbar.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }*/

    @Override
    public void onViewAllCatClicked() {
        bottomNavigationView.setSelectedItemId(R.id.categories);
    }
}