package com.example.studentapp.fragments;

import static java.time.temporal.ChronoUnit.DAYS;

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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.R;
import com.example.studentapp.adapters.PlanAddRecycler;
import com.example.studentapp.databinding.FragmentEditItemPlanBinding;
import com.example.studentapp.databinding.FragmentSettingPlanBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Plan;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditItemPlanFragment extends Fragment {

    private PlanAddRecycler adapter;
    private FragmentEditItemPlanBinding binding;
    private ApiInterface apiInterface;
    private EditItemPlanFragmentArgs args;
    private Subjects subj;
    private int ind = 1;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Запускаю поиск1  =  ", "");
        setPlan();
        binding.AddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean p [] = adapter.getPlanArray();
                ArrayList<Plan> plans = new ArrayList<>();

                for (int i=0;i<subj.getPlans().size();i++)
                    if (p[i]) plans.add(subj.getPlans().get(i));

                Log.d("Запускаю поиск gkfyf  =  ", ""+plans);
                Call<ArrayList<Plan>> updatePlan = apiInterface.addPlans(plans);
                updatePlan.enqueue(new Callback<ArrayList<Plan>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Plan>> call, Response<ArrayList<Plan>> response) {
                    }
                    @Override
                    public void onFailure(Call<ArrayList<Plan>> call, Throwable t) {
                    }
                });
                NavDirections action = EditItemPlanFragmentDirections.actionEditItemPlanFragmentToEditPlanFragment(args.getId());
                Navigation.findNavController(getView()).navigate(action);

            }
        });

        binding.all1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("План предмета =  ", ""+ subj);
                ind = ind == 0 ? 1 : 0;
                editPlan(subj.getPlans(),ind);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_edit_item_plan, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = EditItemPlanFragmentArgs.fromBundle(getArguments());
        return binding.getRoot();
    }

    private void setPlan(){

        Call<Subjects> getSubj = apiInterface.getSubjectById(args.getId());
        getSubj.enqueue(new Callback<Subjects>() {
            @Override
            public void onResponse(Call<Subjects> call, Response<Subjects> response) {
                if (response.body()!= null){
                    subj = new Subjects(response.body());
                    ArrayList<Plan> p = removeDatesAfterToday();

                    binding.listPlan.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.listPlan.setHasFixedSize(true);
                    adapter = new PlanAddRecycler(getActivity(),subj.getPlans(),1, setNewPlan(p,subj));
                    binding.listPlan.setAdapter(adapter);
                }else{
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Subjects> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private  void editPlan(ArrayList<Plan> p , int check){
        binding.listPlan.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listPlan.setHasFixedSize(true);
        adapter = new PlanAddRecycler(getActivity(),p,check);
        binding.listPlan.setAdapter(adapter);

    }


    private boolean[] setNewPlan(ArrayList<Plan> dates, Subjects subj){
        LocalDate date = LocalDate.parse(subj.getDaysString().split("T")[0]);
        long days = DAYS.between(LocalDate.now(), date);

        Calendar cal = new GregorianCalendar();

        boolean [] p = new boolean[Math.toIntExact(days)];
        for(int i=0; i<days; i++){
            String time = "" + cal.get(Calendar.YEAR)+
                    "-" + cal.get(Calendar.MONTH)+
                    "-" + cal.get(Calendar.DATE);
            if(!checkDate(time)){
                Plan plan = new Plan(null, time, 0,subj);
                dates.add(plan);
                p[i] = false;
            } else  p[i] = true;
            cal.add(Calendar.DATE, 1);
        }

        subj.setPlans(dates);
        return p;
    }

    private boolean checkDate(String str){

        for (Plan plan : subj.getPlans()) {
            if(plan.getDate().equals(str))
                return true;
        }
        return false;
    }

    private ArrayList<Plan> removeDatesAfterToday() {
        ArrayList<Plan> dates = new ArrayList<>(subj.getPlans());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int todayYear = calendar.get(Calendar.YEAR);
        int todayMonth = calendar.get(Calendar.MONTH) + 1;
        int todayDay = calendar.get(Calendar.DAY_OF_MONTH);

        dates.removeIf(date -> {
            String[] parts = date.getDate().split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) + 1;
            int day = Integer.parseInt(parts[2]);

            if (year > todayYear) return true;
            if (year == todayYear && month > todayMonth) return true;
            return  (year == todayYear && month == todayMonth && day > todayDay);

        });
        return dates;
    }

}