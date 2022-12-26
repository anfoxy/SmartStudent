package com.example.studentapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.studentapp.databinding.ActivityLaunchBinding;
import com.example.studentapp.db.Users;
import com.example.studentapp.db_local.MyDBManager;

import io.paperdb.Paper;

public class LaunchActivity extends AppCompatActivity {

    ActivityLaunchBinding binding;
    public static MyDBManager myDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        myDBManager = new MyDBManager(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_launch);

        Paper.init(this);

        Animation animationStart = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);

        binding.logoLaunch.startAnimation(animationStart);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myDBManager.openDB();
                Users user = Users.getUser();
                if (user == null){
                    Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }else {
                    Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intentMain);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }
        },3000);
    }
}