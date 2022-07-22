package com.example.prontojob.uie.postjob;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prontojob.databinding.FragmentEPostedJobBinding;

public class EPostedJobFragment extends Fragment {

    private FragmentEPostedJobBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEPostedJobBinding.inflate(inflater, container, false);
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