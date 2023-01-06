package com.example.studentapp.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.studentapp.AuthActivity;
import com.example.studentapp.LaunchActivity;
import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.Users;


public class AnotherFragment extends Fragment {

    private ConstraintLayout aboutConstraint;
    private ConstraintLayout loginConstraint;
    private ConstraintLayout contactConstraint;
    private ConstraintLayout outConstraint;
    private ConstraintLayout friendConstraint;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        aboutConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_anotherFragment_to_aboutFragment);
            }
        });

        loginConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_anotherFragment_to_accountFragment);
            }
        });

        contactConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_anotherFragment_to_contactFragment);
            }
        });
        friendConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_anotherFragment_to_friendsFragment);
            }
        });

        outConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                out();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View anotherFrag = inflater.inflate(R.layout.fragment_another, container, false);
        aboutConstraint = anotherFrag.findViewById(R.id.constraint_about);
        loginConstraint = anotherFrag.findViewById(R.id.constraint_login);
        contactConstraint = anotherFrag.findViewById(R.id.constraint_contact);
        outConstraint = anotherFrag.findViewById(R.id.constraint_out);
        friendConstraint = anotherFrag.findViewById(R.id.constraint_friend);
        return anotherFrag;
    }

    private void out(){

        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());

        // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.dialog_out,
                        null);
        builder.setView(customLayout);



        AlertDialog dialog
                = builder.create();


        Button out = customLayout.findViewById(R.id.out_acc);
        AppCompatButton clsBtn = customLayout.findViewById(R.id.cancel_window);

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.myDBManager.deleteAllSub();
                Users.deleteUser();
                Intent intent = new Intent(getActivity(), LaunchActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        clsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        dialog.show();

    }
}