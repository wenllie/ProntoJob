package com.example.prontojob.uie.eprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prontojob.databinding.FragmentEProfileBinding;

public class EProfileFragment extends Fragment {

    private FragmentEProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}