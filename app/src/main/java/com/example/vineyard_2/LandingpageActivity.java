package com.example.vineyard_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class LandingpageActivity extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landingpage);
        toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setCurrentItem(1, false);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new HomeFragment());
        viewPagerAdapter.addFragments(new SearchFragment());
        viewPagerAdapter.addFragments(new ProfileFragment());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1, false);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.nhome);
        tabLayout.getTabAt(1).setIcon(R.drawable.nsearch);
        tabLayout.getTabAt(2).setIcon(R.drawable.nmenu);
    }

    public void onClickSignUp (View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
