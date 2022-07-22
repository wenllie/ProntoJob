package com.example.prontojob.uijs.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prontojob.R;
import com.example.prontojob.databinding.FragmentSettingsBinding;
import com.google.android.material.textview.MaterialTextView;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private MaterialTextView privacyPolicy, terms;

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        privacyPolicy = (MaterialTextView) root.findViewById(R.id.tvPrivacyPolicy);
        terms = (MaterialTextView) root.findViewById(R.id.tvTerms);

        privacyPolicy.setOnClickListener(this);
        terms.setOnClickListener(this);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvPrivacyPolicy:
                Dialog dialog = new Dialog(binding.getRoot().getContext());
                dialog.setContentView(R.layout.privacy_policy_modal);
                dialog.show();
                break;

            case R.id.tvTerms:
                Dialog tDialog = new Dialog(binding.getRoot().getContext());
                tDialog.setContentView(R.layout.terms_modal);
                tDialog.show();
                break;
        }
    }
}