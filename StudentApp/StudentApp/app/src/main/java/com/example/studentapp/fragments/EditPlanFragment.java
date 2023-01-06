package com.example.studentapp.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
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

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.adapters.SubjectAddRecycler;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.al.Question;
import com.example.studentapp.databinding.FragmentEditPlanBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.stream.Collectors;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditPlanFragment extends Fragment {

    FragmentEditPlanBinding binding;
    ApiInterface apiInterface;
    EditPlanFragmentArgs args;
    SubjectAddRecycler.OnItemClickListener itemClick;
    final Calendar myCalendar = Calendar.getInstance();
    PlanToSub subject;
    LocalDate localDate;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        subject = MainActivity.myDBManager.getFromDB().stream()
                .filter( c -> c.getSub().getNameOfSubme().equals(args.getId())).collect(Collectors.toList()).get(0);
        localDate = subject.getDateOfExams();

        if(localDate.isBefore(LocalDate.now())) binding.editPlan.setVisibility(View.INVISIBLE);

        itemClick = new SubjectAddRecycler.OnItemClickListener() {
            @Override
            public void onClickQuestion(Question ques, int position) {
                showItemDialog(view, ques,position);
            }
        };

        setQuestions();

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

        binding.b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(view);
            }
        });

        binding.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.myDBManager.delete_SUB(subject.getSub().getNameOfSubme());
                Users.getUser().currentUpdateDbTime();
                NavDirections action = EditPlanFragmentDirections.actionEditPlanFragmentToListFragment();
                Navigation.findNavController(getView()).navigate(action);

            }
        });

        binding.editPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  save();
                NavDirections action = EditPlanFragmentDirections.actionEditPlanFragmentToEditItemPlanFragment2(args.getId());
                Navigation.findNavController(getView()).navigate(action);
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             save();
             Users.getUser().currentUpdateDbTime();
             NavDirections action = EditPlanFragmentDirections.actionEditPlanFragmentToListFragment();
             Navigation.findNavController(getView()).navigate(action);
            }
        });
    }

    private void save(){

        subject.setDateOfExams(localDate);
        MainActivity.myDBManager.updateNameSubAndDateExams(subject,binding.Text1.getText().toString());
        subject.getSub().setNameOfSub(binding.Text1.getText().toString());
        MainActivity.myDBManager.updateQuestionsToSubject(subject);
        MainActivity.myDBManager.updatePlan(subject);

    }
    private void updateLabel(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
        localDate =  myCalendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        binding.editTextDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void setQuestions(){
        binding.Text1.setText(subject.getSub().getNameOfSubme());
        binding.editTextDate.setText(subject.dateToString().split("T")[0]);
        binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listVop.setHasFixedSize(true);
        binding.listVop.setAdapter(new SubjectAddRecycler(getContext(), subject.getSub().getQuestion(), itemClick));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_plan, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = EditPlanFragmentArgs.fromBundle(getArguments());
        return binding.getRoot();
    }

    private void showItemDialog(View view, Question questions, int pos) {
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

        tvAnswer.setText(questions.getAnswer());
        tvQ.setText(questions.getQuestion());

        addBtn.setText("Сохранить");
        clsBtn.setText("Удалить");

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvAnswer.getText().toString() == "" || tvQ.getText().toString() == ""){
                    Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                }else {

                    subject.changeQuestion(pos,tvQ.getText().toString(),tvAnswer.getText().toString());
                    //subject.getSub().getQuestion().get(pos).setAnswer(tvAnswer.getText().toString());
                    //subject.getSub().getQuestion().get(pos).setQuestion(tvQ.getText().toString());
                    setQuestions();
                    dialog.dismiss();


                }
            }
        });

        clsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subject.delQuestion(pos);
              //  subject.getSub().getQuestion().remove(pos);
                setQuestions();
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    public void showAlertDialogButtonClicked(View view)
    {

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
                    subject.setNewQuestion(new Question(tvQ.getText().toString(),tvAnswer.getText().toString()));
                /*    subject.getSub().addQuestion(
                            new Question(tvQ.getText().toString(),tvAnswer.getText().toString()));*/
                    setQuestions();
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

}