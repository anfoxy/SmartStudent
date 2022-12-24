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

import com.example.studentapp.R;
import com.example.studentapp.databinding.FragmentAccountBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Users;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountFragment extends Fragment {

    FragmentAccountBinding binding;
    ApiInterface apiInterface;
    Users user;
  //  AccountFragmentArgs args;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = Users.getUser();
        binding.section1.setTextColor(getResources().getColor(R.color.selected_text_color));
        binding.underline.setBackgroundColor(Color.BLUE);
        binding.name.setText(user.getLogin());
        binding.layProfile.setVisibility(View.VISIBLE);
        binding.section1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.section1.setTextColor(getResources().getColor(R.color.selected_text_color));

                binding.section2.setTextColor(getResources().getColor(R.color.normal_text_color));
                binding.underline.setBackgroundColor(Color.BLUE);
                binding.underline3.setBackgroundColor(Color.GRAY);
                binding.editpng.setVisibility(View.VISIBLE);
                binding.password.setVisibility(View.VISIBLE);
                binding.layProfile.setVisibility(View.VISIBLE);
            }
        });
        binding.section2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.section2.setTextColor(getResources().getColor(R.color.selected_text_color));
                binding.section1.setTextColor(getResources().getColor(R.color.normal_text_color));
                binding.underline.setBackgroundColor(Color.GRAY);
                binding.underline3.setBackgroundColor(Color.BLUE);
                binding.editpng.setVisibility(View.INVISIBLE);
                binding.password.setVisibility(View.INVISIBLE);
                binding.layProfile.setVisibility(View.INVISIBLE);

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