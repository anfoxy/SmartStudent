package com.example.studentapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentapp.R;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Friends;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Users;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsInAdapter extends RecyclerView.Adapter<FriendsInAdapter.ViewHolder> {

    public interface OnItemClickListenerIn{
        void onClickFriendsCheck(ArrayList<Users> friends, int position);
        void onClickFriendsClear(ArrayList<Users> friends, int position);
    }

    private Context context;
    private ArrayList<Users> friends;
    private FriendsInAdapter.OnItemClickListenerIn onItemClickListenerIn;

    public FriendsInAdapter(Context context, ArrayList<Users> friends, FriendsInAdapter.OnItemClickListenerIn onItemClickListenerIn) {
        this.context = context;
        this.friends = friends;
        this.onItemClickListenerIn = onItemClickListenerIn;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_friends_in_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Users subj = friends.get(position);
        holder.subTv.setText(subj.getLogin());
        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListenerIn.onClickFriendsCheck(friends, position);
            }
        });
        holder.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListenerIn.onClickFriendsClear(friends, position);
            }
        });


       /* holder.check.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Users myUser = Users.getUser();
                Call<Friends> planCall = apiInterface.friendsAccept(new Friends(null,"",subj,myUser));
                planCall.enqueue(new Callback<Friends>() {
                    @Override
                    public void onResponse(Call<Friends> call, Response<Friends> response) {

                    }
                    @Override
                    public void onFailure(Call<Friends> call, Throwable t) {
                        Log.d("not ok", t.getMessage());
                    }
                });
            }
        });*/

       /* holder.clear.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Call<Friends> planCall = apiInterface.friendsRefuse(new Friends(null,"",subj,myUser));
                planCall.enqueue(new Callback<Friends>() {
                    @Override
                    public void onResponse(Call<Friends> call, Response<Friends> response) {

                    }
                    @Override
                    public void onFailure(Call<Friends> call, Throwable t) {
                        Log.d("not ok", t.getMessage());
                    }
                });
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView subTv;
        ImageView check;
        ImageView clear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subTv = itemView.findViewById(R.id.subjName);
            check = itemView.findViewById(R.id.check);
            clear = itemView.findViewById(R.id.clear);
        }
    }
}
