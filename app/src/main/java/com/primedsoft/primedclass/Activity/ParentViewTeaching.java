package com.primedsoft.primedclass.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.primedsoft.primedclass.Model.ComplainModel;
import com.primedsoft.primedclass.Model.TeachingModel;
import com.primedsoft.primedclass.R;

public class ParentViewTeaching extends AppCompatActivity {

    FirebaseAuth auth;
    private RecyclerView rvChild;
    private DatabaseReference teachingRef,complainRef,teacherComplain;
    private LinearLayoutManager mLayoutManager;
    private ProgressDialog pDialog;
    Button allComplain;
    String teacherUid,childKey,uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_view_teaching);

        teacherUid = getIntent().getStringExtra("teacherUid");
        childKey = getIntent().getStringExtra("childKey");

        auth = FirebaseAuth.getInstance();

        pDialog = new ProgressDialog(this);
        uid = auth.getCurrentUser().getUid();

        teachingRef = FirebaseDatabase.getInstance().getReference().child("Teaching").child(teacherUid).child(childKey);
        complainRef = FirebaseDatabase.getInstance().getReference().child("Complains");
        teacherComplain = FirebaseDatabase.getInstance().getReference().child("Teacher Complains");
        allComplain = (Button)findViewById(R.id.btnViewAllComplain);
        rvChild = (RecyclerView) findViewById(R.id.rvParentViewTeaching);
        mLayoutManager = new LinearLayoutManager(this);
        // mLayoutManager.setReverseLayout(true);

        rvChild.setHasFixedSize(true);
        rvChild.setLayoutManager(mLayoutManager);

        allComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentViewTeaching.this,ParentChildComplain.class);
                intent.putExtra("key",childKey);
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<TeachingModel, ChildTeaching> adapter = new FirebaseRecyclerAdapter<TeachingModel, ChildTeaching>(
                TeachingModel.class,
                R.layout.single_teaching_item,
                ChildTeaching.class,
                teachingRef

        ) {
            @Override
            protected void populateViewHolder(ChildTeaching viewHolder, TeachingModel model, int position) {

                viewHolder.signout.setVisibility(View.GONE);

                viewHolder.dateStarted.setText(model.getDateStarted());
                viewHolder.topic.setText(model.getTopicTaught());
                viewHolder.completedLevel.setText(model.getCompletedLevel());
                viewHolder.dateCompleted.setText(model.getDateCompleted());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(ParentViewTeaching.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.single_complain_view);


                        TextView topic = dialog.findViewById(R.id.txtTopic);
                        TextInputLayout tilComplaint = dialog.findViewById(R.id.tilComplain);
                        EditText edtComplaint = dialog.findViewById(R.id.edtComplain);

                        topic.setText(model.getTopicTaught());
                        Button btnSubmitComplaint = dialog.findViewById(R.id.btnSubmitCOmplain);

                        btnSubmitComplaint.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String complain = edtComplaint.getText().toString().trim();

                                if (complain.isEmpty()){
                                    tilComplaint.setError("Please add a complain");
                                    return;
                                }

                                pDialog.setMessage("Filling complaint..please wait");
                                pDialog.setCancelable(false);
                                pDialog.show();


                                ComplainModel complainModel = new ComplainModel();
                                complainModel.setChildKey(childKey);
                                complainModel.setComplain(complain);
                                complainModel.setDateStarted(model.getDateStarted());
                                complainModel.setTeacherUid(teacherUid);
                                complainModel.setTopic(model.getTopicTaught());
                                complainModel.setParentUid(uid);
                                complainModel.setResolved("no");


                                complainRef.push().setValue(complainModel)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()){

                                                teacherComplain.child(teacherUid).push().setValue(complainModel)
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()){
                                                                dialog.dismiss();
                                                                pDialog.dismiss();
                                                                Toast.makeText(ParentViewTeaching.this, "Complaint sent...please wait", Toast.LENGTH_SHORT).show();

                                                            }
                                                        });
                                                   }

                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        pDialog.dismiss();
                                        Toast.makeText(ParentViewTeaching.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        });

                        dialog.show();

                    }
                });


            }
        };
        rvChild.setAdapter(adapter);
    }

    public static class ChildTeaching extends RecyclerView.ViewHolder {

        View mView;
        TextView topic, dateStarted, dateCompleted, completedLevel;
        Button signout;


        public ChildTeaching(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            topic = mView.findViewById(R.id.txtTopicTaught);
            dateCompleted = mView.findViewById(R.id.txtDateCompleted);
            dateStarted = mView.findViewById(R.id.txtDateStarted);
            completedLevel = mView.findViewById(R.id.txtCompleted);

            signout = mView.findViewById(R.id.btnSignOutClass);
        }
    }
}
