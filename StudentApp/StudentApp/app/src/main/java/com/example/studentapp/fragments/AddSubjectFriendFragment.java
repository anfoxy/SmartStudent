package com.example.studentapp.fragments;


import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.adapters.FriendSubjectsRecycler;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.FriendsSubjects;
import com.example.studentapp.db.Plan;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import java.util.Calendar;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.studentapp.adapters.PlanAddRecycler;
import com.example.studentapp.databinding.FragmentAddSubjectFriendBinding;
import com.example.studentapp.db.Users;
import java.util.ArrayList;



public class AddSubjectFriendFragment extends Fragment {

    private FriendSubjectsRecycler adapter;
    private FragmentAddSubjectFriendBinding binding;
    private ApiInterface apiInterface;
    private AddSubjectFriendFragmentArgs args;
    private ArrayList<Subjects> subj;
    private Users users;
    private Users friends;
    private int ind = 1;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        users = Users.getUser();
        setPlan();
        binding.AddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean p [] = adapter.getPlanArray();
                ArrayList<FriendsSubjects> subjects = new ArrayList<>();

                for (int i=0;i<subj.size();i++)
                    if (p[i]) subjects.add(new FriendsSubjects(null,null,friends,users,subj.get(i)));

                Call<String> sentFriendsSubjects = apiInterface.sentFriendsSubjects(subjects);
                sentFriendsSubjects.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {


                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });
                NavDirections action = AddSubjectFriendFragmentDirections.actionAddSubjectFriendFragmentToFriendsProfileFragment(args.getId());
                Navigation.findNavController(getView()).navigate(action);
            }
        });

        binding.all1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ind = ind == 0 ? 1 : 0;
                editPlan(subj,ind);
            }
        });

    }



    private void setPlan(){

        Call<Users> getUser = apiInterface.getUsers(args.getId());
        getUser.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if(response.body() != null){
                    friends = response.body();
                    Call<ArrayList<Subjects>> getSubj = apiInterface.getAllFriendsSubjectsNotTabl(args.getId(),users);
                    getSubj.enqueue(new Callback<ArrayList<Subjects>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Subjects>> call, Response<ArrayList<Subjects>> response) {
                            if (response.body()!= null){
                                subj = response.body();
                                binding.listPlan.setLayoutManager(new LinearLayoutManager(getContext()));
                                binding.listPlan.setHasFixedSize(true);
                                adapter = new FriendSubjectsRecycler(getActivity(),subj,1);
                                binding.listPlan.setAdapter(adapter);
                            }else{
                                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ArrayList<Subjects>> call, Throwable t) {
                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });



    }


    private  void editPlan(ArrayList<Subjects> p , int check){
        binding.listPlan.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listPlan.setHasFixedSize(true);
        adapter = new FriendSubjectsRecycler(getActivity(),p,check);
        binding.listPlan.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(MainActivity.myDBManager.tr_From_DB_Give_Sub()==0) {
            AlertDialog.Builder builder
                    = new AlertDialog.Builder(getContext());

            final View customLayout
                    = getLayoutInflater()
                    .inflate(
                            R.layout.dialog_info,
                            null);
            builder.setView(customLayout);

            AlertDialog dialog
                    = builder.create();
            Button out = customLayout.findViewById(R.id.okay);
            TextView text = customLayout.findViewById(R.id.text_info);

            text.setText("Выбери один или несколько предметов, которыми хочешь поделиться с другом. Как только он подтвердит получение, у него будет полная копия предмета.");

            out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.setView(customLayout);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            MainActivity.myDBManager.update_TRAINING(1, 8);
        }
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_subject_friend, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = AddSubjectFriendFragmentArgs.fromBundle(getArguments());
        return binding.getRoot();
    }
}