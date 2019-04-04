package com.primedsoft.primedclass.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.primedsoft.primedclass.Fragments.AllParents;
import com.primedsoft.primedclass.Fragments.AllTeachers;
import com.primedsoft.primedclass.R;
import com.primedsoft.primedclass.Utils.ViewPagerAdapter;

public class AllUsers extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        viewPager = findViewById(R.id.viewpagerUsers);
        // background = (ImageView)findViewById(R.id.backgroundimage);
        //  viewPager.setTag(key);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabsUsers);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllParents(),"Parents");
        adapter.addFragment(new AllTeachers(), "Teachers");
        viewPager.setAdapter(adapter);
    }
}
