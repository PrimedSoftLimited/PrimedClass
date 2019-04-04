package com.primedsoft.primedclass.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.primedsoft.primedclass.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class Signup extends AppCompatActivity {

    private CircleImageView teacher,parent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        teacher = (CircleImageView)findViewById(R.id.TeacherSignup);
        parent = (CircleImageView)findViewById(R.id.imgParentSignup);

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Signup.this,TeacherSignupActivity.class));

            }
        });

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this,ParentSignupActivity.class));
            }
        });

    }
}
