package com.primedsoft.primedclass.Activity;

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
import com.primedsoft.primedclass.Model.ParentModel;
import com.primedsoft.primedclass.Model.RequestedTeacherModel;
import com.primedsoft.primedclass.R;
import com.primedsoft.primedclass.Utils.CircleTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class RequestTeacherActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private RecyclerView AllParentsRequeestRv;
    private DatabaseReference requestedTeachersRef,usersRef;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_teacher);

        auth = FirebaseAuth.getInstance();
        requestedTeachersRef = FirebaseDatabase.getInstance().getReference().child("Requested Teachers");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        AllParentsRequeestRv = (RecyclerView)findViewById(R.id.rvAllTeachersRequests);
        mLayoutManager = new LinearLayoutManager(this);

        AllParentsRequeestRv.setLayoutManager(mLayoutManager);
        AllParentsRequeestRv.setHasFixedSize(true);



    }


    @Override
    protected void onResume() {
        super.onResume();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<RequestedTeacherModel,AllTeachersRequest> adapter = new FirebaseRecyclerAdapter<RequestedTeacherModel, AllTeachersRequest>(

                RequestedTeacherModel.class,
                R.layout.single_child_item,
                AllTeachersRequest.class,
                requestedTeachersRef
        ) {
            @Override
            protected void populateViewHolder(AllTeachersRequest viewHolder, RequestedTeacherModel model, int position) {

               usersRef.child(model.getParentsUid())
                       .addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                               if (dataSnapshot.exists()) {
                                   ParentModel mod = dataSnapshot.getValue(ParentModel.class);
                                   viewHolder.setUserImage(mod.getImageUrl(),RequestTeacherActivity.this);
                                   viewHolder.setDisplayName(model.getParentsName());


                               }

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });

               viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent = new Intent(RequestTeacherActivity.this,AssignTeachers.class);
                       intent.putExtra("childKey",model.getChildsPushKey());
                       intent.putExtra("parentUid",model.getParentsUid());
                       startActivity(intent);
                   }
               });

            }
        };
        AllParentsRequeestRv.setAdapter(adapter);
    }

    public static class AllTeachersRequest extends RecyclerView.ViewHolder {

        View mView;
        ImageView userImage;
        TextView name;


        public AllTeachersRequest(View itemView) {
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
