package com.example.studentapp.fragments;

import static java.time.temporal.ChronoUnit.DAYS;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.adapters.SubjectsCardsRecycler;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.al.Question;
import com.example.studentapp.al.Study;
import com.example.studentapp.databinding.FragmentStatisticBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

import io.paperdb.Paper;


public class StatisticFragment extends Fragment {

    FragmentStatisticBinding binding;

    ApiInterface apiInterface;
    StatisticFragmentArgs args;
    PlanToSub planToSub;

    CardView cardView;
    TextView textQuestion;
    TextView textAnswer;
    ScrollView scrollText2;

    private Spannable spannable;
    private int selectedCount = 0;
    private int totalCount = 0;
    private int CountAnswerText = 0;
    private int emptyStringCount = 0;
    SubjectsCardsRecycler.OnItemClickListener itemClickListener;
    Question nowQuest;
    Study study;

    // Переменная, хранящая текущее состояние карточки (сторона с текстом1 или с текстом2)
    private boolean isText1Visible = true;
    // Переменная, хранящая текущее состояние анимации (запущена или нет)
    private boolean isAnimationRunning = false;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("id равно  "+args.getId());
        planToSub =  MainActivity.myDBManager.getFromDB().stream()
                .filter( c -> c.getSub().getNameOfSubme().equals(args.getId())).collect(Collectors.toList()).get(0);
        setCard(planToSub.getSub().getQuestion());
        study=new Study(planToSub);
        nowQuest=study.GetNewQuestion();
        binding.nameSub.setText("Название предмета\n"+planToSub.getSub().getNameOfSubme());
        binding.kolvop.setText("Всего вопросов\n"+planToSub.getSub().getSizeAllQuest());
        binding.kolzap.setText("Выученных вопросов\n"+planToSub.getSub().getSizeKnow());
        binding.dateob.setText("Невыученных вопросов\n" +planToSub.getSub().getSizeNoKnow());
//        binding.dateex.setText("Дата экзамена\n"+ planToSub.dateToString().split("T")[0]);
        LocalDate date = LocalDate.parse(planToSub.dateToString().split("T")[0]);
        long days = DAYS.between(LocalDate.now(), date);
//        binding.kold.setText("Дней до экзамена\n"+days);

        binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatisticFragmentDirections.ActionStatisticFragmentToEditPlanFragment action = StatisticFragmentDirections.actionStatisticFragmentToEditPlanFragment(args.getId());
                Navigation.findNavController(getView()).navigate(action);
            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calIntent = new Intent(Intent.ACTION_INSERT);
                calIntent.setType("vnd.android.cursor.item/event");
                calIntent.putExtra(CalendarContract.Events.TITLE, "Подготовка по предмету "+ planToSub.getSub().getNameOfSubme()+".");
                calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "");
                calIntent.putExtra(CalendarContract.Events.DESCRIPTION,
                        "Всего вопросов по данному предмету- "+planToSub.getSub().getSizeAllQuest()+"."+
                                "\nГотовься, твой экзамен будет- "+planToSub.dateToString()+".");
                GregorianCalendar calDateStart = new GregorianCalendar(LocalDate.now().getYear(), LocalDate.now().getMonthValue()-1, LocalDate.now().getDayOfMonth()-1);
                GregorianCalendar calDateEnd;
                Integer size=0;
                if(planToSub.getFuturePlan().size()>0){
                    size=planToSub.getFuturePlan().size()-1;
                    calDateEnd= new GregorianCalendar(planToSub.getFuturePlan().get(size).getDate().getYear(),
                            planToSub.getFuturePlan().get(size).getDate().getMonthValue()-1,
                            planToSub.getFuturePlan().get(size).getDate().getDayOfMonth());
                }
                else if(planToSub.getLastPlan().size()>0){
                    size=planToSub.getLastPlan().size()-1;
                    calDateEnd= new GregorianCalendar(planToSub.getLastPlan().get(size).getDate().getYear(),
                            planToSub.getLastPlan().get(size).getDate().getMonthValue()-1,
                            planToSub.getLastPlan().get(size).getDate().getDayOfMonth());
                }
                else calDateEnd= new GregorianCalendar(planToSub.getDateOfExams().getYear(),
                            planToSub.getDateOfExams().getMonthValue()-1,
                            planToSub.getDateOfExams().getDayOfMonth());
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        calDateStart.getTimeInMillis());
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        calDateEnd.getTimeInMillis());
                startActivity(calIntent);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(MainActivity.myDBManager.tr_From_DB_Statistic()==0) {
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

            text.setText("Здесь ты сможешь узнать необходимую информацию о предмете, изучить все вопросы перед тестированием(прохождением плана подготовки), редактировать предмет или установить события у себя в календаре.");
            out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.setView(customLayout);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            MainActivity.myDBManager.update_TRAINING(1, 10);
        }
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_statistic, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = StatisticFragmentArgs.fromBundle(getArguments());
        return binding.getRoot();
    }

    private void setCard(ArrayList<Question> questions) {
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.listSubAllView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.listSubAllView.setLayoutManager(layoutManager);
        binding.listSubAllView.setAdapter(new SubjectsCardsRecycler(getContext(), questions, itemClickListener));
    }
}