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

public class JobseekerRegister extends AppCompatActivity implements View.OnClickListener{

    //declare
    private EditText firstname, lastname, jsEmail, jsPassword, confirmPass;
    private Button register;
    private TextView login;
    private ProgressBar progressBar;

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobseeker_register);

        //initialize
        firstname = (EditText) findViewById(R.id.jsregFirstname);
        lastname = (EditText) findViewById(R.id.jsregLastname);
        jsEmail = (EditText) findViewById(R.id.jsRegEmail);
        jsPassword = (EditText) findViewById(R.id.jsRegPass);
        confirmPass = (EditText) findViewById(R.id.jsRegConfirmPass);
        register = (Button) findViewById(R.id.jsbtnRegister);
        login = (TextView) findViewById(R.id.jsLogin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //jobseeker home page
        register.setOnClickListener(this);

        //jobseeker signin page
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jsbtnRegister:
                createUser();
                break;

            case R.id.jsLogin:
                startActivity(new Intent(JobseekerRegister.this, JobseekerLogin.class));
                finish();
                break;
        }
    }

    private void createUser() {
        //extract data from edit text
        String firstName = firstname.getText().toString();
        String lastName = lastname.getText().toString();
        String Email = jsEmail.getText().toString();
        String Pass = jsPassword.getText().toString();
        String confPass = confirmPass.getText().toString();

        //verify that edit text fields are not empty
        if (firstName.isEmpty()) {
            firstname.setError("First name is required!");
            firstname.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            lastname.setError("Last name is required!");
            lastname.requestFocus();
            return;
        }
        if (Email.isEmpty()) {
            jsEmail.setError("Email is required!");
            jsEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {         //verify that email address is a valid email
            jsEmail.setError("Please enter a valid jsEmail address.");
            jsEmail.requestFocus();
            return;
        }
        if (Pass.isEmpty()) {
            jsPassword.setError("Password is required!");
            jsPassword.requestFocus();
            return;
        }
        if (confPass.isEmpty()) {
            confirmPass.setError("Confirm Password is required!");
            confirmPass.requestFocus();
            return;
        }
        if (!confPass.equals(Pass)) {         //verify that password and confirm password is the same
            confirmPass.setError("Password do not match!");
            confirmPass.requestFocus();
            confirmPass.setText("");
            jsPassword.setText("");
            return;
        }
        if (Pass.equals(confPass)){
            //Create job seeker profile
            mAuth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user =  mAuth.getCurrentUser();



                        //store user data into realtime database
                        UserDetails userDetails = new UserDetails(firstName, lastName);

                        //extracting user reference from database for "Registered Job Seekers"
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Job Seekers");

                        reference.child(user.getUid()).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    //send email verification
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(JobseekerRegister.this,"Account Registered Successfully! Please verify your email.", Toast.LENGTH_LONG).show();

                                                // to get the user ID for verifying user
                                                JobSeekerVerify.userID = user.getUid();

                                                //login user after successful registration
                                                Intent login = new Intent(JobseekerRegister.this, JobSeekerVerify.class);
                                                //prevent user from returning back to the register page on pressing back button after registration
                                                login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(login);
                                                finish();
                                            } else {
                                                Toast.makeText(JobseekerRegister.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                } else {
                                    Toast.makeText(JobseekerRegister.this,"Registration failed! Please try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            jsPassword.setError("Password is too weak! Please use combination of alphabets, numbers and special characters.");
                            jsPassword.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            jsEmail.setError("Email is invalid or already in use! Please use another email.");
                            jsEmail.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            jsEmail.setError("User already registered with this email! Please use another email.");
                            jsEmail.requestFocus();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(JobseekerRegister.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });
        }

    }

}