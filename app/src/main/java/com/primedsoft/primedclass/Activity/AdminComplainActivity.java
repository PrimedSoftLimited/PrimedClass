package com.primedsoft.primedclass.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.primedsoft.primedclass.Model.ComplainModel;
import com.primedsoft.primedclass.Model.ParentModel;
import com.primedsoft.primedclass.R;
import com.primedsoft.primedclass.Utils.CircleTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class AdminComplainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private RecyclerView rvChild;
    private DatabaseReference teachingRef,complainRef,userRef;
    private LinearLayoutManager mLayoutManager;
    private ProgressDialog pDialog;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complain);

        auth = FirebaseAuth.getInstance();

        pDialog = new ProgressDialog(this);
        uid = auth.getCurrentUser().getUid();

        teachingRef = FirebaseDatabase.getInstance().getReference().child("Teaching");
        complainRef = FirebaseDatabase.getInstance().getReference().child("Complains");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rvChild = (RecyclerView) findViewById(R.id.rvAdminComplain);
        mLayoutManager = new LinearLayoutManager(this);


        rvChild.setHasFixedSize(true);
        rvChild.setLayoutManager(mLayoutManager);


    }

    @Override
    protected void onResume() {
        super.onResume();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<ComplainModel,AllParent> adapter = new FirebaseRecyclerAdapter<ComplainModel, AllParent>(
                ComplainModel.class,
                R.layout.single_child_item,
                AllParent.class,
                complainRef
        ) {
            @Override
            protected void populateViewHolder(AllParent viewHolder, ComplainModel model, int position) {

                String pushKey = getRef(position).getKey();
                userRef.child(model.getParentUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {
                                    ParentModel mod = dataSnapshot.getValue(ParentModel.class);

                                    viewHolder.setDisplayName(mod.getName());
                                    viewHolder.setUserImage(mod.getImageUrl(),AdminComplainActivity.this);

                                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(AdminComplainActivity.this,Complains.class);
                                            intent.putExtra("key",pushKey);
                                            startActivity(intent);
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }
        };
        rvChild.setAdapter(adapter);
    }

    public static class AllParent extends RecyclerView.ViewHolder {

        View mView;
        ImageView userImage;
        TextView name;


        public AllParent(View itemView) {
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
