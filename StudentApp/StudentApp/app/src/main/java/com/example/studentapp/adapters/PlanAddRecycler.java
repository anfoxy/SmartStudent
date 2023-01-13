package com.example.studentapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentapp.R;
import com.example.studentapp.al.PlanToDay;
import com.example.studentapp.db.Plan;
import com.example.studentapp.db.Questions;

import java.util.ArrayList;

public class PlanAddRecycler extends RecyclerView.Adapter<PlanAddRecycler.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<PlanToDay> planToDay;
    private int ind;
    private boolean p [];

    public boolean[] getPlanArray(){
        return p;
    }

    public PlanAddRecycler(Context context, ArrayList<PlanToDay> states , int ind) {
        this.planToDay = states;
        this.ind = ind;
        p = new boolean[states.size()];
        this.inflater = LayoutInflater.from(context);
    }
    public PlanAddRecycler(Context context, ArrayList<PlanToDay> states , int ind,boolean pl []) {
        this.planToDay = states;
        this.ind = ind;
        this.p= pl;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public PlanAddRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanAddRecycler.ViewHolder holder, int position) {
        PlanToDay state3 = planToDay.get(position);
        holder.nameView.setText(state3.dateToString());
        switch (ind) {
            case 1:
                holder.box.setChecked(true);
                p[position] = true;
                break;
            case 0:
                holder.box.setChecked(false);
                p[position] = false;
                break;
            default:
                holder.box.setChecked(p[position]);
                break;
        }
        holder.box.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                ind = 2;
                p[position] = holder.box.isChecked();

            }
        });
    }

    @Override
    public int getItemCount() {
        return planToDay.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        final CheckBox box;
        ViewHolder(View view){
            super(view);
            box=(CheckBox) view.findViewById(R.id.checkBox);
            nameView = (TextView) view.findViewById(R.id.opril3o);
        }
    }
}
