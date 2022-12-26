package com.example.studentapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class FriendsIsAdapter extends RecyclerView.Adapter<FriendsIsAdapter.ViewHolder> {

    public interface OnItemClickListenerIs{

        void onClickFriendsCheck(ArrayList<Users> friends, int position);
    }

    private Context context;
    private ArrayList<Users> friends;
    private FriendsIsAdapter.OnItemClickListenerIs onItemClickListenerIs;


    public FriendsIsAdapter(Context context, ArrayList<Users> friends, FriendsIsAdapter.OnItemClickListenerIs onItemClickListenerIs) {
        this.context = context;
        this.friends = friends;
        this.onItemClickListenerIs = onItemClickListenerIs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_friends_is_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Users subj = friends.get(position);
        holder.subTv.setText(subj.getLogin());

        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListenerIs.onClickFriendsCheck(friends, position);
            }
        });
     /*   holder.check.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Users myUser = Users.getUser();
                Call<Friends> planCall = apiInterface.friendsDeleteIs(new Friends(0,"",subj,myUser));
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subTv = itemView.findViewById(R.id.subjName);
            check = itemView.findViewById(R.id.check);
        }
    }
}
