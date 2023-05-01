package com.example.studentapp.fragments.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.adapters.SubjectAdapterGame;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.databinding.FragmentListSubjectGameBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;

import java.util.ArrayList;

import io.paperdb.Paper;


public class ListSubjectGameFragment extends Fragment {

    FragmentListSubjectGameBinding binding;
    ApiInterface apiInterface;
    SubjectAdapterGame.OnItemClickListener itemClickListener;
    ListSubjectGameFragmentArgs args;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemClickListener =new SubjectAdapterGame.OnItemClickListener() {
            @Override
            public void onClickSubject(PlanToSub subject, int position) {
                // переход на экран редактирования режима игры
                ListSubjectGameFragmentDirections
                        .ActionListSubjectGameFragmentToSettingGameFragment action =
                        ListSubjectGameFragmentDirections
                                .actionListSubjectGameFragmentToSettingGameFragment(subject.getSub().getNameOfSubme(),args.getId());
                 Navigation.findNavController(getView()).navigate(action);
            }
        };
        setSub();

    }
    private void setSub(){
        ArrayList<PlanToSub> subjs = MainActivity.myDBManager.getFromDB();
        binding.listSubView.setHasFixedSize(true);
        binding.listSubView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listSubView.setAdapter(new SubjectAdapterGame(getContext(), subjs, itemClickListener));
        if(subjs.size() == 0) {
            binding.predmNullPng.setVisibility(View.VISIBLE);
            binding.textList.setVisibility(View.VISIBLE);
            binding.textInfoSubGame.setVisibility(View.GONE);
            binding.listSubView.setVisibility(View.GONE);

        } else {
            binding.textList.setVisibility(View.GONE);
            binding.predmNullPng.setVisibility(View.GONE);
            binding.textInfoSubGame.setVisibility(View.VISIBLE);
            binding.listSubView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_list_subject_game, container, false);
        Paper.init(getContext());
        args = ListSubjectGameFragmentArgs.fromBundle(getArguments());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        return binding.getRoot();
    }
}