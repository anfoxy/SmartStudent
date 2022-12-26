package com.example.studentapp.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.R;
import com.example.studentapp.adapters.SubjectPlanAdapter;
import com.example.studentapp.databinding.FragmentCalendarBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;

import java.time.LocalDate;
import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CalendarFragment extends Fragment {

    FragmentCalendarBinding binding;
    ApiInterface apiInterface;
    String dateStr;

    SubjectPlanAdapter.OnItemClick itemClick;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getSubjs();

        dateStr = LocalDate.now().toString();

        itemClick = new SubjectPlanAdapter.OnItemClick() {
            @Override
            public void onClickPlanItem(Subjects subject, int position) {
                CalendarFragmentDirections.ActionCalendarFragmentToAnswerQuestionFragment action = CalendarFragmentDirections.actionCalendarFragmentToAnswerQuestionFragment(subject.getId());
                Navigation.findNavController(getView()).navigate(action);

            }
        };

        binding.chooseDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                if (i1 >=10){
                    if (i2>=10){
                        dateStr = i+"-"+(i1+1)+"-"+i2;
                    }else {
                        dateStr = i+"-"+(i1+1)+"-0"+i2;
                    }
                }else {
                    if (i2>=10){
                        dateStr = i+"-0"+(i1+1)+"-"+i2;
                    }else {
                        dateStr = i+"-0"+(i1+1)+"-0"+i2;
                    }
                }

                getSubjs();
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

    private void getSubjs(){
        Users user = Users.getUser();
        Call<ArrayList<Subjects>> getSubs = apiInterface.getSubjectsByUser(user.getId());
        getSubs.enqueue(new Callback<ArrayList<Subjects>>() {
            @Override
            public void onResponse(Call<ArrayList<Subjects>> call, Response<ArrayList<Subjects>> response) {
                if (response.body()!= null){
                    ArrayList<Subjects> subjs = response.body();
                    for (int i = 0; i < subjs.size();i++){
                        LocalDate date = LocalDate.parse(subjs.get(i).getDaysString().split("T")[0]);
                        LocalDate datePicked = LocalDate.parse(dateStr);
                        if (LocalDate.now().compareTo(datePicked) * datePicked.compareTo(date) < 0) {
                            subjs.remove(subjs.get(i));
                            i--;
                        }
                    }
                    binding.listSub.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.listSub.setHasFixedSize(true);
                    binding.listSub.setAdapter(new SubjectPlanAdapter(getContext(), subjs, itemClick));
                    if(subjs.size() == 0) {
                        binding.textPlanNull.setVisibility(View.VISIBLE);
                    } else {
                        binding.textPlanNull.setVisibility(View.INVISIBLE);
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

        binding = DataBindingUtil.inflate(inflater , R.layout.fragment_calendar, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        return binding.getRoot();
    }
}