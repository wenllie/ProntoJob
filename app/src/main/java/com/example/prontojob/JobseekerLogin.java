package com.example.prontojob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class JobseekerLogin extends AppCompatActivity implements View.OnClickListener{

    private EditText email, password;
    private Button signin;
    private TextView register, forgotPass;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobseeker_login);

        //initialize
        email = (EditText) findViewById(R.id.jsEmail);
        password = (EditText) findViewById(R.id.jsPassword);
        signin = (Button) findViewById(R.id.jsSignin);
        register = (TextView) findViewById(R.id.jsRegister);
        forgotPass = (TextView) findViewById(R.id.jsForgotPass);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //jobseeker homepage
        signin.setOnClickListener(this);

        //jobseeker register page
        register.setOnClickListener(this);

        //forgot password
        forgotPass.setOnClickListener(this);

        forgotPass.setOnClickListener(this);
    }

    //function for routing to jobseeker and/or jobseeker homepage
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.jsSignin:
                loginUser();
                break;
            case R.id.jsRegister:
                startActivity(new Intent(JobseekerLogin.this, JobseekerRegister.class));
                finish();
                break;
            case R.id.jsForgotPass:
                startActivity(new Intent(JobseekerLogin.this, ForgotPassword.class));
                finish();
                break;
        }
    }

    private void loginUser() {
        String Email = email.getText().toString();
        String Password = password.getText().toString();

        if (TextUtils.isEmpty(Email)){
            email.setError("Email is required!");
            email.requestFocus();
        } else if (TextUtils.isEmpty(Password)){
            password.setError("Email is required!");
            password.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        if (mAuth.getCurrentUser().isEmailVerified()){
                            Toast.makeText(JobseekerLogin.this, "User Logged in Successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(JobseekerLogin.this, JobseekerHome.class));
                            finish();
                        } else {
                            Toast.makeText(JobseekerLogin.this, "Please verify your email address!", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                        }
                    } else {
                        Toast.makeText(JobseekerLogin.this, "Log in error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}