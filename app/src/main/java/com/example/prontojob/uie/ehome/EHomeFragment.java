package com.example.prontojob.uie.ehome;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prontojob.PostJobDetails;
import com.example.prontojob.R;
import com.example.prontojob.UserDetails;
import com.example.prontojob.databinding.FragmentEHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class EHomeFragment extends Fragment {

    //declare
    private FragmentEHomeBinding binding;
    EditText jobTitle, jobAddress, fromSalary, toSalary, jobDescription;
    Button postJob;
    Spinner jobCategory, jobType;
    public static String userID;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //initialize
        jobTitle = (EditText) root.findViewById(R.id.eJobTitle);
        jobAddress = (EditText) root.findViewById(R.id.jobAddress);
        fromSalary = (EditText) root.findViewById(R.id.starting);
        toSalary = (EditText) root.findViewById(R.id.ending);
        jobDescription = (EditText) root.findViewById(R.id.description);
        jobCategory = (Spinner) root.findViewById(R.id.eCategory);
        jobType = (Spinner) root.findViewById(R.id.eJobType);
        postJob = (Button) root.findViewById(R.id.btnPostJob);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //get the user ID of th user
        userID = user.getUid();

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(root.getContext(),
                R.array.jobCategories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        jobCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(root.getContext(),
                R.array.jobType, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        jobType.setAdapter(typeAdapter);

        postJob.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String category = jobCategory.getSelectedItem().toString();
                String title = jobTitle.getText().toString();
                String type = jobType.getSelectedItem().toString();
                String address = jobAddress.getText().toString();
                String minSalary = fromSalary.getText().toString();
                String maxSalary = toSalary.getText().toString();
                String desc = jobDescription.getText().toString();

                //verify that edit text fields are not empty
                if (category.isEmpty()) {
                    jobCategory.requestFocus();
                    return;
                }
                if (type.isEmpty()) {
                    jobType.requestFocus();
                    return;
                }
                if (title.isEmpty()) {
                    jobTitle.setError("Job Title is required!");
                    jobTitle.requestFocus();
                    return;
                }
                if (address.isEmpty()) {
                    jobAddress.setError("Address is required!");
                    jobAddress.requestFocus();
                    return;
                }
                if (minSalary.isEmpty()) {
                    fromSalary.setError("Minimum Salary is required!");
                    fromSalary.requestFocus();
                    return;
                }
                if (maxSalary.isEmpty()) {
                    toSalary.setError("Maximum Salary is required!");
                    toSalary.requestFocus();
                    return;
                }
                if (desc.isEmpty()) {
                    jobDescription.setError("Job Description is required!");
                    jobDescription.requestFocus();
                    return;
                }

                //store user data into realtime database
                PostJobDetails jobDetails = new PostJobDetails(category, title, type, address, minSalary, maxSalary, desc);

                //extracting user reference from database
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Employers");

                Random rand = new Random();
                // Obtain a number between [0 - 99999].
                int n = rand.nextInt(99999);

                reference.child(userID).child("Job Posting").child(String.valueOf(n)).setValue(jobDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            clearField();

                        }
                    }
                });
            }
        });

        return root;
    }

    private void clearField() {

        jobCategory.setSelection(0);
        jobTitle.setText("");
        jobType.setSelection(0);
        jobAddress.setText("");
        fromSalary.setText("");
        toSalary.setText("");
        jobDescription.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}