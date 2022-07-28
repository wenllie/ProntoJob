package com.example.prontojob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText email;
    private Button verify;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = (EditText) findViewById(R.id.jsFSEmail);
        verify = (Button) findViewById(R.id.btnVerify);

        mAuth = FirebaseAuth.getInstance();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailVerify;
                verifyEmail(emailVerify = email.getText().toString());
            }
        });
    }

    private void verifyEmail(String emailVerify) {
        mAuth.sendPasswordResetEmail(emailVerify).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    // Send verification email

                    Toast.makeText(ForgotPassword.this, "Email verification is sent!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPassword.this, JobseekerLogin.class));
                    finish();
                } else {
                    //Verification email is not sent

                    Toast.makeText(ForgotPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}