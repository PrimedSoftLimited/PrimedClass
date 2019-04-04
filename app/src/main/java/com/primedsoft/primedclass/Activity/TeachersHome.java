package com.primedsoft.primedclass.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.primedsoft.primedclass.Model.AssignedModel;
import com.primedsoft.primedclass.Model.ChildModel;
import com.primedsoft.primedclass.R;
import com.primedsoft.primedclass.Utils.CircleTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class TeachersHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth auth;
    private RecyclerView rvChild;
    private DatabaseReference mUsersDatabase,assignedChildrenRef;
    LinearLayoutManager mLayoutManager;
    private String imageUrl,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("Children");
        assignedChildrenRef = FirebaseDatabase.getInstance().getReference().child("Assigned Children").child(uid);
        rvChild = (RecyclerView) findViewById(R.id.rvTeacherHome);

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
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.teachers_home, menu);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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

    private void initAdapter() {
        FirebaseRecyclerAdapter<AssignedModel,AllChildren> adapter = new FirebaseRecyclerAdapter<AssignedModel, AllChildren>(
                AssignedModel.class,
                R.layout.single_child_item,
                AllChildren.class,
                assignedChildrenRef
        ) {
                    @Override
                    protected void populateViewHolder(AllChildren viewHolder, AssignedModel model, int position) {

                        mUsersDatabase.child(model.getParentUid()).child(model.getChildKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            ChildModel child = dataSnapshot.getValue(ChildModel.class);
                                            viewHolder.setDisplayName(child.getName());
                                            viewHolder.setUserImage(child.getImageUrl(),TeachersHome.this);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                        Toast.makeText(TeachersHome.this, databaseError.getDetails(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent teachingActivity = new Intent(TeachersHome.this,TeachingActivity.class);
                                teachingActivity.putExtra("childsKey",model.getChildKey());
                                teachingActivity.putExtra("parentUid",model.getParentUid());
                                startActivity(teachingActivity);
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
