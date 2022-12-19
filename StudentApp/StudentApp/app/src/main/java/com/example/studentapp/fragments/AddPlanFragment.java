package com.example.studentapp.fragments;

import static java.time.temporal.ChronoUnit.DAYS;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.R;
import com.example.studentapp.adapters.SubjectAddRecycler;
import com.example.studentapp.databinding.FragmentAddPlanBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Plan;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddPlanFragment extends Fragment {

    ApiInterface apiInterface;
    FragmentAddPlanBinding binding;
    final Calendar myCalendar = Calendar.getInstance();
    int idSub;
    SubjectAddRecycler.OnItemClickListener itemClick;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Paper.book("questions").destroy();
        Paper.book("plan").destroy();

        itemClick = new SubjectAddRecycler.OnItemClickListener() {

            @Override
            public void onClickQuestion(Questions ques, int position) {
                Toast.makeText(getActivity(), "Что-то нажали", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(getContext());

                // set the custom layout
                final View customLayout
                        = getLayoutInflater()
                        .inflate(
                                R.layout.dialog_add_question,
                                null);
                builder.setView(customLayout);



                AlertDialog dialog
                        = builder.create();

                EditText tvAnswer = customLayout.findViewById(R.id.tv_answer);
                EditText tvQ = customLayout.findViewById(R.id.tv_question);
                Button addBtn = customLayout.findViewById(R.id.add_question);
                AppCompatButton clsBtn = customLayout.findViewById(R.id.cancel_window);
                tvAnswer.setText(ques.getAnswer());
                tvQ.setText(ques.getQuestion());

                addBtn.setText("Сохранить");
                clsBtn.setText("Удалить");

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Questions.updateQuestion(position, tvQ.getText().toString(),tvAnswer.getText().toString());
                        setQuestions(Questions.getQuestions());
                        dialog.dismiss();
                    }
                });

                clsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Questions.deleteQuestion(position);
                        setQuestions(Questions.getQuestions());
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        };
        setQuestions(Questions.getQuestions());

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };

        binding.editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        binding.AddPlan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Questions.getQuestions().isEmpty()){
                    Toast.makeText(getContext(), "Добавьте вопросы", Toast.LENGTH_SHORT).show();
                }else{
                    Subjects sub = new Subjects(0, binding.Text1.getText().toString(), binding.editTextDate.getText().toString(), false, Users.getUser(), new ArrayList<Questions>(),new ArrayList<Plan>());
                    Call<Subjects> addSub = apiInterface.addSubject(sub);

                    addSub.enqueue(new Callback<Subjects>() {
                        @Override
                        public void onResponse(Call<Subjects> call, Response<Subjects> response) {

                            if (response.body()!= null){
                                Subjects newSubject = response.body();
                                idSub=newSubject.getId();
                                ArrayList<Plan> plans = setNewPlan(newSubject);
                                ArrayList<Questions> questions = Questions.getQuestions();
                                for (int i = 0; i < questions.size(); i++){
                                    Questions ques = questions.get(i);
                                    ques.setId(null);
                                    ques.setSubId(newSubject);
                                    Log.d("Вопрос ", ques.toString());
                                    Call<Questions> questionsCall = apiInterface.addQuestion(ques);
                                    questionsCall.enqueue(new Callback<Questions>() {
                                        @Override
                                        public void onResponse(Call<Questions> call, Response<Questions> response) {
                                            if (response.body()!= null){
                                                Log.d("ok", response.message());
                                            }else{
                                                Log.d("not ok", response.message());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Questions> call, Throwable t) {
                                            Log.d("not ok", t.getMessage());
                                        }
                                    });

                                }
                                for (int i = 0; i < plans.size(); i++){
                                    Plan plan = plans.get(i);
                                    plan.setId(null);
                                    Call<Plan> planCall = apiInterface.addPlan(plan);
                                    planCall.enqueue(new Callback<Plan>() {
                                        @Override
                                        public void onResponse(Call<Plan> call, Response<Plan> response) {
                                            if (response.body()!= null){
                                                Log.d("ok", response.message());
                                            }else{
                                                Log.d("not ok", response.message());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Plan> call, Throwable t) {
                                            Log.d("not ok", t.getMessage());
                                        }
                                    });

                                }

                            }else {
                                Toast.makeText(getActivity(), "Не получилось", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Subjects> call, Throwable t) {
                            Log.d("not ok", t.getMessage());
                        }
                    });
                    Log.d("Новый предмет =  ", ""+ idSub);
                    NavDirections action = AddPlanFragmentDirections.actionAddPlanFragmentToSettingPlanFragment2(idSub);
                    Navigation.findNavController(getView()).navigate(action);
                }
            }
        });

        binding.addque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(view);
            }
        });
    }

    private ArrayList<Plan> setNewPlan(Subjects subj){
        LocalDate date = LocalDate.parse(subj.getDaysString().split("T")[0]);
        long days = DAYS.between(LocalDate.now(), date);

        Calendar cal = new GregorianCalendar();
        ArrayList<Plan> p = new ArrayList<Plan>();

        for(int i=0; i<days; i++){
            Plan plan = new Plan(0,
                    "" + cal.get(Calendar.YEAR)+
                            "-" + cal.get(Calendar.MONTH)+
                            "-" + cal.get(Calendar.DATE),
                    Questions.getQuestions().size(),subj);
            p.add(plan);
            Plan.addPlan(plan);
            cal.add(Calendar.DATE, 1);
        }

        return p;
    }

    private void updateLabel(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
        binding.editTextDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void setQuestions(ArrayList<Questions> questions){
        binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listVop.setHasFixedSize(true);
        binding.listVop.setAdapter(new SubjectAddRecycler(getContext(), questions, itemClick));
    }

    public void showAlertDialogButtonClicked(View view) {

        // Create an alert builder
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());

        // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.dialog_add_question,
                        null);
        builder.setView(customLayout);



        AlertDialog dialog
                = builder.create();

        EditText tvAnswer = customLayout.findViewById(R.id.tv_answer);
        EditText tvQ = customLayout.findViewById(R.id.tv_question);
        Button addBtn = customLayout.findViewById(R.id.add_question);
        AppCompatButton clsBtn = customLayout.findViewById(R.id.cancel_window);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvAnswer.getText().toString() == "" || tvQ.getText().toString() == ""){
                    Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                }else {
                    Questions question = new Questions(0, tvQ.getText().toString(), tvAnswer.getText().toString(), false, null);
                    Questions.addQuestion(question);
                    setQuestions(Questions.getQuestions());
                    dialog.dismiss();
                }
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_plan, container, false);
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        Paper.init(getContext());
        return binding.getRoot();
    }
}