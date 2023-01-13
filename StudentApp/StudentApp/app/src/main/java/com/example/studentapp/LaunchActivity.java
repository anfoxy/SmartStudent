package com.example.studentapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.databinding.ActivityLaunchBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;
import com.example.studentapp.db_local.MyDBManager;

import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaunchActivity extends AppCompatActivity {

    ActivityLaunchBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        MainActivity.myDBManager = new MyDBManager(this);
        MainActivity.myDBManager.openDB();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_launch);

        Paper.init(this);

        Animation animationStart = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);

        binding.logoLaunch.startAnimation(animationStart);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Users user = Users.getUser();
                if (user == null){
                    Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }else {
                    if(isNetworkWorking()) {
                        updateDBTime();
                    }else {
                        nextDay();
                        finishAnimation();
                    }
                }
            }
        },2000);
    }

    private boolean isNetworkWorking(){
        try {
            ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (manager != null){
                networkInfo = manager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();
        }catch (NullPointerException ex){
            return false;
        }
    }

    private void finishAnimation(){
        Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intentMain);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
    private void nextDay(){
        ArrayList <PlanToSub> pl=MainActivity.myDBManager.getFromDB();
        for(PlanToSub p : pl) {
            p.nextDay();
            MainActivity.myDBManager.updatePlan(p);
            MainActivity.myDBManager.updateQuestionsToSubject(p);
            MainActivity.myDBManager.updateSubTodayLearned(p);
            Users.getUser().currentUpdateDbTime();
        }
    }

    private  void updateDBTime(){
        ApiInterface apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        Call<ArrayList<Subjects>> update = apiInterface.update(MainActivity.getAllSubjects());
        update.enqueue(new Callback<ArrayList<Subjects>>() {
            @Override
            public void onResponse(Call<ArrayList<Subjects>> call, Response<ArrayList<Subjects>> response) {
                if(response.body() != null) {
                    MainActivity.myDBManager.deleteAllSub();
                    for (PlanToSub pl: MainActivity.getAllPlanToSub(response.body())) {
                        MainActivity.myDBManager.setFromDB(pl);
                    }
                }
                nextDay();
                finishAnimation();
            }
            @Override
            public void onFailure(Call<ArrayList<Subjects>> call, Throwable t) {
                nextDay();
                finishAnimation();
            }
        });
    }
}

