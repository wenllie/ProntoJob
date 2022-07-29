package com.example.prontojob.uijs.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prontojob.R;
import com.example.prontojob.UserDetails;
import com.example.prontojob.databinding.FragmentProfileBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    MaterialTextView firstName, lastName;
    TextView type, experience;
    EditText age, phonenumber, address;
    TextInputEditText birthday;
    Button btnSave;
    public static String userID;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //initialize
        firstName = (MaterialTextView) root.findViewById(R.id.jstvFirstName);
        lastName = (MaterialTextView) root.findViewById(R.id.jstvLastName);
        type = (TextView) root.findViewById(R.id.jsVIDType);
        experience = (TextView) root.findViewById(R.id.jsVExperience);
        birthday = (TextInputEditText) root.findViewById(R.id.jsVBirthday);
        age = (EditText) root.findViewById(R.id.jsVAge);
        phonenumber = (EditText) root.findViewById(R.id.jsVPhoneNumber);
        address = (EditText) root.findViewById(R.id.jsVAddress);
        btnSave = (Button) root.findViewById(R.id.jsSave);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //get the user ID of th user
        userID = user.getUid();

        //extracting user reference from database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Job Seekers");

        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userWelcome = snapshot.getValue(UserDetails.class);
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (userWelcome != null) {
                    String fname = userWelcome.firstname;
                    String lname = userWelcome.lastname;

                    firstName.setText(fname);
                    lastName.setText(lname);
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
                        birthday.setText(valueTitle);
                    } else if (keyTitle.equalsIgnoreCase("Age")) {
                        age.setText(valueTitle);
                    } else if (keyTitle.equalsIgnoreCase("Phone Number")) {
                        phonenumber.setText(valueTitle);
                    } else if (keyTitle.equalsIgnoreCase("Address")) {
                        address.setText(valueTitle);
                    } else if (keyTitle.equalsIgnoreCase("ID Type")) {
                        type.setText(valueTitle);
                    } else if (keyTitle.equalsIgnoreCase("Experience")) {
                        experience.setText(valueTitle);
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