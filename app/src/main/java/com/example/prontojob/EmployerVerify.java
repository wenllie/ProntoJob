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

public class EmployerVerify extends AppCompatActivity implements View.OnClickListener {

    EditText eBirthday, eAge, ePhoneNumber, eAddress;
    Button eDate, btnVerify;
    Spinner eIDType;
    ImageButton eEmpCert;
    private int mYear, mMonth, mDay;
    public static String eUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_verify);

        //initialize variables
        eBirthday = (EditText) findViewById(R.id.eBirthday);
        eAge = (EditText) findViewById(R.id.eAge);
        ePhoneNumber = (EditText) findViewById(R.id.ephonenumber);
        eAddress = (EditText) findViewById(R.id.eAddress);
        eIDType = (Spinner) findViewById(R.id.eIDType);
        eDate = (Button) findViewById(R.id.eButtonDate);
        btnVerify = (Button) findViewById(R.id.ebtnVerify);

        btnVerify.setOnClickListener(this);
        eDate.setOnClickListener(this);

        ArrayAdapter<CharSequence> idAdapter = ArrayAdapter.createFromResource(this,
                R.array.id_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        idAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        eIDType.setAdapter(idAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ebtnVerify:
                employerVerify();
                break;

            case R.id.eButtonDate:
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
                                eBirthday.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
        }
    }

    private void employerVerify() {

        String birthday = eBirthday.getText().toString();
        String age = eAge.getText().toString();
        String phoneNumber = ePhoneNumber.getText().toString();
        String address = eAddress.getText().toString();
        String idTy = eIDType.getSelectedItem().toString();

        //verify that edit text fields are not empty
        if (birthday.isEmpty()) {
            eBirthday.setError("First name is required!");
            eBirthday.requestFocus();
            return;
        }
        if (age.isEmpty()) {
            eAge.setError("First name is required!");
            eAge.requestFocus();
            return;
        }
        if (phoneNumber.isEmpty()) {
            ePhoneNumber.setError("First name is required!");
            ePhoneNumber.requestFocus();
            return;
        }
        if (address.isEmpty()) {
            eAddress.setError("First name is required!");
            eAddress.requestFocus();
            return;
        }

        //
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Employers");

        reference.child(eUserID).child("Information").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    reference.child(eUserID).child("Information").child("Birthday").push();
                    reference.child(eUserID).child("Information").child("Age").push();
                    reference.child(eUserID).child("Information").child("Phone Number").push();
                    reference.child(eUserID).child("Information").child("Address").push();
                    reference.child(eUserID).child("Information").child("ID Type").push();

                    reference.child(eUserID).child("Information").child("Birthday").setValue(birthday);
                    reference.child(eUserID).child("Information").child("Age").setValue(age);
                    reference.child(eUserID).child("Information").child("Phone Number").setValue(phoneNumber);
                    reference.child(eUserID).child("Information").child("Address").setValue(address);
                    reference.child(eUserID).child("Information").child("ID Type").setValue(idTy);

                    startActivity(new Intent(EmployerVerify.this, EmployerHome.class));
                    finish();
                }
            }
        });
    }
}