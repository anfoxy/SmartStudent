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

import com.example.studentapp.R;
import com.example.studentapp.databinding.FragmentCompareGameBinding;
import com.example.studentapp.databinding.FragmentLoadingGameBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Game;
import com.example.studentapp.db.ServiceBuilder;

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


public class LoadingGameFragment extends Fragment {

    ApiInterface apiInterface;
    FragmentLoadingGameBinding binding;
    LoadingGameFragmentArgs args;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        /*plan= MainActivity.myDBManager.getFromDB().stream()
                .filter( c -> c.getSub().getNameOfSubme().equals(args.getId())).collect(Collectors.toList()).get(0);

        binding.nameSub.setText(args.getId());

        */
        Schedulers.start();
       if(args.getStatus().equals("HOST")) {
           System.out.println("хост");
           observableHost();
       }
       if(args.getStatus().equals("FRIEND")){
           System.out.println("друг");
           observableFriend();
       }
        binding.Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //закрываем игру

            }
        });


    }


   private void observableFriend(){

       Observable<String> observable = apiInterface.gameCheckStart(args.getIdGame())
               .repeatWhen(completed -> completed.delay(3, TimeUnit.SECONDS))
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread());

       observable.subscribe(new Observer<String>() {
           @Override
           public void onSubscribe(Disposable d) {

           }

           @Override
           public void onNext(String status) {
               if(status.equals("EXPECTED")) {
                   // хост ожидает нашего подключения
                   Schedulers.shutdown();
                   Call<Game> getUser = apiInterface.gameSetStatus(args.getIdGame(),"ACCEPTED");
                   getUser.enqueue(new Callback<Game>() {
                       @Override
                       public void onResponse(Call<Game> call, Response<Game> response) {
                           if(response.body() != null){
                               observableFriend();
                           }
                       }
                       @Override
                       public void onFailure(Call<Game> call, Throwable t) {
                       }
                   });

               }
               if(status.equals("NOT")) {
                   // если произошла какая-то ошибка, и данной игры нет
                   Schedulers.shutdown();
                   binding.text.setText("Игра была завершена.");
               }
               if(status.equals("STARTED")) {
                   // игра уже была запущена и мы должны вернуться к игре
                   Schedulers.shutdown();
                   LoadingGameFragmentDirections
                           .ActionLoadingGameFragmentToQuestionGameFragment action =
                           LoadingGameFragmentDirections
                                   .actionLoadingGameFragmentToQuestionGameFragment(args.getIdGame(),"FRIEND");
                   Navigation.findNavController(getView()).navigate(action);
               }

           }

           @Override
           public void onError(Throwable e) {

           }

           @Override
           public void onComplete() {

           }
       });
   }

    private void observableHost(){
        Observable<String> observable = apiInterface.gameCheckStart(args.getIdGame())
                .repeatWhen(completed -> completed.delay(3, TimeUnit.SECONDS))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String status) {
                if(status.equals("ACCEPTED")) {
                    // друг принял игру и мы должны теперь ее запустить
                    Schedulers.shutdown();

                    Call<Game> getUser = apiInterface.gameStart(args.getIdGame());
                    getUser.enqueue(new Callback<Game>() {
                        @Override
                        public void onResponse(Call<Game> call, Response<Game> response) {
                            if(response.body() != null){
                                LoadingGameFragmentDirections
                                        .ActionLoadingGameFragmentToQuestionGameFragment action =
                                        LoadingGameFragmentDirections
                                                .actionLoadingGameFragmentToQuestionGameFragment(args.getIdGame(),"HOST");
                                Navigation.findNavController(getView()).navigate(action);
                            }
                        }
                        @Override
                        public void onFailure(Call<Game> call, Throwable t) {
                        }
                    });


                }
                if(status.equals("NOT")) {
                    // если произошла какая-то ошибка, и данной игры нет
                    Schedulers.shutdown();
                    binding.text.setText("Игра была завершена.");
                }
                if(status.equals("STARTED")) {
                    // игра уже была запущена и мы должны вернуться к игре
                    Schedulers.shutdown();
                    LoadingGameFragmentDirections
                            .ActionLoadingGameFragmentToQuestionGameFragment action =
                            LoadingGameFragmentDirections
                                    .actionLoadingGameFragmentToQuestionGameFragment(args.getIdGame(),"HOST");
                    Navigation.findNavController(getView()).navigate(action);
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Schedulers.shutdown();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_loading_game, container, false);
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = LoadingGameFragmentArgs.fromBundle(getArguments());
        Paper.init(getContext());
        return binding.getRoot();
    }

}