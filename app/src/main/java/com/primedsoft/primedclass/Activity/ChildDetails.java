package com.primedsoft.primedclass.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.primedsoft.primedclass.Fragments.ChildrenTeachers;
import com.primedsoft.primedclass.R;
import com.primedsoft.primedclass.Utils.ViewPagerAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ChildDetails extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageButton button;
    private String key,imageUrl;
    private ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details);


        key = getIntent().getStringExtra("key");
        imageUrl = getIntent().getStringExtra("imageUrl");

        viewPager = findViewById(R.id.viewpager);
        background = (ImageView)findViewById(R.id.backgroundimage);
        viewPager.setTag(key);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        button = findViewById(R.id.onback);


        Picasso.with(ChildDetails.this).load(imageUrl) .networkPolicy(NetworkPolicy.OFFLINE).into(background, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(ChildDetails.this)
                        .load(imageUrl).into(background);

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }
    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new com.primedsoft.primedclass.Fragments.ChildDetails(),"Child Details");
        adapter.addFragment(new ChildrenTeachers(), "Teachers");
        viewPager.setAdapter(adapter);
    }
}
