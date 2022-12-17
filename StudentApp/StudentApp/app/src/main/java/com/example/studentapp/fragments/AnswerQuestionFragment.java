package com.example.studentapp.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.fragment.app.Fragment;
import com.example.studentapp.R;
import com.example.studentapp.databinding.FragmentAnswerQuestionBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;

import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AnswerQuestionFragment extends Fragment {

    FragmentAnswerQuestionBinding binding;
    ApiInterface apiInterface;
    AnswerQuestionFragmentArgs args;
    ArrayList<Questions> questions;
    int number = 1;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData();

        binding.lookAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.textAnswer.setVisibility(View.VISIBLE);
                binding.yesAnswerButton.setVisibility(View.VISIBLE);
                binding.noAnswerButton.setVisibility(View.VISIBLE);
                binding.lookAnswerButton.setVisibility(View.INVISIBLE);
            }
        });
        binding.noAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number++;
                setQuestions();
            }
        });
        binding.yesAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Questions question = questions.get(number-1);
                question.setCompleted(true);
                Call<Questions> questionsCall = apiInterface.updateQuestion(question.getId(), question);
                questionsCall.enqueue(new Callback<Questions>() {
                    @Override
                    public void onResponse(Call<Questions> call, Response<Questions> response) {
                        if (response.isSuccessful()){
                            number++;
                            setQuestions();
                        }
                    }

                    @Override
                    public void onFailure(Call<Questions> call, Throwable t) {
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


    private void setQuestions() {
        if (number < questions.size()+1){
            binding.lookAnswerButton.setVisibility(View.VISIBLE);
            binding.textAnswer.setVisibility(View.INVISIBLE);
            binding.yesAnswerButton.setVisibility(View.INVISIBLE);
            binding.noAnswerButton.setVisibility(View.INVISIBLE);
            binding.textNumber.setText("Вопрос "+number);
            binding.textQuestion.setText(questions.get(number-1).getQuestion());
            binding.textAnswer.setText(questions.get(number-1).getAnswer());
        }else{
            Toast.makeText(getContext(), "Вопросы по данному предмету закончились", Toast.LENGTH_SHORT).show();
            @NonNull NavDirections action = AnswerQuestionFragmentDirections.actionAnswerQuestionFragmentToCalendarFragment();
            Navigation.findNavController(getView()).navigate(action);
        }
    }

    private void setData(){
        Call<Subjects> subjectsCall = apiInterface.getSubjectById(args.getId());
        subjectsCall.enqueue(new Callback<Subjects>() {
            @Override
            public void onResponse(Call<Subjects> call, Response<Subjects> response) {
                    questions = response.body().getQuestions();
                    setQuestions();
            }

            @Override
            public void onFailure(Call<Subjects> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_answer_question, container, false);
        args = AnswerQuestionFragmentArgs.fromBundle(getArguments());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        Paper.init(getContext());
        return binding.getRoot();
    }
}