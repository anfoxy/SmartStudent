package com.example.studentapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.adapters.SubjectAdapter;
import com.example.studentapp.adapters.SubjectPlanAdapter;
import com.example.studentapp.al.PlanToDay;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.databinding.FragmentCalendarBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CalendarFragment extends Fragment {

    FragmentCalendarBinding binding;
    ApiInterface apiInterface;
    LocalDate localDate;
    SubjectPlanAdapter.OnItemClick itemClick;
    int flag = 0;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(isNetworkWorking()){
            updateDB();
        }
        localDate = LocalDate.now();

        //Добавление точек в календарь
        {
            ArrayList<PlanToSub> subjs =  MainActivity.myDBManager.getFromDB();

            List<Event> events = new ArrayList<>();

            for (int i = 0; i < subjs.size();i++){
                //прошедшие даты
                for(int j=0; j<subjs.get(i).getLastPlan().size(); j++){
                    subjs.get(i).getPlans().get(j).getDate();
                    events.add(new Event(Color.GRAY, subjs.get(i).getLastPlan().get(j).getDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), ""));
                }
                //будущие даты
                for(int j=0; j<subjs.get(i).getFuturePlan().size(); j++){
                    subjs.get(i).getPlans().get(j).getDate();
                    events.add(new Event(Color.GREEN, subjs.get(i).getFuturePlan().get(j).getDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), ""));
                }
                //экзамен
                events.add(new Event(Color.RED, subjs.get(i).getDateOfExams().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), ""));

            }
            binding.compactcalendarview.addEvents(events);
        }

        itemClick = new SubjectPlanAdapter.OnItemClick() {
            @Override
            public void onClickPlanItem(PlanToSub subject, int position) {
                CalendarFragmentDirections.ActionCalendarFragmentToAnswerQuestionFragment action = CalendarFragmentDirections.actionCalendarFragmentToAnswerQuestionFragment(subject.getSub().getNameOfSubme());
                Navigation.findNavController(getView()).navigate(action);

            }
        };
        getSubjs();


        binding.compactcalendarview.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                myCalendar.setTime(dateClicked);
                localDate =  myCalendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                getSubjs();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }
        });

    }

    private void updateDB(){
        ApiInterface apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        Call<ArrayList<Subjects>> update = apiInterface.update(MainActivity.getAllSubjects());
        update.enqueue(new Callback<ArrayList<Subjects>>() {
            @Override
            public void onResponse(Call<ArrayList<Subjects>> call, Response<ArrayList<Subjects>> response) {
                //Toast.makeText(getContext(), "Есть подключение к серверу", Toast.LENGTH_SHORT).show();
                if(response.body() != null) {
                    MainActivity.myDBManager.deleteAllSub();
                    for (PlanToSub pl: MainActivity.getAllPlanToSub(response.body())) {
                        MainActivity.myDBManager.setFromDB(pl);
                        getSubjs();
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Subjects>> call, Throwable t) {
            }
        });
    }

    private boolean isNetworkWorking(){
        try {
            ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (manager != null){
                networkInfo = manager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();
        }catch (NullPointerException ex){
            return false;
        }
    }

    private void getSubjs(){
        ArrayList<PlanToSub> subjs =  MainActivity.myDBManager.getFromDB();

        ArrayList<PlanToSub> subToDayCalendar = new ArrayList<>();

        for (int i = 0; i < subjs.size();i++){

          if (subjs.get(i).checkPlanToDay(localDate) != null
                  || subjs.get(i).getDateOfExams().isEqual(localDate)) {
              subToDayCalendar.add(subjs.get(i));
          }

        }
        binding.listSub.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listSub.setHasFixedSize(true);
        binding.listSub.setAdapter(new SubjectPlanAdapter(getContext(), subToDayCalendar, localDate, itemClick));
        if(subToDayCalendar.isEmpty()) {
            binding.textPlanNull.setVisibility(View.VISIBLE);
            binding.predmNullPlan.setVisibility(View.VISIBLE);
        } else {
            binding.textPlanNull.setVisibility(View.INVISIBLE);
            binding.predmNullPlan.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(MainActivity.myDBManager.tr_From_DB_Calendar()==0) {
            AlertDialog.Builder builder
                    = new AlertDialog.Builder(getContext());

            final View customLayout
                    = getLayoutInflater()
                    .inflate(
                            R.layout.dialog_info,
                            null);
            builder.setView(customLayout);

            AlertDialog dialog
                    = builder.create();
            Button out = customLayout.findViewById(R.id.okay);
            TextView text = customLayout.findViewById(R.id.text_info);

            text.setText("Добро пожаловать в Smart Student! Тут ты сможешь смотреть план и проходить обучение. Все просто- под календарем расположены блоки, которые нужно выполнить за день. Нажатием на них ты перейдешь в режим обучения.");

            out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Переход на второй слайд обучения
                    if (flag == 0) {
                        flag = 1;
                        text.setText("Для того, чтобы начать обучение - перейди в 'Список предметов' и создай свой первый предмет");
                        // иначе для этого экрана диалоговое окно больше не откроется
                    } else dialog.dismiss();
                }
            });
            dialog.setView(customLayout);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();
            MainActivity.myDBManager.update_TRAINING(1, 1);
        }


        binding = DataBindingUtil.inflate(inflater , R.layout.fragment_calendar, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        return binding.getRoot();
    }
}