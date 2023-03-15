package com.example.studentapp.fragments.game;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.studentapp.db.Users;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CompareGameFragment extends Fragment {

    ApiInterface apiInterface;
    FragmentCompareGameBinding binding;
    CompareGameFragmentArgs args;
    GameSubjects gameSubjects;

    int flag1 = 0;
    int flag2 = 0;
    int res = -1;
    int n_click = 0;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getResult();
        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(res!=-1) {
                    setResult(res);
                    flag1 = 0;
                    flag2 = 0;
                    n_click = 0;
                    res = -1;
                    binding.answerTitleMe.setBackgroundColor(getResources().getColor(R.color.color_back));
                    binding.answerTitleFriend.setBackgroundColor(getResources().getColor(R.color.color_back));
                }
            }
        });
        binding.answerMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag1==0) { // если фон начальный
                    flag1 = 1;
                    n_click++;
                    if (n_click == 1) { // если клик первый
                        res = 1;
                } else { // если клик второй
                        res = 2;
                    }
                    binding.answerTitleMe.setBackgroundColor(getResources().getColor(R.color.green));
                } else if (flag1==1) { // если фон уже установлен
                    flag1 = 0;
                    res=-1;
                    n_click--;
                    binding.answerTitleMe.setBackgroundColor(getResources().getColor(R.color.color_back));
                }
            }
        });

        binding.answerFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag2==0) {
                    flag2 = 1;
                    n_click++;
                    if (n_click == 1) {
                        res = 0;
                    } else {
                        res = 2;
                    }
                    binding.answerTitleFriend.setBackgroundColor(getResources().getColor(R.color.green));
                } else if (flag2==1) {
                    flag2 = 0;
                    res=-1;
                    n_click--;
                    binding.answerTitleFriend.setBackgroundColor(getResources().getColor(R.color.color_back));
                }
            }
        });


    }
    private void setResult( int res) {
        if (gameSubjects != null) {

            gameSubjects.setResultHost(res);
            Call<GameSubjects> getUser = apiInterface.gameSetResult(Users.getUser().getId(), gameSubjects);
            getUser.enqueue(new Callback<GameSubjects>() {
                @Override
                public void onResponse(Call<GameSubjects> call, Response<GameSubjects> response) {
                    if (response.body() != null) {
                        getResult();
                    }
                }

                @Override
                public void onFailure(Call<GameSubjects> call, Throwable t) {
                }
            });

        }
    }


    private void getResult(){

        Call<GameSubjects> getUser = apiInterface.gameGetResult(args.getId(),Users.getUser());
        getUser.enqueue(new Callback<GameSubjects>() {
            @Override
            public void onResponse(Call<GameSubjects> call, Response<GameSubjects> response) {
                if (response.body() != null) {
                    if (response.body().getId().equals(-1)) {
                        Navigation.
                                findNavController(getView()).
                                navigate(CompareGameFragmentDirections
                                        .actionCompareGameFragmentToLoadingGameFragment(args.getId()));
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