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
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.db.Subjects;

import java.util.ArrayList;

import retrofit2.Call;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    public interface OnItemClickListener{
        void onClickSubject(PlanToSub subject, int position);
    }

    private Context context;
    private ArrayList<PlanToSub> subjects;
    private OnItemClickListener onItemClickListener;

    public SubjectAdapter(Context context, ArrayList<PlanToSub> subjects, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.subjects = subjects;
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
        PlanToSub subj = subjects.get(position);
        holder.subTv.setText(subj.getSub().getNameOfSubme());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClickSubject(subj, position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView subTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subTv = itemView.findViewById(R.id.subjName);
        }
    }
}
