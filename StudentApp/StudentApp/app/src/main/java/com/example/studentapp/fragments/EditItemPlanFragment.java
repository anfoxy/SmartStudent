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

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.adapters.PlanAddRecycler;
import com.example.studentapp.al.PlanToDay;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.databinding.FragmentEditItemPlanBinding;
import com.example.studentapp.databinding.FragmentSettingPlanBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Plan;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditItemPlanFragment extends Fragment {

    private PlanAddRecycler adapter;
    private FragmentEditItemPlanBinding binding;
    private ApiInterface apiInterface;
    private EditItemPlanFragmentArgs args;
    private PlanToSub subject;
    private int ind = 1;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPlan();
        binding.AddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean p[] = adapter.getPlanArray();
                ArrayList<PlanToDay> plans = new ArrayList<>();


                for (int i = 0; i < subject.getFuturePlan().size(); i++)
                    if (p[i]) plans.add(subject.getFuturePlan().get(i));
                if (plans.isEmpty()) {
                    Toast.makeText(getContext(), "Укажите план подготовки", Toast.LENGTH_SHORT).show();
                } else {
                    subject.setFuture(plans);
                    MainActivity.myDBManager.updatePlan(subject);
                    Users.getUser().currentUpdateDbTime();

                    NavDirections action = EditItemPlanFragmentDirections.actionEditItemPlanFragmentToEditPlanFragment(args.getId());
                    Navigation.findNavController(getView()).navigate(action);
                }
            }
        });

        binding.all1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ind = ind == 0 ? 1 : 0;
                editPlan(subject.getFuturePlan(),ind);
            }
        });

    }


    private void setPlan(){

        subject = MainActivity.myDBManager.getFromDB().stream()
                .filter( c -> c.getSub().getNameOfSubme().equals(args.getId()) ).collect(Collectors.toList()).get(0);
        boolean []b = setNewPlan(subject.getFuturePlan());
        binding.listPlan.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listPlan.setHasFixedSize(true);
        adapter = new PlanAddRecycler(getActivity(),subject.getFuturePlan(),3,b);
        binding.listPlan.setAdapter(adapter);



       /* Call<Subjects> getSubj = apiInterface.getSubjectByIdNotQuestion(args.getId());
        getSubj.enqueue(new Callback<Subjects>() {
            @Override
            public void onResponse(Call<Subjects> call, Response<Subjects> response) {
                if (response.body()!= null){
                    subj = new Subjects(response.body());
                    ArrayList<Plan> p = removeDatesAfterToday();

                    boolean []b = setNewPlan(p);

                    binding.listPlan.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.listPlan.setHasFixedSize(true);
                    adapter = new PlanAddRecycler(getActivity(),subj.getPlans(),3,b);
                    binding.listPlan.setAdapter(adapter);
                }else{
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Subjects> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }
    private  void editPlan(ArrayList<PlanToDay> p , int check){
        binding.listPlan.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listPlan.setHasFixedSize(true);
        adapter = new PlanAddRecycler(getActivity(),p,check);
        binding.listPlan.setAdapter(adapter);

    }


    private boolean[] setNewPlan(ArrayList<PlanToDay> dates){
        LocalDate date = LocalDate.parse(subject.dateToString().split("T")[0]);
        long days = DAYS.between(LocalDate.now(), date);
        boolean [] p = new boolean[Math.toIntExact(days)];


        for(int i=0; i<days; i++){
            p[i] = checkDate(LocalDate.now().plusDays(i));
            subject.plusDayToPlan(LocalDate.now().plusDays(i));
        }



/*        Calendar cal = new GregorianCalendar();

        boolean [] p = new boolean[Math.toIntExact(days)];
        ArrayList<Plan> newPlan = new ArrayList<>();

        for(int i=0; i<days; i++){
            String time = "" + cal.get(Calendar.YEAR)+
                    "-" + cal.get(Calendar.MONTH)+
                    "-" + cal.get(Calendar.DATE);

            p[i] = checkDate(time);
            Plan plan = new Plan(null, time, 0,null);
            newPlan.add(plan);
            cal.add(Calendar.DATE, 1);
        }
        subj.setPlans(newPlan);*/

        return p;
    }

    private boolean checkDate(LocalDate str){
        for (PlanToDay plan : subject.getFuturePlan())
            if(plan.getDate().equals(str)) return true;
        return false;
    }

/*    private ArrayList<Plan> removeDatesAfterToday() {
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
            return  (year == todayYear && month == todayMonth && day <= todayDay);
        });
        return dates;
    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_edit_item_plan, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = EditItemPlanFragmentArgs.fromBundle(getArguments());
        return binding.getRoot();
    }
}