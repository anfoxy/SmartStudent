package com.example.studentapp.fragments;

import android.app.AlertDialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.adapters.FriendsAdapter;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.al.Subject;
import com.example.studentapp.databinding.FragmentAccountBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Users;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountFragment extends Fragment {

    FragmentAccountBinding binding;
    ApiInterface apiInterface;
    Users user;
    ArrayList<PlanToSub> subjs;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = Users.getUser();
        subjs = MainActivity.myDBManager.getFromDB();
        binding.section1.setTextColor(getResources().getColor(R.color.selected_text_color));
        binding.underline.setBackgroundColor(Color.BLUE);

        binding.name.setText(user.getLogin());
        binding.email.setText(user.getEmail());
        binding.countExam.setText(String.valueOf(subjs.size()));
        LocalDate localDate = subjs.stream()
                .filter(Objects::nonNull)
                .map(PlanToSub::getDateOfExams)
                .min(LocalDate::compareTo)
                .orElse(null);
        if (localDate == null) binding.nextExam.setText("-");
        else binding.nextExam.setText(localDate.toString());

        binding.layProfile.setVisibility(View.VISIBLE);
        binding.section1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.section1.setTextColor(getResources().getColor(R.color.selected_text_color));

                binding.section2.setTextColor(getResources().getColor(R.color.normal_text_color));
                binding.underline.setBackgroundColor(Color.BLUE);
                binding.underline3.setBackgroundColor(Color.GRAY);
                //binding.editpng.setVisibility(View.VISIBLE);
                binding.password.setVisibility(View.VISIBLE);
                binding.layProfile.setVisibility(View.VISIBLE);
                binding.cardDostiz.setVisibility(View.GONE);
            }
        });
        binding.section2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.section2.setTextColor(getResources().getColor(R.color.selected_text_color));
                binding.section1.setTextColor(getResources().getColor(R.color.normal_text_color));
                binding.underline.setBackgroundColor(Color.GRAY);
                binding.underline3.setBackgroundColor(Color.BLUE);
                // binding.editpng.setVisibility(View.INVISIBLE);
                binding.password.setVisibility(View.GONE);
                binding.layProfile.setVisibility(View.GONE);
                // тут условия достижений
                // ...

                // если 5 выученных вопросов и более то:
                if (subjs.stream()
                        .map(PlanToSub::getSub)
                        .map(Subject::getSizeKnow)
                        .max(Integer::compareTo).orElse(0) > 4) {
                    binding.profQueNo.setVisibility(View.GONE);
                    binding.profQueYes.setVisibility(View.VISIBLE);
                }

                // если от 1 и более друзей, то:
                Call<ArrayList<Users>> getSubs = apiInterface.friendsByUser(user.getId());
                getSubs.enqueue(new Callback<ArrayList<Users>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Users>> call, Response<ArrayList<Users>> response) {

                        if (response.body() != null && !response.body().isEmpty()) {
                            binding.profFriendNo.setVisibility(View.GONE);
                            binding.profFriendYes.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Users>> call, Throwable t) {

                    }
                });


                // если один и более выученных предметов то:
                if (subjs.stream().anyMatch(item -> item.getSub().getSizeKnow() == item.getSub().getSizeNoKnow())) {
                    binding.profSubNo.setVisibility(View.GONE);
                    binding.profSubYes.setVisibility(View.VISIBLE);
                }
//

                // если один и более прошло за всё время экзаменов(наступил день экзамена) то:
                if (subjs.stream().anyMatch(item -> item.getDateOfExams().isBefore(LocalDate.now()))) {
                    binding.profExNo.setVisibility(View.GONE);
                    binding.profExYes.setVisibility(View.VISIBLE);
                }
                binding.cardDostiz.setVisibility(View.VISIBLE);

            }
        });

        binding.password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(getContext());

                // set the custom layout
                final View customLayout
                        = getLayoutInflater()
                        .inflate(
                                R.layout.dialog_edit_password,
                                null);
                builder.setView(customLayout);

                AlertDialog dialog
                        = builder.create();


                Button edit = customLayout.findViewById(R.id.out_acc);
                AppCompatButton clsBtn = customLayout.findViewById(R.id.cancel_window);

                EditText oldPas = customLayout.findViewById(R.id.password_old);
                EditText newPas = customLayout.findViewById(R.id.password_new);
                EditText newPas2 = customLayout.findViewById(R.id.password_new2);

                clsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!oldPas.getText().toString().trim().isEmpty() ||
                                !newPas.getText().toString().trim().isEmpty() ||
                                !newPas2.getText().toString().trim().isEmpty()) {
                            if (!oldPas.getText().toString().equals(user.getPassword()))
                                Toast.makeText(getActivity(), "Неправильный пароль!", Toast.LENGTH_SHORT).show();
                            else
                                if (!newPas.getText().toString().equals(newPas2.getText().toString()))
                                Toast.makeText(getActivity(), "Пароль не совпадает!", Toast.LENGTH_SHORT).show();
                                 else {
                                    Users aUser = new Users(user.getId(), user.getLogin(), user.getEmail(), newPas.getText().toString());
                                    Call<Users> authUser = apiInterface.editUsers(user.getId(), aUser);
                                    authUser.enqueue(new Callback<Users>() {
                                        @Override
                                        public void onResponse(Call<Users> call, Response<Users> response) {
                                            if (response.body() != null) {
                                                Users.writeUser(response.body());

                                                Toast.makeText(getActivity(), "Пароль успешно сохранен!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<Users> call, Throwable t) {
                                            Toast.makeText(getActivity(), "Ошибка на сервере!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    dialog.dismiss();
                                }
                        } else  Toast.makeText(getActivity(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
                    }
                });

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

    }


    private void updateInfo(int sectionId) {
        // Здесь можете реализовать логику обновления информации ниже по странице
        // в зависимости от выбранного раздела. Например, можете использовать свитч-кейс или
        // использовать фрагменты.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_account, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        //args = AccountFragmentArgs.fromBundle(getArguments());
        return binding.getRoot();
    }
}