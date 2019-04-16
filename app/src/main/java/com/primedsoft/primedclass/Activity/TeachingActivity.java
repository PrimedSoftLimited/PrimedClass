package com.primedsoft.primedclass.Activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.primedsoft.primedclass.Model.ParentModel;
import com.primedsoft.primedclass.Model.SigninModel;
import com.primedsoft.primedclass.Model.TeacherModel;
import com.primedsoft.primedclass.Model.TeachingModel;
import com.primedsoft.primedclass.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class TeachingActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private RecyclerView rvChild;
    private DatabaseReference mUsersDatabase, parentRef, teachingRef,signinRef;
    FloatingTextButton btnSigning;
    private LinearLayoutManager mLayoutManager;
    private String parentUid, childsPushKey;
    private ProgressDialog pDialog;
    private Calendar calendar;
    private double lng, lat;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SimpleDateFormat simpledateformat;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teaching);

        auth = FirebaseAuth.getInstance();
        parentUid = getIntent().getStringExtra("parentUid");
        childsPushKey = getIntent().getStringExtra("childsKey");

        pDialog = new ProgressDialog(this);
        String uid = auth.getCurrentUser().getUid();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Children").child(parentUid).child(childsPushKey);
        parentRef = FirebaseDatabase.getInstance().getReference().child("Users");
        signinRef = FirebaseDatabase.getInstance().getReference().child("Sigin");
        teachingRef = FirebaseDatabase.getInstance().getReference().child("Teaching").child(uid).child(childsPushKey);
        rvChild = (RecyclerView) findViewById(R.id.rvClassTaught);
        btnSigning = (FloatingTextButton) findViewById(R.id.teacherSignIn);

        mLayoutManager = new LinearLayoutManager(this);
        // mLayoutManager.setReverseLayout(true);

        rvChild.setHasFixedSize(true);
        rvChild.setLayoutManager(mLayoutManager);


        btnSigning.setOnClickListener(v -> startSigning());
    }

    private void startSigning() {


        if (ActivityCompat.checkSelfPermission(TeachingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(TeachingActivity.this, "Please put on your location before starting class", Toast.LENGTH_SHORT).show();
            return;
        } else {

            calendar = Calendar.getInstance();
            simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String date = simpledateformat.format(calendar.getTime());

            TeachingModel teachingModel = new TeachingModel();
            teachingModel.setCompletedLevel("");
            teachingModel.setDateCompleted("");
            teachingModel.setDateStarted(date);
            teachingModel.setTopicTaught("");

            teachingRef.push().setValue(teachingModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                parentRef.child(parentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            ParentModel parentModel = dataSnapshot.getValue(ParentModel.class);

                                            double pLat = Double.parseDouble(parentModel.getLatitude());
                                            double pLng = Double.parseDouble(parentModel.getLongitude());

                                            String uid = auth.getCurrentUser().getUid();

                                         parentRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                 if (dataSnapshot.exists()){

                                                     TeacherModel teachingModel = dataSnapshot.getValue(TeacherModel.class);
                                                     if (ActivityCompat.checkSelfPermission(TeachingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TeachingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                         return;
                                                     }

                                                     fusedLocationProviderClient.getLastLocation()
                                                             .addOnSuccessListener(TeachingActivity.this, new OnSuccessListener<Location>() {
                                                                 @Override
                                                                 public void onSuccess(Location location) {

                                                                     lng = location.getLongitude();
                                                                     lat = location.getLatitude();

                                                                 }
                                                             });

                                                     Location loc1 = new Location("");
                                                     loc1.setLatitude(pLat);
                                                     loc1.setLongitude(pLng);

                                                     Location loc2 = new Location("");
                                                     loc2.setLatitude(lat);
                                                     loc2.setLongitude(lng);

                                                     int  distanceInMeters = Math.round(loc1.distanceTo(loc2));

                                                     if (distanceInMeters > 10){
                                                         message = "Teacher is not in registered location ";
                                                     }else {
                                                         message = "Teacher is in registered location ";
                                                     }


                                                     SigninModel signinModel = new SigninModel();
                                                     signinModel.setDistance(String.valueOf(distanceInMeters));
                                                     signinModel.setParentName(parentModel.getName());
                                                     signinModel.setMeesage(message);
                                                     signinModel.setTeacherName(teachingModel.getName());
                                                     signinModel.setTime(date);
                                                     signinModel.setTeacherUid(uid);



                                                     signinRef.push().setValue(signinModel)
                                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                 @Override
                                                                 public void onComplete(@NonNull Task<Void> task) {
                                                                     if (task.isSuccessful()){
                                                                         Toast.makeText(TeachingActivity.this, "Class started", Toast.LENGTH_SHORT).show();
                                                                     }
                                                                 }
                                                             });


                                                 }
                                             }

                                             @Override
                                             public void onCancelled(@NonNull DatabaseError databaseError) {
                                                 Toast.makeText(TeachingActivity.this, databaseError.getDetails(), Toast.LENGTH_SHORT).show();

                                             }
                                         });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(TeachingActivity.this, databaseError.getDetails(), Toast.LENGTH_SHORT).show();
                                    }
                                });





                            }
                        }
                    });

        }
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

                viewHolder.dateStarted.setText(model.getDateStarted());
                viewHolder.dateCompleted.setText(model.getDateCompleted());
                viewHolder.topic.setText(model.getTopicTaught());
                viewHolder.completedLevel.setText(model.getCompletedLevel());

                if (model.getCompletedLevel().equalsIgnoreCase("finished")) {
                    viewHolder.signout.setEnabled(false);
                    viewHolder.signout.setText("Signed out");
                } else {


                    viewHolder.signout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog dialog = new Dialog(TeachingActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.single_class_taught_form);

                            TextInputLayout tilTopic, tilCompletedLevel;
                            EditText edtTopic, edtCompletedLevel;
                            Button btnEndClass;

                            tilTopic = dialog.findViewById(R.id.tilTopic);
                            tilCompletedLevel = dialog.findViewById(R.id.tilCompletedLevel);
                            edtTopic = dialog.findViewById(R.id.edtTopicTaught);
                            edtCompletedLevel = dialog.findViewById(R.id.edtCompletedLevel);
                            btnEndClass = dialog.findViewById(R.id.btnFinishClass);


                            btnEndClass.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String topic = edtTopic.getText().toString().trim();
                                    String completedLevel = edtCompletedLevel.getText().toString().trim();

                                    calendar = Calendar.getInstance();
                                    simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                    String date = simpledateformat.format(calendar.getTime());

                                    if (topic.isEmpty()) {
                                        tilTopic.setError("Please enter a topic to signout");
                                        return;
                                    }

                                    if (completedLevel.isEmpty()) {
                                        tilCompletedLevel.setError("Please enter if you completed the topic or not");
                                        return;
                                    }

                                    pDialog.setMessage("Signing out of class.... please wait");
                                    pDialog.setCancelable(false);
                                    pDialog.show();


                                    String pushkey = getRef(position).getKey();

                                    teachingRef.child(pushkey).child("topicTaught").setValue(topic)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        teachingRef.child(pushkey).child("dateCompleted").setValue(date)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            teachingRef.child(pushkey).child("completedLevel").setValue(completedLevel)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                Toast.makeText(TeachingActivity.this, "successfully finished class", Toast.LENGTH_SHORT).show();
                                                                                                pDialog.dismiss();
                                                                                                dialog.dismiss();


                                                                                                viewHolder.completedLevel.setText(model.getCompletedLevel());
                                                                                                viewHolder.topic.setText(model.getTopicTaught());
                                                                                                viewHolder.dateCompleted.setText(model.getDateCompleted());
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });

                                }
                            });

                            dialog.show();

                        }
                    });


                }
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
