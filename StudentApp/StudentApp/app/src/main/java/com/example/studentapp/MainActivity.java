package com.example.studentapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.studentapp.al.PlanToDay;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.databinding.ActivityMainBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;
import com.example.studentapp.db_local.MyDBManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    public static MyDBManager myDBManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDBManager = new MyDBManager(this);
        myDBManager.openDB();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.calendarFragment, R.id.listFragment, R.id.anotherFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavMenu, navController);


    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp();
    }

    public static void updateDBTime(){
        ApiInterface apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        ArrayList<PlanToSub> pl = myDBManager.getFromDB();
        ArrayList<Subjects> res = new ArrayList<>();
        for(PlanToSub p : pl){
            ArrayList<PlanToDay> plans= new ArrayList<>(p.getLastPlan());
            plans.addAll(p.getFuturePlan());
            res.add(new Subjects(p.getId(),p.getSub().getNameOfSubme(),p.dateToString(), p.getTodayLearned(), Users.getUser(),p.getSub().getQuestions(),plans));

        }

        Call<ArrayList<Subjects>> update = apiInterface.update();
        update.enqueue(new Callback<ArrayList<Subjects>>() {
            @Override
            public void onResponse(Call<ArrayList<Subjects>> call, Response<ArrayList<Subjects>> response) {
                ArrayList<Subjects> user = response.body();

            }

            @Override
            public void onFailure(Call<ArrayList<Subjects>> call, Throwable t) {

            }
        });
    }
}