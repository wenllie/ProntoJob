package com.example.prontojob.uijs.helpcenter;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prontojob.R;
import com.example.prontojob.databinding.FragmentHelpCenterBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class HelpCenterFragment extends Fragment implements View.OnClickListener {

    private MaterialButton jobApplications, forgotPass, jsFaqs, jsFB;

    private FragmentHelpCenterBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHelpCenterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        jobApplications = (MaterialButton) root.findViewById(R.id.jsAppliedJobs);
        forgotPass = (MaterialButton) root.findViewById(R.id.jsHCPassword);
        jsFaqs = (MaterialButton) root.findViewById(R.id.jsFAQs);
        jsFB = (MaterialButton) root.findViewById(R.id.jsFeedback);

        jobApplications.setOnClickListener(this);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jsAppliedJobs:
                Dialog jobApplication = new Dialog(binding.getRoot().getContext());
                jobApplication.setContentView(R.layout.job_application_modal);
                jobApplication.show();
                break;

            case R.id.jsHCPassword:
                Dialog pass = new Dialog(binding.getRoot().getContext());
                pass.setContentView(R.layout.forgot_password_modal);
                pass.show();
                break;

            case R.id.jsFAQs:
                Dialog faqs = new Dialog(binding.getRoot().getContext());
                faqs.setContentView(R.layout.faqs_modal);
                faqs.show();
                break;

            case R.id.jsFeedback:
                Dialog dialog = new Dialog(binding.getRoot().getContext());
                dialog.setContentView(R.layout.feedback_modal);
                dialog.show();
                break;
        }
    }
}