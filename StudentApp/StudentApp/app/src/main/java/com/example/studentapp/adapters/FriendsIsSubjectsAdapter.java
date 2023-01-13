package com.example.studentapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentapp.R;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;

import java.util.ArrayList;

public class FriendsIsSubjectsAdapter extends RecyclerView.Adapter<FriendsIsSubjectsAdapter.ViewHolder> {

    public interface OnItemClickListenerIs{

        void onClickFriendsCheck(ArrayList<Subjects> friends, int position);
    }

    private Context context;
    private ArrayList<Subjects> subjects;
    private FriendsIsSubjectsAdapter.OnItemClickListenerIs onItemClickListenerIs;


    public FriendsIsSubjectsAdapter(Context context, ArrayList<Subjects> subjects, FriendsIsSubjectsAdapter.OnItemClickListenerIs onItemClickListenerIs) {
        this.context = context;
        this.subjects = subjects;
        this.onItemClickListenerIs = onItemClickListenerIs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_friends_is_item_sub, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Subjects subj = subjects.get(position);
        holder.subTv.setText(subj.getName());

        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListenerIs.onClickFriendsCheck(subjects, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView subTv;
        ImageView check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subTv = itemView.findViewById(R.id.subjName);
            check = itemView.findViewById(R.id.check);
        }
    }
}
