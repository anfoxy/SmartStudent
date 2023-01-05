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
        /*    for(Plan plan : plans){
                plan.setSubId(s);
            }
            for(Questions questions1 : questions){
                questions1.setSubId(s);
            }*/
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
            /*ArrayList<PlanToDay> f = new ArrayList<>();
            ArrayList<PlanToDay> l = new ArrayList<>();
            ArrayList<Question> question= new ArrayList<>();
            boolean flag = true;
            for (int i = 0 ; i < p.getPlans().size();i++){
                if(p.getPlans().get(i).isBoolDate()) flag = false;

                String[] parts = p.getPlans().get(i).getDate().split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int  day = Integer.parseInt(parts[2]);
                LocalDate date_local1 = LocalDate.of(year, month, day);
                PlanToDay plan = new PlanToDay(date_local1, p.getPlans().get(i).getNumberOfQuestions());
                plan.setId(p.getPlans().get(i).getId());

                if(flag) l.add(plan);
                else f.add(plan);

            }
            for(Questions q : p.getQuestions()){
                String[] parts = q.getDate().split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                LocalDate date_local1 = LocalDate.of(year, month, day);
                question.add(new Question(q.getQuestion(), q.getAnswer(),  date_local1, q.getSizeOfView(), q.getPercentKnow()));

            }
            Subject sub_ = new Subject(p.getName(),question);

            String[] parts = p.getDays().split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int  day = Integer.parseInt(parts[2]);
            LocalDate date_of_exams1 = LocalDate.of(year, month, day);
            PlanToSub planToSub = new PlanToSub(sub_, p.getTodayLearned(), date_of_exams1, f, l);
            planToSub.setId(p.getId());
            res.add(planToSub);*/
            res.add(p.getPlanToSub());
        }
        return  res;
    }
}