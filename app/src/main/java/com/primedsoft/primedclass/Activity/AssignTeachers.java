package com.primedsoft.primedclass.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.primedsoft.primedclass.Model.AssignedModel;
import com.primedsoft.primedclass.Model.TeacherModel;
import com.primedsoft.primedclass.R;
import com.primedsoft.primedclass.Utils.CircleTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class AssignTeachers extends AppCompatActivity {

    FirebaseAuth auth;
    private RecyclerView AllParentsRv;
    private DatabaseReference mUsersDatabase,assignedChildrenRef;
    private LinearLayoutManager mLayoutManager;
    private Query stylistQuery;
    private String parentUid,ChildrenKey;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_teachers);

        auth = FirebaseAuth.getInstance();
        parentUid = getIntent().getStringExtra("parentUid");
        ChildrenKey = getIntent().getStringExtra("childKey");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        assignedChildrenRef = FirebaseDatabase.getInstance().getReference().child("Assigned Children");

        AllParentsRv = (RecyclerView) findViewById(R.id.rvAllTeachers);
        stylistQuery = mUsersDatabase.orderByChild("status").equalTo("teacher");

        mLayoutManager = new LinearLayoutManager(this);
        AllParentsRv.setHasFixedSize(true);
        AllParentsRv.setLayoutManager(mLayoutManager);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<TeacherModel,AllParent> adapter = new FirebaseRecyclerAdapter<TeacherModel, AllParent>(
                TeacherModel.class,
                R.layout.single_teacher_list,
                AllParent.class,
                stylistQuery
        ) {
            @Override
            protected void populateViewHolder(AllParent viewHolder, TeacherModel model, int position) {

                viewHolder.setDisplayName(model.getName());
                viewHolder.setClassT(model.getClassTaught());
                viewHolder.setPhone(model.getPhone());
                viewHolder.setSubject(model.getSubjectTaught());
                viewHolder.setUserImage(model.getImageUrl(),AssignTeachers.this);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AssignTeachers.this);
                        alertDialogBuilder.setMessage("Do you want to assign this teacher to the child");
                                alertDialogBuilder.setPositiveButton("YES",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                Toast.makeText(AssignTeachers.this, "Assigning child...please wait", Toast.LENGTH_SHORT).show();
                                                AssignedModel assignedModel  = new AssignedModel();
                                                assignedModel.setChildKey(ChildrenKey);
                                                assignedModel.setParentUid(parentUid);

                                                assignedChildrenRef.child(model.getUid()).push().setValue(assignedModel)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(AssignTeachers.this, "Child successfully assigned", Toast.LENGTH_SHORT).show();
                                                                    finish();

                                                                }

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(AssignTeachers.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });

                      alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              finish();
                          }
                      });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });


            }
        };
        AllParentsRv.setAdapter(adapter);
    }

    public static class AllParent extends RecyclerView.ViewHolder {

        View mView;
        ImageView userImage;
        TextView name,phone,subject,classT;


        public AllParent(View itemView) {
            super(itemView);

            mView = itemView;
            userImage = (ImageView) mView.findViewById(R.id.teacherImage);
            name = (TextView)mView.findViewById(R.id.txtTeachersName);
            phone = (TextView)mView.findViewById(R.id.txtTeachersPhoneNumber);
            subject = (TextView)mView.findViewById(R.id.txtSubjectTaught);
            classT = (TextView)mView.findViewById(R.id.txtClassTaught);

        }

        public void setDisplayName(String sname){

            name.setText(sname);

        }



        public void setPhone(String sname){

            phone.setText(sname);

        }



        public void setSubject(String sname){

            subject.setText(sname);

        }



        public void setClassT(String sname){

            classT.setText(sname);

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
