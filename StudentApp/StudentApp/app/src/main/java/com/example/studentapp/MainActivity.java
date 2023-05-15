package com.example.studentapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static final String CHANNEL_ID = "ExamPreparationChannel";

    private NotificationManager notificationManager;

    public static MyDBManager myDBManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.bottomNavMenu, navController);


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        updateNotifications();
    }

    public void updateNotifications() {
        // Удаление всех существующих уведомлений
        SharedPreferences sharedPreferences = getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        notificationManager.cancelAll();

        // Получение всех предметов из базы данных
        // ArrayList<Subjects> subjects = getAllSubjects();
        ArrayList<PlanToSub> planToSubs = MainActivity.myDBManager.getFromDB();

        // Перебор всех предметов
        for (PlanToSub subject : planToSubs) {
            for (PlanToDay plan : subject.getFuturePlan()) {

                LocalDate planDate = plan.getDate();
                // Проверка, что уведомление на эту дату еще не создано
                boolean notificationExists = checkNotificationExists(planDate);
                if (!notificationExists) {
                    // Создание уведомления
                    createNotification(plan);
                }
            }
        }
    }
    private  boolean checkNotificationExists(LocalDate date) {
        // Получение экземпляра SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Notifications", Context.MODE_PRIVATE);

        // Получение значения для указанной даты из SharedPreferences
        boolean notificationExists = sharedPreferences.getBoolean(date.toString(), false);

        return notificationExists;
    }
    private  void createNotification(PlanToDay plan) {
        // Создание уведомления
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, plan.getDate().getDayOfMonth());
        calendar.set(Calendar.MONTH, plan.getDate().getMonthValue() - 1);  // Месяцы в Calendar начинаются с 0
        calendar.set(Calendar.YEAR, plan.getDate().getYear());

        if (calendar.getTimeInMillis() >= System.currentTimeMillis()) {
            System.out.println(plan.dateToString());
            Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            intent.setAction("" + getNotificationId(plan.getDate()));
            intent.putExtra("notificationId", getNotificationId(plan.getDate()));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }
        }
        //------------------------------------------------------------------
        // Сохранение информации о созданном уведомлении в SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(plan.getDate().toString(), true);
        editor.apply();

    }

    private int getNotificationId(LocalDate date) {
        int a = Integer.parseInt(String.valueOf(date.getDayOfMonth())+String.valueOf(date.getMonthValue()));
        System.out.println(a);
        return a;
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
                    Users.getUser().currentUpdateDbTime();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Subjects>> call, Throwable t) {
            }
        });
    }

    public static ArrayList<Subjects> getAllSubjects() {

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
    public static ArrayList<PlanToSub> getAllPlanToSub(ArrayList<Subjects> pl) {
        System.out.println(pl.toString());
        ArrayList<PlanToSub> res = new ArrayList<>();
        for(Subjects p : pl){
            res.add(p.getPlanToSub());
        }
        return  res;
    }
}