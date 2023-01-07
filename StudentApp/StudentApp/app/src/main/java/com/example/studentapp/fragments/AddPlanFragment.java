package com.example.studentapp.fragments;

import static java.time.temporal.ChronoUnit.DAYS;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.adapters.QuestionAddRecycler;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.al.Question;
import com.example.studentapp.databinding.FragmentAddPlanBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Users;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

import io.paperdb.Paper;


public class AddPlanFragment extends Fragment {

    ApiInterface apiInterface;
    FragmentAddPlanBinding binding;
    final Calendar myCalendar = Calendar.getInstance();
    int idSub;
    QuestionAddRecycler.OnItemClickListener itemClick;
    PlanToSub planToSub;
    LocalDate localDate;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        planToSub = new PlanToSub();
 /*       Paper.book("questions").destroy();
        Paper.book("plan").destroy();*/

        itemClick = new QuestionAddRecycler.OnItemClickListener() {

            @Override
            public void onClickQuestion(Question ques, int position) {
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

                        planToSub.getSub().getQuestion().get(position).setQuestion(tvQ.getText().toString());
                        planToSub.getSub().getQuestion().get(position).setAnswer(tvAnswer.getText().toString());

                        setQuestions(planToSub.getSub().getQuestion());
                        dialog.dismiss();
                    }
                });

                clsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        planToSub.getSub().getQuestion().remove(position);
                        setQuestions(planToSub.getSub().getQuestion());
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        };
        setQuestions(planToSub.getSub().getQuestion());

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
                Calendar minDate = Calendar.getInstance();
                minDate.set(Calendar.YEAR, myCalendar.get(Calendar.YEAR));
                minDate.set(Calendar.MONTH, myCalendar.get(Calendar.MONTH));
                minDate.set(Calendar.DAY_OF_MONTH, myCalendar.get(Calendar.DAY_OF_MONTH));
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        binding.AddPlan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (planToSub.getSub().getQuestion().isEmpty()){
                    Toast.makeText(getContext(), "Добавьте вопросы", Toast.LENGTH_SHORT).show();
                }if(localDate.equals(LocalDate.now())){
                    Toast.makeText(getContext(), "Добавьте дату экзамена", Toast.LENGTH_SHORT).show();
                }if(binding.Text1.getText().toString().trim().isEmpty()){
                    Toast.makeText(getContext(), "Добавьте имя предмета", Toast.LENGTH_SHORT).show();
                }else{
                    int id = MainActivity.myDBManager.getFromDB().stream().mapToInt(PlanToSub::getId).min().orElse(0)-1;
                    planToSub.setId(id);
                    planToSub.setDateOfExams(localDate);
                    planToSub.getSub().setNameOfSub(binding.Text1.getText().toString());
                    setNewPlan();
                    MainActivity.myDBManager.setFromDB(planToSub);
                    Users.getUser().currentUpdateDbTime();

                    NavDirections action = AddPlanFragmentDirections.actionAddPlanFragmentToSettingPlanFragment2(planToSub.getSub().getNameOfSubme());
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

    private void setNewPlan(){
        LocalDate date = LocalDate.parse(planToSub.dateToString().split("T")[0]);
        long days = DAYS.between(LocalDate.now(), date);
        for(int i=0; i<days; i++){
            planToSub.plusDayToPlan(LocalDate.now().plusDays(i));
        }
    }

    private void updateLabel(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
        localDate =  myCalendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        binding.editTextDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void setQuestions(ArrayList<Question> questions){
        binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listVop.setHasFixedSize(true);
        binding.listVop.setAdapter(new QuestionAddRecycler(getContext(), questions, itemClick));
    }

    public void showAlertDialogButtonClicked(View view) {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_question, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();

        EditText tvAnswer = customLayout.findViewById(R.id.tv_answer);
        EditText tvQ = customLayout.findViewById(R.id.tv_question);
        Button addBtn = customLayout.findViewById(R.id.add_question);
        ImageButton addBtnCam = customLayout.findViewById(R.id.camera_btn);
        AppCompatButton clsBtn = customLayout.findViewById(R.id.cancel_window);

        addBtnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ДИАНА. При нажатии на кнопку, вызываем эту функцию с Tesseract
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvAnswer.getText().toString() == "" || tvQ.getText().toString() == ""){
                    Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                }else {

                    planToSub.getSub().addQuestion(
                            new Question(tvQ.getText().toString(),tvAnswer.getText().toString()));
                    setQuestions(planToSub.getSub().getQuestion());
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