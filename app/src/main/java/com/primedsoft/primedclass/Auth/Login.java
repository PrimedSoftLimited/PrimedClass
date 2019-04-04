package com.primedsoft.primedclass.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.primedsoft.primedclass.Activity.HomeTeacher;
import com.primedsoft.primedclass.R;

public class Login extends AppCompatActivity {

    private FirebaseAuth auth;
    private ProgressDialog dialog;
    private EditText edtEmail,edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        edtEmail = (EditText)findViewById(R.id.edtLoginEmail);
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
                        dialog.dismiss();
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, HomeTeacher.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
