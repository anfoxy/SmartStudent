package com.example.studentapp.fragments.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.databinding.FragmentQuestionGameBinding;
import com.example.studentapp.databinding.FragmentSettingGameBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Friends;
import com.example.studentapp.db.Game;
import com.example.studentapp.db.GameSubjects;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;

import java.time.LocalDate;
import java.util.stream.Collectors;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QuestionGameFragment extends Fragment {

    ApiInterface apiInterface;
    FragmentQuestionGameBinding binding;
    QuestionGameFragmentArgs args;
    Game game;
    GameSubjects gameSubjects;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        game = new Game(args.getId(),args.getStatus());
        getQuestion();
        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gameSubjects != null) {

                    if (args.getStatus().equals("HOST")) {
                        gameSubjects.setAnswerHost(binding.tvAnswer.getText().toString());
                        Call<GameSubjects> getUser = apiInterface.gameSetQuestionHost(gameSubjects);
                        getUser.enqueue(new Callback<GameSubjects>() {
                            @Override
                            public void onResponse(Call<GameSubjects> call, Response<GameSubjects> response) {
                                if (response.body() != null) {
                                    getQuestion();
                                }
                            }

                            @Override
                            public void onFailure(Call<GameSubjects> call, Throwable t) {
                            }
                        });
                    } else {
                        gameSubjects.setAnswerFriend(binding.tvAnswer.getText().toString());
                        Call<GameSubjects> getUser = apiInterface.gameSetQuestionFriend(gameSubjects);
                        getUser.enqueue(new Callback<GameSubjects>() {
                            @Override
                            public void onResponse(Call<GameSubjects> call, Response<GameSubjects> response) {
                                if (response.body() != null) {
                                    getQuestion();
                                }
                            }

                            @Override
                            public void onFailure(Call<GameSubjects> call, Throwable t) {
                            }
                        });
                    }
                }

            }
        });
    }

    private void getQuestion(){

        binding.tvAnswer.setText("");
        Call<GameSubjects> getUser = apiInterface.gameGetQuestion(game);
        getUser.enqueue(new Callback<GameSubjects>() {
            @Override
            public void onResponse(Call<GameSubjects> call, Response<GameSubjects> response) {
                if (response.body() != null) {


                    if (response.body().getId().equals(-1)) {
                        try {
                            Thread.sleep(3000);
                            Toast.makeText(getContext(), "Подсчет результатов...", Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Navigation.
                                findNavController(getView()).
                                navigate(QuestionGameFragmentDirections
                                        .actionQuestionGameFragmentToCompareGameFragment(response.body().getId(), "HOST"));
                    } else {
                        gameSubjects = response.body();
                        binding.tvQuestion.setText(gameSubjects.getQuestion());
                    }
                }
            }

            @Override
            public void onFailure(Call<GameSubjects> call, Throwable t) {
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_game, container, false);
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = QuestionGameFragmentArgs.fromBundle(getArguments());
        Paper.init(getContext());
        return binding.getRoot();
    }

}