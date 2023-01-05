package com.example.studentapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.R;
import com.example.studentapp.adapters.FriendsAdapter;
import com.example.studentapp.adapters.FriendsInAdapter;
import com.example.studentapp.adapters.FriendsInSubjectsAdapter;
import com.example.studentapp.adapters.FriendsIsAdapter;
import com.example.studentapp.adapters.FriendsIsSubjectsAdapter;
import com.example.studentapp.adapters.SubjectAddRecycler;
import com.example.studentapp.databinding.FragmentFriendsBinding;
import com.example.studentapp.databinding.FragmentProfileFriendsBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Friends;
import com.example.studentapp.db.FriendsSubjects;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;

import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendsProfileFragment extends Fragment {

    FragmentProfileFriendsBinding binding;
    ApiInterface apiInterface;
    Users user;
    FriendsProfileFragmentArgs args;
    Users friends;
    FriendsInSubjectsAdapter.OnItemClickListenerIn itemClickListenerIn;
    FriendsIsSubjectsAdapter.OnItemClickListenerIs itemClickListenerIs;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = Users.getUser();
        getFriends();

        itemClickListenerIn =new FriendsInSubjectsAdapter.OnItemClickListenerIn() {
            @Override
            public void onClickFriendsCheck(ArrayList<Subjects> subjectsArrayList, int position) {
                Subjects subj = subjectsArrayList.get(position);
                subjectsArrayList.remove(position);

                Call<FriendsSubjects> planCall = apiInterface.friendsSubjectsAccept(new FriendsSubjects(null,null,friends,user,subj));
                planCall.enqueue(new Callback<FriendsSubjects>() {
                    @Override
                    public void onResponse(Call<FriendsSubjects> call, Response<FriendsSubjects> response) {
                        binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.listVop.setHasFixedSize(true);
                        binding.listVop.setAdapter(new FriendsInSubjectsAdapter(getContext(), subjectsArrayList, itemClickListenerIn));
                        // тут сохраняем также в локальной !!!


                    }
                    @Override
                    public void onFailure(Call<FriendsSubjects> call, Throwable t) {
                        Log.d("not ok", t.getMessage());
                    }
                });
            }

            @Override
            public void onClickFriendsClear(ArrayList<Subjects> subjectsArrayList, int position) {
                Subjects subj = subjectsArrayList.get(position);
                subjectsArrayList.remove(position);

                Call<FriendsSubjects> planCall = apiInterface.friendsSubjectsRefuse(new FriendsSubjects(null,null,friends,user,subj));
                planCall.enqueue(new Callback<FriendsSubjects>() {
                    @Override
                    public void onResponse(Call<FriendsSubjects> call, Response<FriendsSubjects> response) {
                        binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.listVop.setHasFixedSize(true);
                        binding.listVop.setAdapter(new FriendsInSubjectsAdapter(getContext(), subjectsArrayList, itemClickListenerIn));
                    }
                    @Override
                    public void onFailure(Call<FriendsSubjects> call, Throwable t) {
                        Log.d("not ok", t.getMessage());
                    }
                });

            }
        };
        itemClickListenerIs = new FriendsIsSubjectsAdapter.OnItemClickListenerIs() {

            @Override
            public void onClickFriendsCheck(ArrayList<Subjects> subjectsArrayList, int position) {
                Subjects subj = subjectsArrayList.get(position);

                subjectsArrayList.remove(position);
                Call<FriendsSubjects> planCall = apiInterface.friendsSubjectsDeleteIs(new FriendsSubjects(null,null,friends,user,subj));
                planCall.enqueue(new Callback<FriendsSubjects>() {
                    @Override
                    public void onResponse(Call<FriendsSubjects> call, Response<FriendsSubjects> response) {

                        binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.listVop.setHasFixedSize(true);
                        binding.listVop.setAdapter(new FriendsIsSubjectsAdapter(getContext(),subjectsArrayList, itemClickListenerIs));

                    }
                    @Override
                    public void onFailure(Call<FriendsSubjects> call, Throwable t) {
                        Log.d("not ok", t.getMessage());
                    }
                });
            }
        };


        binding.getSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(getView()).navigate(FriendsProfileFragmentDirections.actionFriendsProfileFragmentToAddSubjectsFriendFragment(args.getId()));
            }
        });



        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<String> friendsDelete = apiInterface.friendsDelete(new Friends(null,null,friends,user));
                friendsDelete.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.body() != null){
                            if(response.body().equals("ok")) {
                                Toast.makeText(getContext(), "Вы удалили из друзей " + friends.getLogin(), Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(getView()).navigate(FriendsProfileFragmentDirections.actionFriendsProfileFragmentToFriendsFragment());
                            } else
                                Toast.makeText(getContext(), "Произошла ошибка" + friends.getLogin(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getContext(), "Произошла ошибка на сервере", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        binding.inSub1.setBackgroundColor(Color.BLUE);
        binding.isSub1.setBackgroundColor(Color.GRAY);
        binding.isSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.isSub1.setBackgroundColor(Color.BLUE);
                binding.inSub1.setBackgroundColor(Color.GRAY);
                setIsSub();
            }
        });
        binding.inSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.inSub1.setBackgroundColor(Color.BLUE);
                binding.isSub1.setBackgroundColor(Color.GRAY);
                setInSub();
            }
        });

    }


    private void getFriends(){
        Call<Users> getUser = apiInterface.getUsers(args.getId());
        getUser.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if(response.body() != null){
                    friends = response.body();
                    binding.name.setText(response.body().getLogin());
                    setInSub();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(getContext(), "Произошла ошибка на сервере", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setInSub(){

        Call<ArrayList<Subjects>> getSubs = apiInterface.friendsSubjectsIn(new FriendsSubjects(null,null,friends,user,null));
        getSubs.enqueue(new Callback<ArrayList<Subjects>>() {
            @Override
            public void onResponse(Call<ArrayList<Subjects>> call, Response<ArrayList<Subjects>> response) {

                ArrayList<Subjects> friends = response.body();

                binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.listVop.setHasFixedSize(true);
                binding.listVop.setAdapter(new FriendsInSubjectsAdapter(getContext(), friends, itemClickListenerIn));
            }

            @Override
            public void onFailure(Call<ArrayList<Subjects>> call, Throwable t) {
                System.out.println("не работает2!!!");
            }
        });

    }

    private void setIsSub(){

        Call<ArrayList<Subjects>> getSubs = apiInterface.friendsSubjectsIs(new FriendsSubjects(null,null,friends,user,null));
        getSubs.enqueue(new Callback<ArrayList<Subjects>>() {
            @Override
            public void onResponse(Call<ArrayList<Subjects>> call, Response<ArrayList<Subjects>> response) {

                ArrayList<Subjects> friends = response.body();

                binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.listVop.setHasFixedSize(true);
                binding.listVop.setAdapter(new FriendsIsSubjectsAdapter(getContext(),friends, itemClickListenerIs));


            }

            @Override
            public void onFailure(Call<ArrayList<Subjects>> call, Throwable t) {
                System.out.println("не работает1!!!");
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile_friends, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = FriendsProfileFragmentArgs.fromBundle(getArguments());
        return binding.getRoot();
    }
}