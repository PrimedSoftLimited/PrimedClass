package com.primedsoft.primedclass.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.primedsoft.primedclass.Model.ChildModel;
import com.primedsoft.primedclass.Model.ParentModel;
import com.primedsoft.primedclass.R;
import com.primedsoft.primedclass.Utils.CircleTransform;
import com.primedsoft.primedclass.WelcomeActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class HomeTeacher extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    boolean doubleBackToExitPressedOnce = false;

    FirebaseAuth auth;
    private RecyclerView rvChild;
    private DatabaseReference mUsersDatabase,parentRef;
    FloatingTextButton addChild;
    private LinearLayoutManager mLayoutManager;
    private String imageUrl,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_teacher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        auth = FirebaseAuth.getInstance();

        String uid = auth.getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Children").child(uid);
        parentRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        rvChild = (RecyclerView) findViewById(R.id.rvChildren);
        addChild = (FloatingTextButton)findViewById(R.id.addChildParent);

        getDetails();

        addChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeTeacher.this,AddChildActivity.class));
            }
        });

        mLayoutManager = new LinearLayoutManager(this);
       // mLayoutManager.setReverseLayout(true);

        rvChild.setHasFixedSize(true);
        rvChild.setLayoutManager(mLayoutManager);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        CircleImageView parentImage = header.findViewById(R.id.imgParent);

        Picasso.with(HomeTeacher.this).load(imageUrl).transform(new CircleTransform()) .networkPolicy(NetworkPolicy.OFFLINE).into(parentImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(HomeTeacher.this)
                        .load(imageUrl).into(parentImage);

            }
        });


        TextView parentName = header.findViewById(R.id.txtParentName);
        parentName.setText(name);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getDetails() {
        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ParentModel parentModel = dataSnapshot.getValue(ParentModel.class);

                imageUrl = parentModel.getImageUrl();
                name = parentModel.getName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        }else {

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
            Intent homeScreenIntent = new Intent(Intent.ACTION_MAIN);
            homeScreenIntent.addCategory(Intent.CATEGORY_HOME);
            homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeScreenIntent);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_teacher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.homeParent) {
            startActivity(new Intent(HomeTeacher.this,TeachersHome.class));
        } else if (id == R.id.LogoutParent) {
            auth.signOut();
            startActivity(new Intent(HomeTeacher.this, WelcomeActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<ChildModel,AllChildren> adapter = new FirebaseRecyclerAdapter<ChildModel, AllChildren>(
                ChildModel.class,
                R.layout.single_child_item,
                AllChildren.class,
                mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(AllChildren viewHolder, ChildModel model, int position) {

                String key = getRef(position).getKey();

                viewHolder.setDisplayName(model.getName());
                viewHolder.setUserImage(model.getImageUrl(),HomeTeacher.this);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(HomeTeacher.this,ChildDetails.class);
                        intent.putExtra("imageUrl",model.getImageUrl());
                        intent.putExtra("key",key);
                        startActivity(intent);

                    }
                });

            }
        };
        rvChild.setAdapter(adapter);
    }

    public static class AllChildren extends RecyclerView.ViewHolder {

        View mView;
        ImageView userImage;
        TextView name;


        public AllChildren(View itemView) {
            super(itemView);

            mView = itemView;
            // like = (ImageView) mView.findViewById(R.id.imgLike);
            //   comment = (ImageView) mView.findViewById(R.id.imgComment);
            userImage = (ImageView) mView.findViewById(R.id.imgChild);
            name = (TextView)mView.findViewById(R.id.childName);

        }

        public void setDisplayName(String sname){

            name.setText(sname);

        }




        public void setUserImage(String status, Context context){

            Picasso.with(context).load(status).transform(new CircleTransform()) .networkPolicy(NetworkPolicy.OFFLINE).into(userImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(status).into(userImage);

                }
            });



        }


    }
}
