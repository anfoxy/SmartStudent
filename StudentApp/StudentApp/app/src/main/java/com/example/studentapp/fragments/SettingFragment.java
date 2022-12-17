package com.example.studentapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.studentapp.R;


public class SettingFragment extends Fragment {


    public SettingFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View settingFragment = inflater.inflate(R.layout.fragment_setting, container, false);
        return settingFragment;
    }
}