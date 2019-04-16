package com.primedsoft.primedclass.Fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.primedsoft.primedclass.Activity.ParentViewTeaching;
import com.primedsoft.primedclass.Model.ChildrenTeacherModel;
import com.primedsoft.primedclass.Model.ParentModel;
import com.primedsoft.primedclass.Model.RequestedTeacherModel;
import com.primedsoft.primedclass.R;
import com.primedsoft.primedclass.Utils.CircleTransform;
import com.primedsoft.primedclass.Utils.MultipleSelectionSpinner;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildrenTeachers extends Fragment {

    private Button requestTeacher;
    FirebaseAuth auth;
    private RecyclerView childrenTeacherRv;
    private DatabaseReference childrenTeacherRef, requestedTeachersRef, userRef;
    private LinearLayoutManager mLayoutManager;
    private String key;
    private ProgressDialog dialogs;

    public ChildrenTeachers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        key = container.getTag().toString();
        return inflater.inflate(R.layout.fragment_children_teachers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        dialogs = new ProgressDialog(getContext());
        childrenTeacherRef = FirebaseDatabase.getInstance().getReference().child("Children Teachers").child(uid).child(key);
        requestedTeachersRef = FirebaseDatabase.getInstance().getReference().child("Requested Teachers");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        childrenTeacherRv = view.findViewById(R.id.rvChildrenTeacher);
        mLayoutManager = new LinearLayoutManager(getContext());
        childrenTeacherRv.setLayoutManager(mLayoutManager);
        requestTeacher = (Button) view.findViewById(R.id.btnRequestTeacher);

        requestTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestDialog();
            }
        });
    }

    private void showRequestDialog() {


        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.single_request_teacher_view);

        MultipleSelectionSpinner mSpinner = dialog.findViewById(R.id.spinClassTaughtDialog);
        MultipleSelectionSpinner mSpinnerSubject = dialog.findViewById(R.id.spinSubjectTaughtDialog);
        //List which hold multiple selection spinner values
        List<String> list = new ArrayList<String>();
        List<String> subjectList = new ArrayList<String>();

        list.add("Grade 1");
        list.add("Grade 2");
        list.add("Grade 3");
        list.add("Grade 4");
        list.add("Grade 5");
        list.add("Grade 6");
        list.add("Grade 7");


        subjectList.add("Mathematics");
        subjectList.add("English");
        subjectList.add("Phonics");
        subjectList.add("Sciences");
        subjectList.add("Art");
        subjectList.add("Logic");
        subjectList.add("Programming");

        //set items to spinner from list
        mSpinner.setItems(list);

        mSpinnerSubject.setItems(subjectList);

        Button request = dialog.findViewById(R.id.btnRequestTeacherDialog);

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String subject = mSpinnerSubject.getSelectedItemsAsString();
                String classTaught = mSpinner.getSelectedItemsAsString();

                if (subject.isEmpty()) {
                    Toast.makeText(getContext(), "Please select a subject or more to request", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (classTaught.isEmpty()) {
                    Toast.makeText(getContext(), "Please select a class or more to request", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialogs.setMessage("Placing request...please wait");
                dialogs.setCancelable(false);
                dialogs.show();
                String uid = auth.getCurrentUser().getUid();


                userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ParentModel mod = dataSnapshot.getValue(ParentModel.class);

                        RequestedTeacherModel md = new RequestedTeacherModel();
                        md.setParentsName(mod.getName());
                        md.setClassRequested(classTaught);
                        md.setSubjectRequested(subject);
                        md.setParentsUid(uid);
                        md.setChildsPushKey(key);

                        requestedTeachersRef.push().setValue(md)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialogs.dismiss();
                                            dialog.dismiss();
                                            Toast.makeText(getContext(), "Request sent.. please wait", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                dialogs.dismiss();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        dialog.show();

    }


    @Override
    public void onResume() {
        super.onResume();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<ChildrenTeacherModel, ChildrensTeachersViewholder> adapter = new FirebaseRecyclerAdapter<ChildrenTeacherModel, ChildrensTeachersViewholder>(
                ChildrenTeacherModel.class,
                R.layout.single_child_item,
                ChildrensTeachersViewholder.class,
                childrenTeacherRef
        ) {
            @Override
            protected void populateViewHolder(ChildrensTeachersViewholder viewHolder, ChildrenTeacherModel model, int position) {

                viewHolder.setDisplayName(model.getTeacerName());
                viewHolder.setUserImage(model.getTeacherImageUrl(), getContext());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goToViewingActivity = new Intent(getContext(), ParentViewTeaching.class);
                        goToViewingActivity.putExtra("teacherUid", model.getTeacherUid());
                        goToViewingActivity.putExtra("childKey", key);
                        getContext().startActivity(goToViewingActivity);
                    }
                });

            }
        };
        childrenTeacherRv.setAdapter(adapter);
    }

    public static class ChildrensTeachersViewholder extends RecyclerView.ViewHolder {

        View mView;
        ImageView userImage;
        TextView name;

        public ChildrensTeachersViewholder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            userImage = (ImageView) mView.findViewById(R.id.imgChild);
            name = (TextView) mView.findViewById(R.id.childName);
        }

        public void setDisplayName(String sname) {

            name.setText(sname);

        }


        public void setUserImage(String status, Context context) {

            Picasso.with(context).load(status).transform(new CircleTransform()).networkPolicy(NetworkPolicy.OFFLINE).into(userImage, new Callback() {
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
