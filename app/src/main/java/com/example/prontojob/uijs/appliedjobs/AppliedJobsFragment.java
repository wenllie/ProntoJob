package com.example.prontojob.uijs.appliedjobs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prontojob.R;
import com.example.prontojob.databinding.FragmentAppliedJobsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AppliedJobsFragment extends Fragment {

    private FragmentAppliedJobsBinding binding;
    private FirebaseUser user;
    private String userID;
    TextView jobTitles, jCategory, jType, jAddress, jMinSalary, jMaxSalary, jDescription;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAppliedJobsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        jobTitles = root.findViewById(R.id.jobTitles);
        jCategory = root.findViewById(R.id.jCategory);
        jType = root.findViewById(R.id.jType);
        jAddress = root.findViewById(R.id.jAddress);
        jMinSalary = root.findViewById(R.id.jMinSalary);
        jMaxSalary = root.findViewById(R.id.jMaxSalary);
        jDescription = root.findViewById(R.id.jDescription);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Job Seekers");

        reference.child(userID).child("Applied Jobs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String keyTitle = postSnapshot.getKey();
                    String keyValue = (String) postSnapshot.getValue();
                    if (keyTitle.equalsIgnoreCase("Description")) {
                        jDescription.setText(keyValue);
                    } else if (keyTitle.equalsIgnoreCase("Job Address")) {
                        jAddress.setText(keyValue);
                    } else if (keyTitle.equalsIgnoreCase("Job Type")) {
                        jType.setText(keyValue);
                    } else if (keyTitle.equalsIgnoreCase("Job Title")) {
                        jobTitles.setText(keyValue);
                    } else if (keyTitle.equalsIgnoreCase("Job Category")) {
                        jCategory.setText(keyValue);
                    } else if (keyTitle.equalsIgnoreCase("Salary From")) {
                        jMinSalary.setText(keyValue);
                    } else if (keyTitle.equalsIgnoreCase("Salary To")) {
                        jMaxSalary.setText(keyValue);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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