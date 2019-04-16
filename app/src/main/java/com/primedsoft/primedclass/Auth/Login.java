package com.primedsoft.primedclass.Auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.primedsoft.primedclass.Activity.AdminDashboard;
import com.primedsoft.primedclass.Activity.HomeTeacher;
import com.primedsoft.primedclass.Activity.TeachersHome;
import com.primedsoft.primedclass.R;

public class Login extends AppCompatActivity {

    private FirebaseAuth auth;
    private ProgressDialog dialog;
    private EditText edtEmail,edtPassword;
    private DatabaseReference userDatabase;
    private Button btnLogin;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        edtEmail = (EditText)findViewById(R.id.edtLoginEmail);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        edtPassword = (EditText)findViewById(R.id.edtLoginPassword);

        btnLogin = (Button)findViewById(R.id.btnLoginUser);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startLogin();

            }
        });
    }

    private void startLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty()){
            Toast.makeText(this, "field can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()){
            Toast.makeText(this, "field can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.setMessage("Login you in.. please wait");
        dialog.setCancelable(false);
        dialog.show();


        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        String uid = auth.getCurrentUser().getUid();


                        userDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    String status = dataSnapshot.child("status").getValue().toString();

                                    if (status.equalsIgnoreCase("parent")){

                                        SharedPreferences.Editor edit = preferences.edit();
                                        edit.putString("status", "parent");
                                        edit.apply();

                                        dialog.dismiss();
                                        Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this, HomeTeacher.class));
                                    }else if (status.equalsIgnoreCase("teacher")){

                                        SharedPreferences.Editor edit = preferences.edit();
                                        edit.putString("status", "teacher");
                                        edit.apply();
                                        dialog.dismiss();
                                        Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this, TeachersHome.class));

                                    }else {
                                        SharedPreferences.Editor edit = preferences.edit();
                                        edit.putString("status", "admin");
                                        edit.apply();
                                        dialog.dismiss();
                                        Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this, AdminDashboard.class));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                dialog.dismiss();


                            }
                        });
                        dialog.dismiss();
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, HomeTeacher.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

    }


}
