package com.example.studentapp.fragments.game;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.studentapp.AuthActivity;
import com.example.studentapp.LaunchActivity;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QuestionGameFragment extends Fragment {

    ApiInterface apiInterface;
    FragmentQuestionGameBinding binding;
    QuestionGameFragmentArgs args;
    GameSubjects gameSubjects;
    boolean timerStart = false;
    boolean timerStop = false;
    int second;
    Thread t;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getQuestion();
        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if (gameSubjects != null) {

                    System.out.println("gameSubjects");
                    gameSubjects.setAnswerHost(binding.tvAnswer.getText().toString());
                    Call<GameSubjects> getUser = apiInterface.gameSetQuestion(Users.getUser().getId(), gameSubjects);
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
        });

        binding.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameSubjects != null) {
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(getContext());

                    final View customLayout
                            = getLayoutInflater()
                            .inflate(
                                    R.layout.dialog_out,
                                    null);
                    builder.setView(customLayout);

                    AlertDialog dialog
                            = builder.create();

                    TextView text = customLayout.findViewById(R.id.text_out);
                    text.setText("Вы действительно хотите выйти \n из игры?");
                    Button out = customLayout.findViewById(R.id.out_acc);
                    AppCompatButton clsBtn = customLayout.findViewById(R.id.cancel_window);

                    out.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Call<Integer> getUser = apiInterface.exitingTheGame(Users.getUser().getId(), gameSubjects.getGameId());
                            getUser.enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    if (response.body() != null) {
                                        Navigation.
                                                findNavController(getView()).
                                                navigate(QuestionGameFragmentDirections
                                                        .actionQuestionGameFragmentToFriendsProfileFragment(response.body()));
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {
                                }
                            });

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
        });
    }
    private void getQuestion() {

        binding.tvAnswer.setText("");
        Call<GameSubjects> getUser = apiInterface.gameGetQuestion(args.getId(),Users.getUser());
        getUser.enqueue(new Callback<GameSubjects>() {
            @Override
            public void onResponse(Call<GameSubjects> call, Response<GameSubjects> response) {
                if (response.body() != null) {
                    if (response.body().getId().equals(-1)) {
                        Navigation.
                                findNavController(getView()).
                                navigate(QuestionGameFragmentDirections
                                        .actionQuestionGameFragmentToLoadingGameFragment(args.getId()));
                    } else {
                        gameSubjects = response.body();
                        if (!timerStart) timer();
                        binding.tvQuestion.setText(gameSubjects.getQuestion());
                    }
                }
            }
            @Override
            public void onFailure(Call<GameSubjects> call, Throwable t) {

            }
        });
    }

    private void timer() {
        timerStart = true;

        if (gameSubjects != null) {
            second = Integer.parseInt(gameSubjects.getGameId().getDate());
            Integer id = args.getId();

            Runnable helloRunnable = new Runnable() {
                public void run() {

                    while (!timerStop && second > 0) {
                        binding.time.setText(setTime());
                        second--;
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!timerStop) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Navigation.findNavController(getView()).
                                        navigate(QuestionGameFragmentDirections
                                                .actionQuestionGameFragmentToLoadingGameFragment(id));
                            }
                        });
                    }
                }
            };
            t = new Thread(helloRunnable);
            t.start();
        }
    }

    void timerStop(){
        timerStop = true;
    }

    private String setTime() {
        int hours = second / 3600;
        int minutes = (second % 3600) / 60;
        int seconds = second % 60;
        if (hours > 0)
            return checkDateFor0(hours) + ":" + checkDateFor0(minutes) + ":" + checkDateFor0(seconds);
        return checkDateFor0(minutes) + ":" + checkDateFor0(seconds);
    }
    private String checkDateFor0(int figure) {
        return figure < 10 ? "0" + figure : "" + figure;
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
    @Override
    public void onPause() {
        super.onPause();
        timerStop();
    }
}