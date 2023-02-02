package com.example.studentapp.fragments.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.adapters.GamesAdapter;
import com.example.studentapp.adapters.SubjectAdapter;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.databinding.FragmentListGameHistoryBinding;
import com.example.studentapp.databinding.FragmentListSubjectGameBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Friends;
import com.example.studentapp.db.Game;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Users;
import com.example.studentapp.fragments.FriendsProfileFragmentDirections;

import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListGameHistoryFragment extends Fragment {

    FragmentListGameHistoryBinding binding;
    ApiInterface apiInterface;
    GamesAdapter.OnItemClickListener itemClickListener;
    ListGameHistoryFragmentArgs args;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemClickListener = new GamesAdapter.OnItemClickListener() {
            @Override
            public void onClickSubject(Game game, int position) {

                ListGameHistoryFragmentDirections
                        .ActionListGameHistoryFragmentToLoadingGameFragment action =
                        ListGameHistoryFragmentDirections
                                .actionListGameHistoryFragmentToLoadingGameFragment(game.getId(), game.getStatus());
                Navigation.findNavController(getView()).navigate(action);

            }
        };
        setGames();
        // Navigation.findNavController(getView()).navigate(FriendsProfileFragmentDirections.actionFriendsProfileFragmentToListSubjectGameFragment(args.getId()));

        binding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(ListGameHistoryFragmentDirections.actionListGameHistoryFragmentToListSubjectGameFragment(args.getId()));
            }
        });

    }
    private void setGames() {
        Call<ArrayList<Game>> games = apiInterface.getAllGamesByUserId(args.getId(),Users.getUser());
        games.enqueue(new Callback<ArrayList<Game>>() {
            @Override
            public void onResponse(Call<ArrayList<Game>> call, Response<ArrayList<Game>> response) {

                if (response.body() != null) {
                    binding.listSubView.setHasFixedSize(true);
                    binding.listSubView.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.listSubView.setAdapter(new GamesAdapter(getContext(), response.body(), itemClickListener));
                    if (response.body().isEmpty()) {
                        binding.predmNullPng.setVisibility(View.VISIBLE);
                        binding.textList.setVisibility(View.VISIBLE);
                    } else {
                        binding.textList.setVisibility(View.INVISIBLE);
                        binding.predmNullPng.setVisibility(View.INVISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Game>> call, Throwable t) {
                if (getContext() != null)
                    Toast.makeText(getContext(), "Сервер не отвечает", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_list_game_history, container, false);
        Paper.init(getContext());
        args = ListGameHistoryFragmentArgs.fromBundle(getArguments());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        return binding.getRoot();
    }
}