package com.example.studentapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.studentapp.databinding.ActivityRegistrationBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Users;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);

        binding.authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Users rUser = new Users(null,binding.loginAuth.getText().toString(),
                        binding.emailAuth.getText().toString(),
                        binding.passwordAuth.getText().toString(),
                        binding.password2Auth.getText().toString());

                Call<String> authUser = apiInterface.regUsers(rUser);
                authUser.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.body()!= null){
                            switch (response.body()) {
                                case "login exists":
                                    Toast.makeText(RegistrationActivity.this,
                                            "Имя пользователя занято!", Toast.LENGTH_SHORT).show();
                                    break;
                                case "email exists":
                                    Toast.makeText(RegistrationActivity.this,
                                            "Такой email уже существует", Toast.LENGTH_SHORT).show();
                                    break;
                                case "password doesn't match":
                                    Toast.makeText(RegistrationActivity.this,
                                            "Пароль не совпадает!!!", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                                    startActivity(intent);
                                    break;
                            }
                        }else{
                            Toast.makeText(RegistrationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(RegistrationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }
}