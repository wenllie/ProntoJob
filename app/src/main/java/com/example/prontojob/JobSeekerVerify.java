package com.example.prontojob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class JobSeekerVerify extends AppCompatActivity implements View.OnClickListener {

    EditText jsBirthday, jsAge, jsPhoneNumber, jsAddress;
    Button jsDate, btnVerify;
    Spinner jsExperience, jsIDType;
    ImageButton jsResume;
    private int mYear, mMonth, mDay;
    public static String userID;

       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_seeker_verify);

        //initialize variables
        jsBirthday = (EditText) findViewById(R.id.jsBirthday);
        jsAge = (EditText) findViewById(R.id.jsAge);
        jsPhoneNumber = (EditText) findViewById(R.id.jsphonenumber);
        jsAddress = (EditText) findViewById(R.id.jsAddress);
        jsExperience = (Spinner) findViewById(R.id.jsExperience);
        jsIDType = (Spinner) findViewById(R.id.jsIDType);
        jsDate = (Button) findViewById(R.id.jsButtonDate);
        btnVerify = (Button) findViewById(R.id.jsbtnVerify);

        btnVerify.setOnClickListener(this);
        jsDate.setOnClickListener(this);

           ArrayAdapter<CharSequence> experienceAdapter = ArrayAdapter.createFromResource(this,
                   R.array.experience, android.R.layout.simple_spinner_item);
           // Specify the layout to use when the list of choices appears
           experienceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
           jsExperience.setAdapter(experienceAdapter);

           ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                   R.array.id_type, android.R.layout.simple_spinner_item);
           // Specify the layout to use when the list of choices appears
           typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
           // Apply the adapter to the spinner
           jsIDType.setAdapter(typeAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jsbtnVerify:
                verifyUser();
                break;
            case R.id.jsButtonDate:
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                jsBirthday.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
        }
    }

    private void verifyUser() {
        String birthday = jsBirthday.getText().toString();
        String age = jsAge.getText().toString();
        String phoneNumber = jsPhoneNumber.getText().toString();
        String address = jsAddress.getText().toString();
        String exp = jsExperience.getSelectedItem().toString();
        String idTy = jsIDType.getSelectedItem().toString();

        //verify that edit text fields are not empty
        if (birthday.isEmpty()) {
            jsBirthday.setError("First name is required!");
            jsBirthday.requestFocus();
            return;
        }
        if (age.isEmpty()) {
            jsAge.setError("First name is required!");
            jsAge.requestFocus();
            return;
        }
        if (phoneNumber.isEmpty()) {
            jsPhoneNumber.setError("First name is required!");
            jsPhoneNumber.requestFocus();
            return;
        }
        if (address.isEmpty()) {
            jsAddress.setError("First name is required!");
            jsAddress.requestFocus();
            return;
        }

        //
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Job Seekers");

        reference.child(userID).child("Information").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    reference.child(userID).child("Information").child("Birthday").push();
                    reference.child(userID).child("Information").child("Age").push();
                    reference.child(userID).child("Information").child("Phone Number").push();
                    reference.child(userID).child("Information").child("Address").push();
                    reference.child(userID).child("Information").child("Experience").push();
                    reference.child(userID).child("Information").child("ID Type").push();

                    reference.child(userID).child("Information").child("Birthday").setValue(birthday);
                    reference.child(userID).child("Information").child("Age").setValue(age);
                    reference.child(userID).child("Information").child("Phone Number").setValue(phoneNumber);
                    reference.child(userID).child("Information").child("Address").setValue(address);
                    reference.child(userID).child("Information").child("Experience").setValue(exp);
                    reference.child(userID).child("Information").child("ID Type").setValue(idTy);

                    startActivity(new Intent(JobSeekerVerify.this, JobseekerHome.class));
                    finish();
                }
            }
        });
    }
}