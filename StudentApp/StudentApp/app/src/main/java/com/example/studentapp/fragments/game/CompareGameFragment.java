package com.example.studentapp.fragments.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.studentapp.R;
import com.example.studentapp.databinding.FragmentCompareGameBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Game;
import com.example.studentapp.db.GameSubjects;
import com.example.studentapp.db.ServiceBuilder;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CompareGameFragment extends Fragment {

    ApiInterface apiInterface;
    FragmentCompareGameBinding binding;
    CompareGameFragmentArgs args;
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
                setResult(2);
            }
        });
        binding.answerMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(1);
            }
        });
        binding.answerFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(0);
            }
        });


    }
    private void setResult( int res) {
        if (gameSubjects != null) {
            if (args.getStatus().equals("HOST")) {
                gameSubjects.setResultHost(res);
                Call<GameSubjects> getUser = apiInterface.gameSetResultHost(gameSubjects);
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
                gameSubjects.setResultFriend(res);
                Call<GameSubjects> getUser = apiInterface.gameSetResultFriend(gameSubjects);
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




    private void getQuestion(){


        Call<GameSubjects> getUser = apiInterface.gameGetResult(game);
        getUser.enqueue(new Callback<GameSubjects>() {
            @Override
            public void onResponse(Call<GameSubjects> call, Response<GameSubjects> response) {
                if (response.body() != null) {


                    if (response.body().getId().equals(-1)) {
                        // меняем статус и ждем друга
                        Navigation.
                                findNavController(getView()).
                                navigate(CompareGameFragmentDirections
                                        .actionCompareGameFragmentToLoadingGameFragment(args.getId(), args.getStatus()));
                    } else {
                        gameSubjects = response.body();
                        binding.question.setText(gameSubjects.getQuestion());
                        binding.answer.setText(gameSubjects.getAnswer());
                        binding.answerMe.setText(gameSubjects.getAnswerHost());
                        binding.answerFriend.setText(gameSubjects.getAnswerFriend());
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compare_game, container, false);
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = CompareGameFragmentArgs.fromBundle(getArguments());
        Paper.init(getContext());
        return binding.getRoot();
    }

}