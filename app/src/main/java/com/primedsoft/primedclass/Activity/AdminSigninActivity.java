package com.primedsoft.primedclass.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.primedsoft.primedclass.Model.SigninModel;
import com.primedsoft.primedclass.R;

public class AdminSigninActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private RecyclerView rvAllSignings;
    private DatabaseReference  parentRef,signinRef;
    private LinearLayoutManager mLayoutManager;
    private String parentUid, childsPushKey;
    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_signin);

        signinRef = FirebaseDatabase.getInstance().getReference().child("Sigin");
        parentRef = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();

        rvAllSignings = (RecyclerView)findViewById(R.id.rvAdminSignin);
        mLayoutManager = new LinearLayoutManager(this);
        rvAllSignings.setLayoutManager(mLayoutManager);


    }


    @Override
    protected void onResume() {
        super.onResume();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<SigninModel,AllSigning> adapter = new FirebaseRecyclerAdapter<SigninModel, AllSigning>(
                SigninModel.class,
                R.layout.single_signin_view,
                AllSigning.class,
                signinRef
        ) {
            @Override
            protected void populateViewHolder(AllSigning viewHolder, SigninModel model, int position) {

                viewHolder.parentName.setText(model.getParentName());
                viewHolder.distance.setText(model.getDistance()+" meters from registered location");
                viewHolder.time.setText(model.getTime());
                viewHolder.message.setText(model.getMeesage());
                viewHolder.teacherName.setText(model.getTeacherName());

            }
        };
        rvAllSignings.setAdapter(adapter);
    }

    public static class AllSigning extends RecyclerView.ViewHolder {

        View mView;
        TextView teacherName,parentName,message,time,distance;


        public AllSigning(View itemView) {
            super(itemView);

            mView = itemView;
            // like = (ImageView) mView.findViewById(R.id.imgLike);
            //   comment = (ImageView) mView.findViewById(R.id.imgComment);

            parentName = (TextView)mView.findViewById(R.id.txtStatus);
            teacherName = (TextView)mView.findViewById(R.id.txtTeacherSigninName);
            message = (TextView)mView.findViewById(R.id.txtMessage);
            time = (TextView)mView.findViewById(R.id.txtDateSignin);
            distance = (TextView)mView.findViewById(R.id.txtTopic);

        }


    }

}
