package com.samcreators.wakeupwallpaper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

public class MainPagerAdapter extends FragmentStatePagerAdapter implements HomeFragment.ViewAllCatClicked {

    int noOfTabs;

    public MainPagerAdapter(@NonNull FragmentManager fm, int behavior, int noOfTabs) {
        super(fm, behavior);
        this.noOfTabs = noOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        HomeFragment homeFragment = new HomeFragment();
        RecentFragment recentFragment = new RecentFragment();
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        PopularFragment popularFragment = new PopularFragment();
        switch (position){
            case 0 :
                return homeFragment;
            case 1 :
                return popularFragment;
            case 2 :
                return categoriesFragment;
            case 3 :
                return recentFragment;
            default:
                return homeFragment;
        }
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }

    @Override
    public void onViewAllCatClicked() {

    }
}
