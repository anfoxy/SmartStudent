package com.example.studentapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.studentapp.R;


public class AnotherFragment extends Fragment {

    private ConstraintLayout aboutConstraint;
    private ConstraintLayout settingsConstraint;
    private ConstraintLayout contactConstraint;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        aboutConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_anotherFragment_to_aboutFragment);
            }
        });

        settingsConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_anotherFragment_to_settingFragment);
            }
        });

        contactConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_anotherFragment_to_contactFragment);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View anotherFrag = inflater.inflate(R.layout.fragment_another, container, false);
        aboutConstraint = anotherFrag.findViewById(R.id.constraint_about);
        settingsConstraint = anotherFrag.findViewById(R.id.constraint_settings);
        contactConstraint = anotherFrag.findViewById(R.id.constraint_contact);




        return anotherFrag;

    }
}