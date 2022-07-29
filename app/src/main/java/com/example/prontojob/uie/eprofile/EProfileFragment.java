package com.example.prontojob.uie.eprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prontojob.R;
import com.example.prontojob.UserDetails;
import com.example.prontojob.databinding.FragmentEProfileBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EProfileFragment extends Fragment {

    private FragmentEProfileBinding binding;

    MaterialTextView efirstName, elastName;
    TextView etype;
    EditText eage, ephonenumber, eaddress;
    TextInputEditText ebirthday;
    Button ebtnSave;
    public static String userID;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //initialize
        efirstName = (MaterialTextView) root.findViewById(R.id.etvFirstName);
        elastName = (MaterialTextView) root.findViewById(R.id.etvLastName);
        etype = (TextView) root.findViewById(R.id.eDBIDType);
        ebirthday = (TextInputEditText) root.findViewById(R.id.jsDbBirthday);
        eage = (EditText) root.findViewById(R.id.jsDbAge);
        ephonenumber = (EditText) root.findViewById(R.id.jsDbPhoneNumber);
        eaddress = (EditText) root.findViewById(R.id.jsDbAddress);
        ebtnSave = (Button) root.findViewById(R.id.eSave);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //get the user ID of th user
        userID = user.getUid();

        //extracting user reference from database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Employers");

        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userWelcome = snapshot.getValue(UserDetails.class);
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (userWelcome != null) {
                    String fname = userWelcome.firstname;
                    String lname = userWelcome.lastname;

                    efirstName.setText(fname);
                    elastName.setText(lname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child(userID).child("Information").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapTitle : snapshot.getChildren()) {

                    String keyTitle = snapTitle.getKey();
                    String valueTitle = (String) snapTitle.getValue();

                    if (keyTitle.equalsIgnoreCase("Birthday")) {
                        ebirthday.setText(valueTitle);
                    } else if (keyTitle.equalsIgnoreCase("Age")) {
                        eage.setText(valueTitle);
                    } else if (keyTitle.equalsIgnoreCase("Phone Number")) {
                        ephonenumber.setText(valueTitle);
                    } else if (keyTitle.equalsIgnoreCase("Address")) {
                        eaddress.setText(valueTitle);
                    } else if (keyTitle.equalsIgnoreCase("ID Type")) {
                        etype.setText(valueTitle);
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