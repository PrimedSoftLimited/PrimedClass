package com.primedsoft.primedclass.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
import com.primedsoft.primedclass.Model.ComplainModel;
import com.primedsoft.primedclass.Model.TeacherModel;
import com.primedsoft.primedclass.R;

public class Complains extends AppCompatActivity {

    FirebaseAuth auth;
    private RecyclerView rvChild;
    private DatabaseReference parentComplainRef,complainRef,userRef;
    private LinearLayoutManager mLayoutManager;
    private ProgressDialog pDialog;
    String uid;
    String childKey,pushKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complains);
        pushKey = getIntent().getStringExtra("key");
        auth = FirebaseAuth.getInstance();

        pDialog = new ProgressDialog(this);
        uid = auth.getCurrentUser().getUid();

        parentComplainRef = FirebaseDatabase.getInstance().getReference().child("ParenComplain");
        complainRef = FirebaseDatabase.getInstance().getReference().child("Complains").child(pushKey);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rvChild = (RecyclerView) findViewById(R.id.rvComplain);
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


                userRef.child(model.getTeacherUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    TeacherModel mod = dataSnapshot.getValue(TeacherModel.class);
                                    viewHolder.setDisplayName("Teacher name: "+mod.getName());
                                    viewHolder.setComplain("Complain: "+model.getComplain());
                                    viewHolder.setDate(model.getDateStarted());
                                    viewHolder.setStatus("Resolved: "+model.getResolved());
                                    viewHolder.setTopic("Topic taught : "+model.getTopic());


                                    if (model.getResolved().equalsIgnoreCase("yes")){
                                        viewHolder.address.setEnabled(false);
                                        viewHolder.address.setText("Resolved");
                                    }else if (model.getResolved().equalsIgnoreCase("addressed")){
                                        viewHolder.address.setEnabled(false);
                                        viewHolder.address.setText("Addressed");
                                    }else if (model.getResolved().equalsIgnoreCase("no")){
                                        viewHolder.address.setEnabled(true);
                                        viewHolder.address.setText("Address");
                                    }
                                    viewHolder.address.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            viewHolder.address.setEnabled(false);
                                            Toast.makeText(Complains.this, "addressing complain..please wait", Toast.LENGTH_SHORT).show();
                                            complainRef.child("resolved").setValue("addressed")
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                viewHolder.address.setEnabled(true);
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    viewHolder.address.setEnabled(true);
                                                    Toast.makeText(Complains.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
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
        TextView name,topic,date,complain,status;
        Button address;


        public AllParent(View itemView) {
            super(itemView);

            mView = itemView;
            name = (TextView)mView.findViewById(R.id.txtTeacherComplainName);
            date = (TextView)mView.findViewById(R.id.txtDateSignin);
            topic = (TextView)mView.findViewById(R.id.txtTopic);
            status = (TextView)mView.findViewById(R.id.txtStatus);
            complain = (TextView)mView.findViewById(R.id.txtComplain);
            address = (Button)mView.findViewById(R.id.btnAddressComplain);
        }

        public void setDisplayName(String sname){

            name.setText(sname);

        }


        public void setTopic(String sname){

            topic.setText(sname);

        }

        public void setDate(String sname){

            date.setText(sname);

        }

        public void setStatus(String sname){

            status.setText(sname);

        }

        public void setComplain(String sname){

            complain.setText(sname);

        }



        }


    }

