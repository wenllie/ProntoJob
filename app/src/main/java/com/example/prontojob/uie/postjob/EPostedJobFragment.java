package com.example.prontojob.uie.postjob;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prontojob.PostJobDetails;
import com.example.prontojob.UserDetails;
import com.example.prontojob.databinding.FragmentEPostedJobBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.prontojob.R;

public class EPostedJobFragment extends Fragment {

    public static String userID;

    TextView firstName, lastName, category, title, type, address, miniSal, maxiSal, desc;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private FragmentEPostedJobBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEPostedJobBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //initialize
        firstName = (TextView) root.findViewById(R.id.appliedFirstname);
        lastName = (TextView) root.findViewById(R.id.appliedLastname);
        category = (TextView) root.findViewById(R.id.jaJobCategory);
        title = (TextView) root.findViewById(R.id.jaJobTitle);
        type = (TextView) root.findViewById(R.id.jaJobType);
        address = (TextView) root.findViewById(R.id.jaJobAddress);
        miniSal = (TextView) root.findViewById(R.id.jaJobminSalary);
        maxiSal = (TextView) root.findViewById(R.id.jaJobmaxSalary);
        desc = (TextView) root.findViewById(R.id.jaJobDescription);


        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //get the user ID of th user
        userID = user.getUid();

        //extracting user reference from database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Job Seekers");

        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userJS = snapshot.getValue(UserDetails.class);
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (userJS != null) {
                    String fname = userJS.firstname;
                    String lname = userJS.lastname;

                    firstName.setText(fname);
                    lastName.setText(lname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(root.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        int key = 0;
        //extracting user reference from database
        DatabaseReference empReference = FirebaseDatabase.getInstance().getReference("Registered Employers");
        empReference.child(userID).child("Job Posting").child(String.valueOf(key)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                PostJobDetails postJobDetails = snapshot.getValue(PostJobDetails.class);
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (postJobDetails != null) {
                    String jCategory = postJobDetails.jobCategory;
                    String jTitle = postJobDetails.jobTitle;
                    String jType = postJobDetails.jobType;
                    String jAddress = postJobDetails.jobAddress;
                    String jMinSalary = postJobDetails.minSalary;
                    String jMaxSalary = postJobDetails.maxSalary;
                    String jDescription = postJobDetails.jobDescription;

                    category.setText(jCategory);
                    title.setText(jTitle);
                    type.setText(jType);
                    address.setText(jAddress);
                    miniSal.setText(jMinSalary);
                    maxiSal.setText(jMaxSalary);
                    desc.setText(jDescription);
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