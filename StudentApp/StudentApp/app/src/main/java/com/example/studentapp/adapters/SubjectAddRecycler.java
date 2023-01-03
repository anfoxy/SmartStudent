package com.example.studentapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentapp.R;
import com.example.studentapp.al.Question;
import com.example.studentapp.db.Questions;

import java.util.ArrayList;

public class SubjectAddRecycler extends RecyclerView.Adapter<SubjectAddRecycler.ViewHolder> {

    public interface OnItemClickListener{
        void onClickQuestion(Question ques, int position);
    }

    private Context context;
    private ArrayList<Question> questions;
    private OnItemClickListener onItemClickListener;

    public SubjectAddRecycler(Context context, ArrayList<Question> questions, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.questions = questions;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvName.setText(questions.get(position).getQuestion());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClickQuestion(questions.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.subjName);
        }
    }
}
