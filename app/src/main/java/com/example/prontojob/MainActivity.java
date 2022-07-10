package com.example.prontojob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button jobseeker, employer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize button
        jobseeker = (Button) findViewById(R.id.btnJobSeeker);
        employer = (Button) findViewById(R.id.btnEmployer);

        //jobseeker login page
        jobseeker.setOnClickListener(this);

        //employer login page
        employer.setOnClickListener(this);

    }

    //function for routing to job seeker and/or employer page
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnJobSeeker:
                startActivity(new Intent(MainActivity.this, JobseekerLogin.class));
                break;
            case R.id.btnEmployer:
                startActivity(new Intent(MainActivity.this, EmployerLogin.class));
                break;
        }
    }
}