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
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.R;
import com.example.studentapp.adapters.QuestionAddRecycler;
import com.example.studentapp.adapters.SubjectAdapterGameResult;
import com.example.studentapp.al.Question;
import com.example.studentapp.databinding.FragmentCompareGameBinding;
import com.example.studentapp.databinding.FragmentResultGameBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Game;
import com.example.studentapp.db.GameHistory;
import com.example.studentapp.db.GameSubjects;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Users;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ResultGameFragment extends Fragment {

    private ApiInterface apiInterface;
    private FragmentResultGameBinding binding;
    private ResultGameFragmentArgs args;
    private ArrayList<GameSubjects> gameSubjects ;
    private int friendWin;
    private int hostWin;
    private SubjectAdapterGameResult.OnItemClickListener itemClick;
    private boolean endGame = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setGameSubjects();
        update();

        itemClick = new SubjectAdapterGameResult.OnItemClickListener() {

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
                int color = ContextCompat.getColor(getContext(), R.color.normal);
                int color2 = ContextCompat.getColor(getContext(), R.color.no_normal);

                answer.setText(""+ques.getAnswer());
                question.setText(""+ques.getQuestion());
                answerHost.setText(""+ques.getAnswerHost());
                answerFriend.setText(""+ques.getAnswerFriend());
                if(ques.getResultFriend()!=null && ques.getResultHost()!= null)
                if((ques.getResultFriend()==1 && ques.getResultHost() ==1)||
                        (ques.getResultFriend()==2 && ques.getResultHost() ==1)||
                        (ques.getResultFriend()==1 && ques.getResultHost() ==2)) {
                    answerHost.setBackgroundColor(color);
                    answerFriend.setBackgroundColor(color2);
                }
                else if((ques.getResultFriend()==0 && ques.getResultHost() ==0)||
                        (ques.getResultFriend()==2 && ques.getResultHost() ==0)||
                        (ques.getResultFriend()==0 && ques.getResultHost() ==2)) {
                    answerHost.setBackgroundColor(color2);
                    answerFriend.setBackgroundColor(color);
                }
                else {
                    answerHost.setBackgroundColor(color);
                    answerFriend.setBackgroundColor(color);
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
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<Integer> getRes = apiInterface.deleteGame(new GameHistory(null, Users.getUser(),new Game(args.getId())));
                getRes.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.body() != null ) {
                            Navigation.
                                    findNavController(getView()).
                                    navigate(ResultGameFragmentDirections
                                            .actionResultGameFragmentToFriendsProfileFragment(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        System.out.println("Ошибка");
                    }
                });

            }
        });
    }

    private void setGameSubjects(){
        Call<ArrayList<GameSubjects>> getRes = apiInterface.gameGetQuestionList(args.getId());
        getRes.enqueue(new Callback<ArrayList<GameSubjects>>() {
            @Override
            public void onResponse(Call<ArrayList<GameSubjects>> call, Response<ArrayList<GameSubjects>> response) {
                if (response.body() != null) {
                    gameSubjects = response.body();
                    setInfo();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GameSubjects>> call, Throwable t) {
            }
        });
    }

    private void setInfo() {
        if(gameSubjects.isEmpty()) return ;
        binding.nameSub.setText(whoWon());

        binding.resultFriend.setText("Результат друга\n"+friendWin+" бал(ов)");
        binding.resultHost.setText("Ваш результат\n"+hostWin+" бал(ов)");
        setQuestions();



    }

    private void setQuestions(){
        binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listVop.setHasFixedSize(true);
        binding.listVop.setAdapter(new SubjectAdapterGameResult(getContext(), gameSubjects, itemClick));
    }

    private String whoWon() {

        friendWin = 0;
        hostWin = 0;

        for (GameSubjects gs: gameSubjects) {

            if(gs.getResultFriend() == null || gs.getResultHost()== null) {

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
            return "Вы выиграли!";
        } else if (friendWin > hostWin) {
            return "Победу одержал "+gameSubjects.get(0).getGameId().getFriendId().getFriendId().getLogin();
        } else {
            return "Ничья";
        }
    }

    Disposable disposable = null;
    private void update(){
        if(!endGame) {
            Observable<Boolean> observable = apiInterface.gameCheckEnd(args.getId())
                    .repeatWhen(completed -> completed.delay(3, TimeUnit.SECONDS))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            observable.subscribe(new Observer<Boolean>() {
                @Override
                public void onSubscribe(Disposable d) {
                    disposable = d;
                }

                @Override
                public void onNext(Boolean status) {
                    if(status) {
                        disposable.dispose();
                        setGameSubjects();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    //оповестить о том, что произошли проблемы с инетом
                }

                @Override
                public void onComplete() {

                }
            });
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