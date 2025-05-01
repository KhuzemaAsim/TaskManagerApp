package com.example.taskmanager;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private ViewPager mainPager;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstance) {

        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isNightEnabled = preferences.getBoolean("dark_mode", false);

        if (isNightEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        mainPager = findViewById(R.id.view_pager);

        configureViewPager(mainPager);

        bottomNav.setOnNavigationItemSelectedListener(navItem -> {
            int itemId = navItem.getItemId();

            if (itemId == R.id.nav_schedule) {
                mainPager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.nav_past) {
                mainPager.setCurrentItem(1);
                return true;
            } else if (itemId == R.id.nav_notifications) {
                mainPager.setCurrentItem(2);
                return true;
            } else if (itemId == R.id.nav_profile) {
                mainPager.setCurrentItem(3);
                return true;
            }
            return false;
        });

        mainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            @Override
            public void onPageSelected(int i) {
                bottomNav.getMenu().getItem(i).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {}
        });
    }

    private void configureViewPager(ViewPager pagerView) {
        CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
        adapter.appendFragment(new ScheduleFragment(), "Schedule");
        adapter.appendFragment(new PreviousTasksFragment(), "Past");
        adapter.appendFragment(new NotificationFragment(), "Notifications");
        adapter.appendFragment(new ProfileFragment(), "Profile");
        pagerView.setAdapter(adapter);
    }
}

class CustomPagerAdapter extends androidx.fragment.app.FragmentPagerAdapter {

    private final List<androidx.fragment.app.Fragment> fragments = new ArrayList<>();
    private final List<String> titles = new ArrayList<>();

    public CustomPagerAdapter(androidx.fragment.app.FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void appendFragment(androidx.fragment.app.Fragment frag, String name) {
        fragments.add(frag);
        titles.add(name);
    }

    @Override
    public androidx.fragment.app.Fragment getItem(int pos) {
        return fragments.get(pos);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int pos) {
        return titles.get(pos);
    }
}
