package com.example.prontojob.uijs.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prontojob.R;
import com.example.prontojob.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    EditText jobAdd;
    Button findJob, viewJob, btnApply;
    TextView jCategory, jTitle, jDescription, jminSalary, jmaxSalary, jAddress, jType;
    public static String userID;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //initialize
        jobAdd = (EditText) root.findViewById(R.id.jsCity);
        findJob = (Button) root.findViewById(R.id.jsbtnFind);
        viewJob = (Button) root.findViewById(R.id.btnViewJob);
        btnApply = (Button) root.findViewById(R.id.btnApply);
        jCategory = (TextView) root.findViewById(R.id.jCategory);
        jTitle = (TextView) root.findViewById(R.id.jobTitles);
        jDescription = (TextView) root.findViewById(R.id.jDescription);
        jminSalary = (TextView) root.findViewById(R.id.jMinSalary);
        jmaxSalary = (TextView) root.findViewById(R.id.jMaxSalary);
        jAddress = (TextView) root.findViewById(R.id.jAddress);
        jType = (TextView) root.findViewById(R.id.jType);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //get the user ID of th user
        userID = user.getUid();

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //extracting user reference from database
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Job Seekers");

                reference.child(userID).child("Applied Jobs").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            reference.child(userID).child("Applied Jobs").child("Job Title").setValue(jTitle.getText().toString());
                            reference.child(userID).child("Applied Jobs").child("Job Category").setValue(jCategory.getText().toString());
                            reference.child(userID).child("Applied Jobs").child("Job Type").setValue(jType.getText().toString());
                            reference.child(userID).child("Applied Jobs").child("Job Address").setValue(jAddress.getText().toString());
                            reference.child(userID).child("Applied Jobs").child("Salary From").setValue(jminSalary.getText().toString());
                            reference.child(userID).child("Applied Jobs").child("Salary To").setValue(jmaxSalary.getText().toString());
                            reference.child(userID).child("Applied Jobs").child("Description").setValue(jDescription.getText().toString());

                            Toast.makeText(root.getContext(), "Applied Successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        viewJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (jType.getVisibility() == View.INVISIBLE) {
                    jType.setVisibility(View.VISIBLE);
                    jAddress.setVisibility(View.VISIBLE);
                    jminSalary.setVisibility(View.VISIBLE);
                    jmaxSalary.setVisibility(View.VISIBLE);
                    jDescription.setVisibility(View.VISIBLE);
                    btnApply.setVisibility(View.VISIBLE);
                } else {
                    jType.setVisibility(View.INVISIBLE);
                    jAddress.setVisibility(View.INVISIBLE);
                    jminSalary.setVisibility(View.INVISIBLE);
                    jmaxSalary.setVisibility(View.INVISIBLE);
                    jDescription.setVisibility(View.INVISIBLE);
                    btnApply.setVisibility(View.INVISIBLE);
                }
            }
        });

        findJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = jobAdd.getText().toString();

                if (address.isEmpty()) {
                    jobAdd.setError("City is required!");
                    jobAdd.requestFocus();
                    return;
                }

                viewJob.setVisibility(View.INVISIBLE);
                jTitle.setVisibility(View.INVISIBLE);
                jCategory.setVisibility(View.INVISIBLE);
                jType.setVisibility(View.INVISIBLE);
                jAddress.setVisibility(View.INVISIBLE);
                jminSalary.setVisibility(View.INVISIBLE);
                jmaxSalary.setVisibility(View.INVISIBLE);
                jDescription.setVisibility(View.INVISIBLE);
                btnApply.setVisibility(View.INVISIBLE);

                //extracting user reference from database
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered Employers");

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot childSnap : postSnapshot.getChildren()) {
                                String key = childSnap.getKey();
                                if (key.equalsIgnoreCase("Job Posting")) {
                                    for (DataSnapshot childSnapz : childSnap.getChildren()) {
                                        String keyNumber = childSnapz.getKey();
                                        for (DataSnapshot childnumber : childSnapz.getChildren()) {
                                            String keyz = (String) childnumber.getValue();
                                            if (keyz.equalsIgnoreCase(address)) {
                                                for (DataSnapshot getChild : childSnapz.getChildren()) {
                                                    String getKey = getChild.getKey();
                                                    String getValue = (String) getChild.getValue();
                                                    if (getKey.equalsIgnoreCase("jobAddress")) {
                                                        jAddress.setText(getValue);
                                                    } else if (getKey.equalsIgnoreCase("jobCategory")) {
                                                        jCategory.setText(getValue);
                                                    } else if (getKey.equalsIgnoreCase("jobDescription")) {
                                                        jDescription.setText(getValue);
                                                    } else if (getKey.equalsIgnoreCase("jobTitle")) {
                                                        jTitle.setText(getValue);
                                                    } else if (getKey.equalsIgnoreCase("jobType")) {
                                                        jType.setText(getValue);
                                                    } else if (getKey.equalsIgnoreCase("maxSalary")) {
                                                        jmaxSalary.setText(getValue);
                                                    } else if (getKey.equalsIgnoreCase("minsalary")) {
                                                        jminSalary.setText(getValue);
                                                    }
                                                }
                                                jAddress.setVisibility(View.VISIBLE);
                                                jCategory.setVisibility(View.VISIBLE);
                                                jDescription.setVisibility(View.VISIBLE);
                                                jTitle.setVisibility(View.VISIBLE);
                                                jType.setVisibility(View.VISIBLE);
                                                jmaxSalary.setVisibility(View.VISIBLE);
                                                jminSalary.setVisibility(View.VISIBLE);
                                                btnApply.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}