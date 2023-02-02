package com.example.studentapp.fragments.game;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import androidx.navigation.Navigation;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.databinding.FragmentSettingGameBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Friends;
import com.example.studentapp.db.Game;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;
import com.example.studentapp.fragments.FriendsProfileFragmentDirections;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import io.paperdb.Paper;

public class SettingGameFragment extends Fragment implements SeekBar.OnSeekBarChangeListener{

    ApiInterface apiInterface;
    FragmentSettingGameBinding binding;
    SettingGameFragmentArgs args;

    PlanToSub planToSub;
    LocalDate localDate;
    Users friend;
    PlanToSub plan;
    Integer colQue;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        friend = new Users(args.getFriend());

        plan= MainActivity.myDBManager.getFromDB().stream()
                .filter( c -> c.getSub().getNameOfSubme().equals(args.getId())).collect(Collectors.toList()).get(0);

        binding.nameSub.setText(args.getId());
        binding.seekBar.setOnSeekBarChangeListener(this);
        binding.Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              binding.seekBar.getProgress();

             /* ArrayList<Questions> questions = new ArrayList<>();
              for (int i = 0 ; i<binding.seekBar.getProgress(); i++) {
                  questions.add(plan.getSub().getQuestions().get(i));
                  questions.get(i).setId(plan.getId());
              }*/

                Game game = new Game(null,
                        new Subjects(plan.getId()),
                        new Friends(null,"",friend,Users.getUser()),binding.time.getText().toString(),colQue,plan.getSub().getNameOfSubme());

                Call<Game> getUser = apiInterface.gameSet(game);
                getUser.enqueue(new Callback<Game>() {
                    @Override
                    public void onResponse(Call<Game> call, Response<Game> response) {
                        if(response.body() != null){

                            Navigation.
                                    findNavController(getView()).
                                    navigate(SettingGameFragmentDirections
                                            .actionSettingGameFragmentToLoadingGameFragment2(response.body().getId(),"HOST"));
                        }
                    }
                    @Override
                    public void onFailure(Call<Game> call, Throwable t) {
                    }
                });

            }
        });
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBar.setMax(plan.getSub().getQuestion().size());
        seekBar.setMin(1);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        colQue = seekBar.getProgress();
        binding.questionCount.setText("Колличество вопросов: "+String.valueOf(seekBar.getProgress()));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_game, container, false);
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = SettingGameFragmentArgs.fromBundle(getArguments());
        Paper.init(getContext());
        return binding.getRoot();
    }

}