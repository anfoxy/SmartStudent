package com.example.studentapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentapp.R;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.Subjects;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class SubjectPlanAdapter extends RecyclerView.Adapter<SubjectPlanAdapter.SubjectViewHolder> {

    public interface OnItemClick{
        void onClickPlanItem(PlanToSub subject, int position);
    }

    private Context context;
    private ArrayList<PlanToSub> subjects;
    private OnItemClick onItemClick;
    private LocalDate date;

    public SubjectPlanAdapter(Context context, ArrayList<PlanToSub> subjects, LocalDate date, OnItemClick onItemClick) {
        this.context = context;
        this.subjects = subjects;
        this.onItemClick = onItemClick;
        this.date = date;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subj_card, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PlanToSub subject = subjects.get(position);
        holder.subjName.setText(subject.getSub().getNameOfSubme());
        int allQuestions = subject.getSub().getSizeAllQuest();
        int comQues = subject.getSub().getSizeKnow();
        if(date.isEqual(subject.getDateOfExams())) {
            holder.subjStat.setText("Экзамен");

        } else if(date.isBefore(LocalDate.now())) {
            holder.subjStat.setText("Выучено: " + comQues);

        } else if(date.isAfter(LocalDate.now())) {
            holder.subjStat.setText("Необходимо выучить " + allQuestions);

        } else {
            holder.subjStat.setText("Выполнено " + comQues + " из " + allQuestions);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onClickPlanItem(subject, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder {

        private TextView subjName;
        private TextView subjStat;


        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);

            subjName = itemView.findViewById(R.id.subj_name);
            subjStat = itemView.findViewById(R.id.subj_stat);

        }
    }
}
