package com.example.prontojob.uie.ehome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prontojob.databinding.FragmentEHomeBinding;

public class EHomeFragment extends Fragment {

    private FragmentEHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEHomeBinding.inflate(inflater, container, false);
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