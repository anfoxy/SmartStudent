package com.example.studentapp;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.studentapp.al.PlanToDay;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.al.Question;
import com.example.studentapp.al.Subject;
import com.example.studentapp.databinding.ActivityMainBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Plan;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;
import com.example.studentapp.db_local.MyConstants;
import com.example.studentapp.db_local.MyDBManager;

import java.time.LocalDate;
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

/*        myDBManager = new MyDBManager(this);
        myDBManager.openDB();*/

        updateDBTime();
        ArrayList <PlanToSub> pl=myDBManager.getFromDB();
        for(PlanToSub p : pl) {
            p.nextDay();
            MainActivity.myDBManager.updatePlan(p);
            MainActivity.myDBManager.updateQuestionsToSubject(p);
            MainActivity.myDBManager.updateSubTodayLearned(p);
            Users.getUser().currentUpdateDbTime();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


   /*     AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.calendarFragment, R.id.listFragment, R.id.anotherFragment)
                .build();*/
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.bottomNavMenu, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp();
    }

    public static void updateDBTime(){
        ApiInterface apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        Call<ArrayList<Subjects>> update = apiInterface.update(getAllSubjects());
        update.enqueue(new Callback<ArrayList<Subjects>>() {
            @Override
            public void onResponse(Call<ArrayList<Subjects>> call, Response<ArrayList<Subjects>> response) {
                if(response.body() != null) {
                    myDBManager.deleteAllSub();
                    for (PlanToSub pl: getAllPlanToSub(response.body())) {
                        MainActivity.myDBManager.setFromDB(pl);
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Subjects>> call, Throwable t) {

            }
        });
    }

    private static ArrayList<Subjects> getAllSubjects() {

        ArrayList<PlanToSub> pl = MainActivity.myDBManager.getFromDB();
        ArrayList<Subjects> res = new ArrayList<>();
        res.add(new Subjects(Users.getUser()));
        for(PlanToSub p : pl){
            ArrayList<Plan> plans= p.getPlans();
            ArrayList<Questions> questions= p.getSub().getQuestions();
            Subjects s = new Subjects(p.getId(),p.getSub().getNameOfSubme(),p.dateToString(), p.getTodayLearned(), Users.getUser());
            s.setPlans(plans);
            s.setQuestions(questions);
            res.add(s);
        }
        return  res;
    }
    private static ArrayList<PlanToSub> getAllPlanToSub(ArrayList<Subjects> pl) {
        System.out.println(pl.toString());
        ArrayList<PlanToSub> res = new ArrayList<>();
        for(Subjects p : pl){
            res.add(p.getPlanToSub());
        }
        return  res;
    }
}