package com.primedsoft.primedclass.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.primedsoft.primedclass.Model.ChildModel;
import com.primedsoft.primedclass.R;
import com.primedsoft.primedclass.Utils.CircleTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class PArentChildrenActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private RecyclerView AllParentsRv;
    private DatabaseReference mUsersDatabase;
    private LinearLayoutManager mLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_children);
        auth = FirebaseAuth.getInstance();
        AllParentsRv = (RecyclerView) findViewById(R.id.rvParentChildren);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("Children");

        mLayoutManager = new LinearLayoutManager(this);
        AllParentsRv.setHasFixedSize(true);
        AllParentsRv.setLayoutManager(mLayoutManager);
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
                viewHolder.setUserImage(model.getImageUrl(),PArentChildrenActivity.this);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(PArentChildrenActivity.this,ChildDetails.class);
                        intent.putExtra("imageUrl",model.getImageUrl());
                        intent.putExtra("key",key);
                        startActivity(intent);
                        //  startActivity(new Intent(HomeTeacher.this,ChildDetails.class));
                    }
                });

            }
        };
        AllParentsRv.setAdapter(adapter);
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
