package com.example.prontojob.uie.ehelp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prontojob.databinding.FragmentEHelpCenterBinding;

public class EHelpCenterFragment extends Fragment {

    private FragmentEHelpCenterBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEHelpCenterBinding.inflate(inflater, container, false);
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