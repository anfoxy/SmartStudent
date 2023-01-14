package com.example.studentapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.R;
import com.example.studentapp.adapters.FriendsAdapter;
import com.example.studentapp.adapters.FriendsInAdapter;
import com.example.studentapp.adapters.FriendsIsAdapter;
import com.example.studentapp.adapters.SubjectAdapter;
import com.example.studentapp.adapters.SubjectPlanAdapter;
import com.example.studentapp.databinding.FragmentAccountBinding;
import com.example.studentapp.databinding.FragmentFriendsBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Friends;
import com.example.studentapp.db.Plan;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;

import java.time.LocalDate;
import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendsFragment extends Fragment {

    FragmentFriendsBinding binding;
    ApiInterface apiInterface;
    Users user;
    FriendsAdapter.OnItemClickListener itemClickListener;
    FriendsInAdapter.OnItemClickListenerIn itemClickListenerIn;
    FriendsIsAdapter.OnItemClickListenerIs itemClickListenerIs;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = Users.getUser();

        if(isNetworkWorking()) {
            binding.inter.setVisibility(View.GONE);
            setFriends();
        }
        itemClickListener =new FriendsAdapter.OnItemClickListener() {
            @Override
            public void onClickFriends(Users friends, int position) {
                FriendsFragmentDirections.ActionFriendsFragmentToFriendsProfileFragment action = FriendsFragmentDirections.actionFriendsFragmentToFriendsProfileFragment(friends.getId());
                Navigation.findNavController(getView()).navigate(action);
            }
        };
        itemClickListenerIn =new FriendsInAdapter.OnItemClickListenerIn() {

            @Override
            public void onClickFriendsCheck(ArrayList<Users> friends, int position) {
                Users subj = friends.get(position);
                friends.remove(position);
                Users myUser = Users.getUser();
                Call<Friends> planCall = apiInterface.friendsAccept(new Friends(null,"",subj,myUser));
                planCall.enqueue(new Callback<Friends>() {
                    @Override
                    public void onResponse(Call<Friends> call, Response<Friends> response) {
                        if(response.isSuccessful()) {
                            binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
                            binding.listVop.setHasFixedSize(true);
                            binding.listVop.setAdapter(new FriendsAdapter(getContext(), friends, itemClickListener));
                        }
                    }
                    @Override
                    public void onFailure(Call<Friends> call, Throwable t) {
                        if(getContext()!=null)
                            Toast.makeText(getContext(), "Сервер не отвечает", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onClickFriendsClear(ArrayList<Users> friends, int position) {
                Users subj = friends.get(position);
                Users myUser = Users.getUser();
                friends.remove(position);
                Call<Friends> planCall = apiInterface.friendsRefuse(new Friends(null,"",subj,myUser));
                planCall.enqueue(new Callback<Friends>() {
                    @Override
                    public void onResponse(Call<Friends> call, Response<Friends> response) {
                        if(response.isSuccessful()) {
                            binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
                            binding.listVop.setHasFixedSize(true);
                            binding.listVop.setAdapter(new FriendsAdapter(getContext(), friends, itemClickListener));
                        }
                    }
                    @Override
                    public void onFailure(Call<Friends> call, Throwable t) {
                        if(getContext()!=null)
                            Toast.makeText(getContext(), "Сервер не отвечает", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        itemClickListenerIs = new FriendsIsAdapter.OnItemClickListenerIs() {

            @Override
            public void onClickFriendsCheck(ArrayList<Users> friends, int position) {
                Users subj = friends.get(position);
                Users myUser = Users.getUser();
                friends.remove(position);
                Call<Friends> planCall = apiInterface.friendsDeleteIs(new Friends(0,"",subj,myUser));
                planCall.enqueue(new Callback<Friends>() {
                    @Override
                    public void onResponse(Call<Friends> call, Response<Friends> response) {
                        if(response.isSuccessful()) {
                            binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
                            binding.listVop.setHasFixedSize(true);
                            binding.listVop.setAdapter(new FriendsAdapter(getContext(), friends, itemClickListener));
                        }
                    }
                    @Override
                    public void onFailure(Call<Friends> call, Throwable t) {
                        if(getContext()!=null)
                            Toast.makeText(getContext(), "Сервер не отвечает", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

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
                                case "exists":
                                    Toast.makeText(getActivity(), "Запрос уже отправлен", Toast.LENGTH_SHORT).show();
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
        binding.underline.setBackgroundColor(Color.BLUE);
        binding.underline2.setBackgroundColor(Color.GRAY);
        binding.underline3.setBackgroundColor(Color.GRAY);
        binding.friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.underline.setBackgroundColor(Color.BLUE);
                binding.underline2.setBackgroundColor(Color.GRAY);
                binding.underline3.setBackgroundColor(Color.GRAY);
                setFriends();
            }
        });
        binding.isFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.underline.setBackgroundColor(Color.GRAY);
                binding.underline2.setBackgroundColor(Color.GRAY);
                binding.underline3.setBackgroundColor(Color.BLUE);
                setIsFriends();
            }
        });
        binding.inFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.underline.setBackgroundColor(Color.GRAY);
                binding.underline2.setBackgroundColor(Color.BLUE);
                binding.underline3.setBackgroundColor(Color.GRAY);
                setInFriends();
            }
        });

    }
    private boolean isNetworkWorking(){
        try {
            ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (manager != null){
                networkInfo = manager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();
        }catch (NullPointerException ex){
            return false;
        }
    }

    private void setFriends(){

        Call<ArrayList<Users>> getSubs = apiInterface.friendsByUser(user.getId());
        getSubs.enqueue(new Callback<ArrayList<Users>>() {
            @Override
            public void onResponse(Call<ArrayList<Users>> call, Response<ArrayList<Users>> response) {

                if(response.isSuccessful()) {
                    ArrayList<Users> friends = response.body();
                    binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.listVop.setHasFixedSize(true);
                    binding.listVop.setAdapter(new FriendsAdapter(getContext(), friends, itemClickListener));
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Users>> call, Throwable t) {
                if(getContext()!=null)
                    Toast.makeText(getContext(), "Сервер не отвечает", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setInFriends(){

        Call<ArrayList<Users>> getSubs = apiInterface.friendsIn(user.getId());
        getSubs.enqueue(new Callback<ArrayList<Users>>() {
            @Override
            public void onResponse(Call<ArrayList<Users>> call, Response<ArrayList<Users>> response) {
                if(response.isSuccessful()) {
                    ArrayList<Users> friends = response.body();

                    binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.listVop.setHasFixedSize(true);
                    binding.listVop.setAdapter(new FriendsInAdapter(getContext(), friends, itemClickListenerIn));

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Users>> call, Throwable t) {
                if(getContext()!=null)
                    Toast.makeText(getContext(), "Сервер не отвечает", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setIsFriends(){

        Call<ArrayList<Users>> getSubs = apiInterface.friendsIs(user.getId());
        getSubs.enqueue(new Callback<ArrayList<Users>>() {
            @Override
            public void onResponse(Call<ArrayList<Users>> call, Response<ArrayList<Users>> response) {
                if(response.isSuccessful()) {
                    ArrayList<Users> friends = response.body();

                    binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.listVop.setHasFixedSize(true);
                    binding.listVop.setAdapter(new FriendsIsAdapter(getContext(), friends, itemClickListenerIs));
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Users>> call, Throwable t) {
                if(getContext()!=null)
                    Toast.makeText(getContext(), "Сервер не отвечает", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_friends, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        return binding.getRoot();
    }
}