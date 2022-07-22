package com.example.prontojob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmployerRegister extends AppCompatActivity implements View.OnClickListener{

    //declare
    private EditText efirstname, elastname, eEmail, ePassword, eConfirmPass;
    private Button eregister;
    private TextView elogin;
    private ProgressBar eprogressBar;

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_register);

        //initialize
        efirstname = (EditText) findViewById(R.id.eregFirstname);
        elastname = (EditText) findViewById(R.id.eregLastname);
        eEmail = (EditText) findViewById(R.id.eRegEmail);
        ePassword = (EditText) findViewById(R.id.eRegPass);
        eConfirmPass = (EditText) findViewById(R.id.eRegConfirmPass);
        eregister = (Button) findViewById(R.id.ebtnRegister);
        elogin = (TextView) findViewById(R.id.eLogin);
        eprogressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //employer home page
        eregister.setOnClickListener(this);

        //employer signin page
        elogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ebtnRegister:
                createEmployerUser();
                break;

            case R.id.eLogin:
                startActivity(new Intent(EmployerRegister.this, EmployerHome.class));
                finish();
                break;
        }
    }

    private void createEmployerUser() {

        //extract data from edit text
        String firstName = efirstname.getText().toString();
        String lastName = elastname.getText().toString();
        String Email = eEmail.getText().toString();
        String Pass = ePassword.getText().toString();
        String confPass = eConfirmPass.getText().toString();

        //verify that edit text fields are not empty
        if (firstName.isEmpty()) {
            efirstname.setError("First name is required!");
            efirstname.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            elastname.setError("Last name is required!");
            elastname.requestFocus();
            return;
        }
        if (Email.isEmpty()) {
            eEmail.setError("Email is required!");
            eEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {         //verify that email address is a valid email
            eEmail.setError("Please enter a valid email address.");
            eEmail.requestFocus();
            return;
        }
        if (Pass.isEmpty()) {
            ePassword.setError("Password is required!");
            ePassword.requestFocus();
            return;
        }
        if (confPass.isEmpty()) {
            eConfirmPass.setError("Confirm password is required!");
            eConfirmPass.requestFocus();
            return;
        }
        if (!confPass.equals(Pass)) {         //verify that password and confirm password is the same
            eConfirmPass.setError("Password do not match!");
            eConfirmPass.requestFocus();
            return;
        }
        if (Pass.equals(confPass)){
            //Create employer profile
            mAuth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user =  mAuth.getCurrentUser();

                        //store user data into realtime database
                        UserDetails userDetails = new UserDetails(firstName, lastName);

                        //extracting user reference from database for "Registered Employers"
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Employers");

                        reference.child(user.getUid()).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    //send email verification
                                    user.sendEmailVerification();

                                    Toast.makeText(EmployerRegister.this,"Account Registered Successfully! Please verify your email.", Toast.LENGTH_LONG).show();

                                    // to get the user ID for verifying user
                                    EmployerVerify.eUserID = user.getUid();

                                    //login user after successful registration
                                    Intent login = new Intent(EmployerRegister.this, EmployerVerify.class);
                                    //prevent user from returning back to the register page on pressing back button after registration
                                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(login);
                                    finish();
                                } else {
                                    Toast.makeText(EmployerRegister.this,"Registration failed! Please try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            ePassword.setError("Password is too weak! Please use combination of alphabets, numbers and special characters.");
                            ePassword.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            eEmail.setError("Email is invalid or already in use! Please use another email.");
                            eEmail.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            eEmail.setError("User already registered with this email! Please use another email.");
                            eEmail.requestFocus();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(EmployerRegister.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });
        }
    }
}