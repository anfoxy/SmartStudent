package com.example.studentapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.example.studentapp.adapters.SubjectAdapter;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.databinding.FragmentListBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;

import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListFragment extends Fragment {

    FragmentListBinding binding;
    ApiInterface apiInterface;
    SubjectAdapter.OnItemClickListener itemClickListener;
    int flag = 0;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(isNetworkWorking()){
            updateDB();
        }
        itemClickListener =new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onClickSubject(PlanToSub subject, int position) {
                ListFragmentDirections.ActionListFragmentToStatisticFragment action = ListFragmentDirections.actionListFragmentToStatisticFragment(subject.getSub().getNameOfSubme());
                Navigation.findNavController(getView()).navigate(action);
            }
        };
        setSub();


        binding.AddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = ListFragmentDirections.actionListFragmentToAddPlanFragment();
                Navigation.findNavController(getView()).navigate(action);
            }
        });

    }
    private void setSub(){
        ArrayList<PlanToSub> subjs = MainActivity.myDBManager.getFromDB();
        binding.listSubView.setHasFixedSize(true);
        binding.listSubView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listSubView.setAdapter(new SubjectAdapter(getContext(), subjs, itemClickListener));
        if(subjs.size() == 0) {
            binding.predmNullPng.setVisibility(View.VISIBLE);
            binding.textList.setVisibility(View.VISIBLE);
        } else {
            binding.textList.setVisibility(View.INVISIBLE);
            binding.predmNullPng.setVisibility(View.INVISIBLE);
        }
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

    private void updateDB(){
        ApiInterface apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        Call<ArrayList<Subjects>> update = apiInterface.update(MainActivity.getAllSubjects());
        update.enqueue(new Callback<ArrayList<Subjects>>() {
            @Override
            public void onResponse(Call<ArrayList<Subjects>> call, Response<ArrayList<Subjects>> response) {
                if(response.body() != null) {
                    MainActivity.myDBManager.deleteAllSub();
                    for (PlanToSub pl: MainActivity.getAllPlanToSub(response.body())) {
                        MainActivity.myDBManager.setFromDB(pl);
                        setSub();
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Subjects>> call, Throwable t) {
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(MainActivity.myDBManager.tr_From_DB_List_Sub()==0) {
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

        text.setText("Тут отображается список твоих предметов. Нажав на предмет, ты сможешь узнать подробную информацию о нем, редактировать его или удалить.");

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Переход на второй слайд обучения
                if(flag == 0) {
                    flag = 1;
                    text.setText("Для создания предмета - нажми на кнопку 'Создать новый предмет'");
                    // иначе для этого экрана диалоговое окно больше не откроется
                } else dialog.dismiss();
            }
        });
        dialog.setView(customLayout);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        MainActivity.myDBManager.update_TRAINING(1, 2);
    }
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_list, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        return binding.getRoot();
    }
}