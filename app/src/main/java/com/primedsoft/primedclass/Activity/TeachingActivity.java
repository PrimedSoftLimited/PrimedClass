package com.primedsoft.primedclass.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.primedsoft.primedclass.R;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class TeachingActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private RecyclerView rvChild;
    private DatabaseReference mUsersDatabase,parentRef;
    FloatingTextButton btnSigning;
    private LinearLayoutManager mLayoutManager;
    private String parentUid,childsPushKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teaching);

        auth = FirebaseAuth.getInstance();
        parentUid = getIntent().getStringExtra("parentUid");
        childsPushKey = getIntent().getStringExtra("childsKey");

        String uid = auth.getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Children").child(parentUid).child(childsPushKey);
        parentRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rvChild = (RecyclerView) findViewById(R.id.rvClassTaught);
        btnSigning = (FloatingTextButton)findViewById(R.id.teacherSignIn);

        mLayoutManager = new LinearLayoutManager(this);
        // mLayoutManager.setReverseLayout(true);

        rvChild.setHasFixedSize(true);
        rvChild.setLayoutManager(mLayoutManager);


        btnSigning.setOnClickListener(v -> startSigning());
    }

    private void startSigning() {

    }


    public static class ChildTeaching extends RecyclerView.ViewHolder{

        View mView;

        public ChildTeaching(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
    }

}
