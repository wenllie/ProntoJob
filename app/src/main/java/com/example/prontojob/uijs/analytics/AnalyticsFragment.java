package com.example.prontojob.uijs.analytics;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.prontojob.R;
import com.example.prontojob.databinding.FragmentAnalyticsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AnalyticsFragment extends Fragment {

    private FragmentAnalyticsBinding binding;

    TextView jsTotal, eTotal;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        jsTotal = (TextView) root.findViewById(R.id.jsJSTotal);
        eTotal = (TextView) root.findViewById(R.id.jsETotal);

        //extracting user reference from database
        DatabaseReference jobSeekersRef = FirebaseDatabase.getInstance().getReference("Registered Job Seekers");
        DatabaseReference employersRef = FirebaseDatabase.getInstance().getReference("Registered Employers");

        jobSeekersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                jsTotal.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        employersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                eTotal.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
    }
}