package com.primedsoft.primedclass.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.primedsoft.primedclass.R;
import com.primedsoft.primedclass.WelcomeActivity;

public class AdminDashboard extends AppCompatActivity {

    private ImageView allUsers,compliant,invoice,signin,assignTeachers,settings;
    private FirebaseAuth auth;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        allUsers = (ImageView)findViewById(R.id.imgAllUsers);
        compliant = (ImageView)findViewById(R.id.imgAllCompliant);
        invoice = (ImageView)findViewById(R.id.imgInvoice);
        assignTeachers = (ImageView)findViewById(R.id.imgAssignTeachers);
        signin = (ImageView)findViewById(R.id.imgAllSigings);
        settings = (ImageView)findViewById(R.id.imgSettings);
        auth = FirebaseAuth.getInstance();


        allUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this,AllUsers.class));
            }
        });

        compliant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this,AdminComplainActivity.class));
            }
        });

        assignTeachers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this,RequestTeacherActivity.class));
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this,AdminSigninActivity.class));
            }
        });

        allUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this,AllUsers.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               auth.signOut();
               startActivity(new Intent(AdminDashboard.this, WelcomeActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        }else {

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
            Intent homeScreenIntent = new Intent(Intent.ACTION_MAIN);
            homeScreenIntent.addCategory(Intent.CATEGORY_HOME);
            homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeScreenIntent);

        }
    }
}
