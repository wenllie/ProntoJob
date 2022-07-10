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

public class EmployerLogin extends AppCompatActivity implements View.OnClickListener{

    private EditText eEmail, ePassword;
    private Button eSignin;
    private TextView eRegister, eForgotPass;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_login);

        //initialize
        eEmail = (EditText) findViewById(R.id.eEmail);
        ePassword = (EditText) findViewById(R.id.ePassword);
        eSignin = (Button) findViewById(R.id.eSignin);
        eRegister = (TextView) findViewById(R.id.eRegister);
        eForgotPass = (TextView) findViewById(R.id.eForgotPass);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //employer homepage
        eSignin.setOnClickListener(this);

        //employer register page
        eRegister.setOnClickListener(this);

        //forgot password
        eForgotPass.setOnClickListener(this);
    }

    //function for routing to employer and/or employer homepage
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.eSignin:
                loginEmployer();
                break;
            case R.id.eRegister:
                startActivity(new Intent(EmployerLogin.this, EmployerRegister.class));
                finish();
                break;
        }
    }

    private void loginEmployer() {
        String Email = eEmail.getText().toString();
        String Password = ePassword.getText().toString();

        if (TextUtils.isEmpty(Email)){
            eEmail.setError("Email is required!");
            eEmail.requestFocus();
        } else if (TextUtils.isEmpty(Password)){
            ePassword.setError("Email is required!");
            ePassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(EmployerLogin.this, "User Logged in Successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(EmployerLogin.this, EmployerHome.class));
                        finish();
                    } else {
                        Toast.makeText(EmployerLogin.this, "Log in error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}