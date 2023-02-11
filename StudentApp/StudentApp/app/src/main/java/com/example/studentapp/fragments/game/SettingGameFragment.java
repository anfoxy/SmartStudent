package com.example.studentapp.fragments.game;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TimePicker;

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

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Locale;
import java.util.stream.Collectors;

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
        String[] hoursValues = new String[]{"00ч", "01ч", "02ч", "03ч", "04ч", "05ч", "06ч", "07ч", "08ч", "09ч"};

        binding.hoursPicker.setMinValue(0);
        binding.hoursPicker.setMaxValue(hoursValues.length - 1);
        binding.hoursPicker.setDisplayedValues(hoursValues);
        binding.hoursPicker.setValue(0);

        String[] minutesValues = new String[]{"00м", "05м", "10м", "15м", "20м", "25м", "30м", "35м", "40м", "45м", "50м", "55м"};

        binding.minutesPicker.setMinValue(0);
        binding.minutesPicker.setMaxValue(minutesValues.length - 1);
        binding.minutesPicker.setDisplayedValues(minutesValues);
        binding.minutesPicker.setValue(0);


        binding.hoursPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        binding.minutesPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        friend = new Users(args.getFriend());
        plan = MainActivity.myDBManager.getFromDB().stream()
                .filter(c -> c.getSub().getNameOfSubme().equals(args.getId())).collect(Collectors.toList()).get(0);
        binding.questionCount.setText("Количество вопросов: " + String.valueOf(1 + " / " + plan.getSub().getQuestion().size()));

        binding.nameSub.setText(args.getId());
        binding.seekBar.setOnSeekBarChangeListener(this);

        binding.Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              binding.seekBar.getProgress();

              int t = binding.hoursPicker.getValue()*60+binding.minutesPicker.getValue()*5;
              String time = "" + t;

                Game game = new Game(null,
                        new Subjects(plan.getId()),
                        new Friends(null,"",friend,Users.getUser()),time,colQue,plan.getSub().getNameOfSubme());

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
        binding.questionCount.setText("Количество вопросов: "+String.valueOf(seekBar.getProgress() + " / " + plan.getSub().getQuestion().size()));
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