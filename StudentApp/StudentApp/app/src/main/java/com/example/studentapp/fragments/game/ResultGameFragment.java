package com.example.studentapp.fragments.game;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.R;
import com.example.studentapp.adapters.QuestionAddRecycler;
import com.example.studentapp.adapters.QuestionGameAddRecycler;
import com.example.studentapp.al.Question;
import com.example.studentapp.databinding.FragmentCompareGameBinding;
import com.example.studentapp.databinding.FragmentResultGameBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Game;
import com.example.studentapp.db.GameSubjects;
import com.example.studentapp.db.ServiceBuilder;

import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ResultGameFragment extends Fragment {

    ApiInterface apiInterface;
    FragmentResultGameBinding binding;
    ResultGameFragmentArgs args;
    Game game;
    ArrayList<GameSubjects> gameSubjects ;
    int friendWin;
    int hostWin;
    boolean allEnd = true;
    QuestionGameAddRecycler.OnItemClickListener itemClick;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        game = new Game(args.getId(),args.getStatus());

        Call<ArrayList<GameSubjects>> getRes = apiInterface.gameGetQuestionList(game);
        getRes.enqueue(new Callback<ArrayList<GameSubjects>>() {
            @Override
            public void onResponse(Call<ArrayList<GameSubjects>> call, Response<ArrayList<GameSubjects>> response) {
                if (response.body() != null) {
                    gameSubjects= response.body();
                    setInfo();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GameSubjects>> call, Throwable t) {
            }
        });
        itemClick = new QuestionGameAddRecycler.OnItemClickListener() {

            @Override
            public void onClickQuestion(GameSubjects ques, int position) {
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(getContext());

                // set the custom layout
                final View customLayout
                        = getLayoutInflater()
                        .inflate(
                                R.layout.dialog_game_question,
                                null);
                builder.setView(customLayout);


                AlertDialog dialog
                        = builder.create();

                TextView answer = customLayout.findViewById(R.id.text_answer);
                TextView question = customLayout.findViewById(R.id.text_question);
                TextView answerHost = customLayout.findViewById(R.id.text_answer_host);
                TextView answerFriend = customLayout.findViewById(R.id.text_answer_friend);
                AppCompatButton clsBtn = customLayout.findViewById(R.id.exit);

                answer.setText(""+ques.getAnswer());
                question.setText(""+ques.getQuestion());
                answerHost.setText(""+ques.getAnswerHost());
                answerFriend.setText(""+ques.getAnswerFriend());
                if(ques.getResultFriend()!=null && ques.getResultHost()!= null)
                if((ques.getResultFriend()==1 && ques.getResultHost() ==1)||
                        (ques.getResultFriend()==2 && ques.getResultHost() ==1)||
                        (ques.getResultFriend()==1 && ques.getResultHost() ==2)) {
                    answerHost.setBackgroundColor(Color.GREEN);
                    answerFriend.setBackgroundColor(Color.RED);
                }
                else if((ques.getResultFriend()==0 && ques.getResultHost() ==0)||
                        (ques.getResultFriend()==2 && ques.getResultHost() ==0)||
                        (ques.getResultFriend()==0 && ques.getResultHost() ==2)) {
                    answerHost.setBackgroundColor(Color.RED);
                    answerFriend.setBackgroundColor(Color.GREEN);
                }
                else {
                    answerHost.setBackgroundColor(Color.GREEN);
                    answerFriend.setBackgroundColor(Color.GREEN);
                }

                clsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

        };
    }

    private void setInfo() {
        if(gameSubjects.isEmpty()) return ;
        binding.nameSub.setText(whoWon());

        System.out.println(gameSubjects.get(0));
        binding.resultFriend.setText(""+gameSubjects.get(0).getGameId().getFriendId().getFriendId().getLogin()+": "+friendWin);
        binding.resultHost.setText(""+gameSubjects.get(0).getGameId().getFriendId().getUserId().getLogin()+": "+hostWin);
        setQuestions();



    }

    private void setQuestions(){
        binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listVop.setHasFixedSize(true);
        binding.listVop.setAdapter(new QuestionGameAddRecycler(getContext(), gameSubjects, itemClick));
    }

    private String whoWon() {

        friendWin = 0;
        hostWin = 0;

        for (GameSubjects gs: gameSubjects) {

            if(gs.getResultFriend() == null || gs.getResultHost()== null) {
                allEnd = false;
                friendWin = 0;
                hostWin = 0;
                return "Ожидаем другого участника";
            }
            if((gs.getResultFriend()==1 && gs.getResultHost() ==1)||
                    (gs.getResultFriend()==2 && gs.getResultHost() ==1)||
                    (gs.getResultFriend()==1 && gs.getResultHost() ==2))
                hostWin++;
            else if((gs.getResultFriend()==0 && gs.getResultHost() ==0)||
                    (gs.getResultFriend()==2 && gs.getResultHost() ==0)||
                    (gs.getResultFriend()==0 && gs.getResultHost() ==2))
                friendWin++;
            else {
                hostWin++;
                friendWin++;
            }
        }

        if (hostWin > friendWin) {
            return "Выиграл "+gameSubjects.get(0).getGameId().getFriendId().getUserId().getLogin();
        } else if (friendWin > hostWin) {
            return "Выиграл "+gameSubjects.get(0).getGameId().getFriendId().getFriendId().getLogin();
        } else {
            return "Ничья";
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result_game, container, false);
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = ResultGameFragmentArgs.fromBundle(getArguments());
        Paper.init(getContext());
        return binding.getRoot();
    }

}