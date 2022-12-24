package com.example.studentapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.studentapp.R;
import com.example.studentapp.databinding.FragmentAccountBinding;
import com.example.studentapp.databinding.FragmentFriendsBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Friends;
import com.example.studentapp.db.Plan;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Users;

import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendsFragment extends Fragment {

    FragmentFriendsBinding binding;
    ApiInterface apiInterface;
    Users user;
  //  AccountFragmentArgs args;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = Users.getUser();


        binding.sendFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.nameFriend.getText().toString().trim().isEmpty()){
                    Toast.makeText(getActivity(), "Введите имя пользователя!", Toast.LENGTH_SHORT).show();
                } else {
                    Friends friends = new Friends(null,"REQUEST_SENT",
                            new Users(null,binding.nameFriend.getText().toString()),
                            Users.getUser());
                    Call<String> planCall = apiInterface.friendsAdd(friends);
                    planCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.body()!= null)
                            switch (response.body()) {
                                case "OK":
                                    Toast.makeText(getActivity(), "Запрос отправлен.", Toast.LENGTH_SHORT).show();
                                    break;
                                case "your username":
                                    Toast.makeText(getActivity(), "Нельзя добавить себя в друзья.", Toast.LENGTH_SHORT).show();
                                    break;
                                case "Not login":
                                    Toast.makeText(getActivity(), "Пользователь с таким именем не найден.", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("not ok", t.getMessage());
                        }
                    });
                }
            }
        });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_friends, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        //args = AccountFragmentArgs.fromBundle(getArguments());
        return binding.getRoot();
    }
}