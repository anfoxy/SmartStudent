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
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    public interface OnItemClickListener{
        void onClickFriends(Users friends, int position);
    }

    private Context context;
    private ArrayList<Users> friends;
    private OnItemClickListener onItemClickListener;

    public FriendsAdapter(Context context, ArrayList<Users> friends, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.friends = friends;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_friends_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Users subj = friends.get(position);
        holder.subTv.setText(subj.getLogin());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClickFriends(subj, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView subTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subTv = itemView.findViewById(R.id.subjName);
        }
    }
}
