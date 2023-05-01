package com.example.studentapp.fragments.game;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.studentapp.R;
import com.example.studentapp.databinding.FragmentCompareGameBinding;
import com.example.studentapp.databinding.FragmentLoadingGameBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Game;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Users;

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
    Disposable disposable = null;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observableAll();
       /*if(args.getStatus().equals("HOST")) {
           System.out.println("хост");
           observableHost();
       }
       if(args.getStatus().equals("FRIEND")){
           System.out.println("друг");
           observableFriend();
       }*/
    }
    private void observableAll(){
        Observable<String> observable = apiInterface.gameCheckStart(args.getIdGame(), Users.getUser().getId())
                .repeatWhen(completed -> completed.delay(3, TimeUnit.SECONDS))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(String status) {

                System.out.println("status " + status);
                if(status.equals("EXPECTED")) {
                    // хост ожидает нашего подключения
                    disposable.dispose();
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(getContext());

                    final View customLayout
                            = getLayoutInflater()
                            .inflate(
                                    R.layout.dialog_start_game,
                                    null);
                    builder.setView(customLayout);

                    AlertDialog dialog
                            = builder.create();

                    TextView text = customLayout.findViewById(R.id.text_start);
                    text.setText("Принять игру?");
                    Button out = customLayout.findViewById(R.id.start_game);
                    AppCompatButton clsBtn = customLayout.findViewById(R.id.cancel_game);

                    out.setText("Начать");
                    clsBtn.setText("Отказаться");
                    out.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Call<Game> getUser = apiInterface.gameSetStatus(args.getIdGame(),"ACCEPTED");
                            getUser.enqueue(new Callback<Game>() {
                                @Override
                                public void onResponse(Call<Game> call, Response<Game> response) {
                                    if(response.body() != null){
                                        observableAll();
                                    }
                                }
                                @Override
                                public void onFailure(Call<Game> call, Throwable t) {
                                }
                            });
                            dialog.dismiss();
                        }
                    });

                    clsBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Call<Game> getUser = apiInterface.abandonTheGame(args.getIdGame());
                            getUser.enqueue(new Callback<Game>() {
                                @Override
                                public void onResponse(Call<Game> call, Response<Game> response) {
                                    binding.text.setText("Игра отменена");
                                }
                                @Override
                                public void onFailure(Call<Game> call, Throwable t) {
                                }
                            });
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                   /* Call<Game> getUser = apiInterface.gameSetStatus(args.getIdGame(),"ACCEPTED");
                    getUser.enqueue(new Callback<Game>() {
                        @Override
                        public void onResponse(Call<Game> call, Response<Game> response) {
                            if(response.body() != null){
                                observableAll();
                            }
                        }
                        @Override
                        public void onFailure(Call<Game> call, Throwable t) {
                        }
                    });*/
                }

                if(status.equals("ACCEPTED")) {
                    // друг принял игру и мы должны теперь ее запуститьч
                    disposable.dispose();
                    Call<Game> getUser = apiInterface.gameStart(args.getIdGame());
                    getUser.enqueue(new Callback<Game>() {
                        @Override
                        public void onResponse(Call<Game> call, Response<Game> response) {
                            if(response.body() != null){
                                binding.text.setText("Идет загрузка...");
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                LoadingGameFragmentDirections
                                        .ActionLoadingGameFragmentToQuestionGameFragment action =
                                        LoadingGameFragmentDirections
                                                .actionLoadingGameFragmentToQuestionGameFragment(args.getIdGame());
                                Navigation.findNavController(getView()).navigate(action);
                            }
                        }
                        @Override
                        public void onFailure(Call<Game> call, Throwable t) {
                        }
                    });
                }

               if(status.equals("WAIT")) {
                    binding.text.setText("Ожидание другого пользователя..");
                }
                if(status.equals("NOT")) {
                    // если произошла какая-то ошибка, и данной игры нет
                    disposable.dispose();
                    binding.text.setText("Игра была завершена.");
                }
                if(status.equals("QUEST_START")) {
                    // игра уже была запущена и мы должны вернуться к игре
                    disposable.dispose();
                    binding.text.setText("Идет загрузка игры...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LoadingGameFragmentDirections
                            .ActionLoadingGameFragmentToQuestionGameFragment action =
                            LoadingGameFragmentDirections
                                    .actionLoadingGameFragmentToQuestionGameFragment(args.getIdGame());
                    Navigation.findNavController(getView()).navigate(action);
                }
                if(status.equals("RESULT_START")) {
                    // игра уже на этапе результирования
                    disposable.dispose();
                    binding.text.setText("Идет загрузка игры..");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LoadingGameFragmentDirections
                            .ActionLoadingGameFragmentToCompareGameFragment action =
                            LoadingGameFragmentDirections
                                    .actionLoadingGameFragmentToCompareGameFragment(args.getIdGame());
                    Navigation.findNavController(getView()).navigate(action);
                }

                if(status.equals("END")) {
                    // игра уже была запущена и мы должны вернуться к игре
                    disposable.dispose();
                    binding.text.setText("Идет загрузка игры...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LoadingGameFragmentDirections
                            .ActionLoadingGameFragmentToResultGameFragment action =
                            LoadingGameFragmentDirections
                                    .actionLoadingGameFragmentToResultGameFragment(args.getIdGame());
                    Navigation.findNavController(getView()).navigate(action);
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

 /*  private void observableFriend(){

      // Schedulers.start();

       Observable<String> observable = apiInterface.gameCheckStart(args.getIdGame())
               .repeatWhen(completed -> completed.delay(3, TimeUnit.SECONDS))
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread());

      observable.subscribe(new Observer<String>() {
           @Override
           public void onSubscribe(Disposable d) {
               disposable = d;
           }

           @Override
           public void onNext(String status) {
               if(status.equals("EXPECTED")) {
                   // хост ожидает нашего подключения
                 //  Schedulers.shutdown();

                   disposable.dispose();
                   Call<Game> getUser = apiInterface.gameSetStatus(args.getIdGame(),"ACCEPTED");
                   getUser.enqueue(new Callback<Game>() {
                       @Override
                       public void onResponse(Call<Game> call, Response<Game> response) {
                           if(response.body() != null){
                               binding.text.setText("Ожидание подключения другого пользователя.");
                               observableFriend();
                           }
                       }
                       @Override
                       public void onFailure(Call<Game> call, Throwable t) {
                       }
                   });

               }
               if(status.equals("QUESTION_FRIEND")||status.equals("ACCEPTED")) {
                   binding.text.setText("Ожидание синхронизации хоста..");
               }
               if(status.equals("NOT")) {
                   // если произошла какая-то ошибка, и данной игры нет
                //   Schedulers.shutdown();
                   disposable.dispose();
                   binding.text.setText("Игра была завершена.");
               }
               if(status.equals("STARTED")||status.equals("QUESTION_HOST")) {
                   // игра уже была запущена и мы должны вернуться к игре
                  // Schedulers.shutdown();
                   disposable.dispose();
                   binding.text.setText("Идет загрузка игры...");
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   LoadingGameFragmentDirections
                           .ActionLoadingGameFragmentToQuestionGameFragment action =
                           LoadingGameFragmentDirections
                                   .actionLoadingGameFragmentToQuestionGameFragment(args.getIdGame(),"FRIEND");
                   Navigation.findNavController(getView()).navigate(action);
               }
               if(status.equals("RESULT_HOST")||status.equals("RESULT_START")) {
                   // игра уже на этапе результирования
                 //  Schedulers.shutdown();
                   disposable.dispose();
                   binding.text.setText("Подготавливаем результаты..");
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   LoadingGameFragmentDirections
                           .ActionLoadingGameFragmentToCompareGameFragment action =
                           LoadingGameFragmentDirections
                                   .actionLoadingGameFragmentToCompareGameFragment(args.getIdGame(),"FRIEND");
                   Navigation.findNavController(getView()).navigate(action);
               }
               if(status.equals("END")||status.equals("RESULT_FRIEND")) {
                   // игра уже была запущена и мы должны вернуться к игре
                  // Schedulers.shutdown();
                   disposable.dispose();
                   binding.text.setText("Идет загрузка игры...");
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   LoadingGameFragmentDirections
                           .ActionLoadingGameFragmentToResultGameFragment action =
                           LoadingGameFragmentDirections
                                   .actionLoadingGameFragmentToResultGameFragment(args.getIdGame(),"FRIEND");
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
      //  Schedulers.start();
        Observable<String> observable = apiInterface.gameCheckStart(args.getIdGame())
                .repeatWhen(completed -> completed.delay(3, TimeUnit.SECONDS))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(String status) {
                if(status.equals("ACCEPTED")) {
                    // друг принял игру и мы должны теперь ее запустить
                 //   Schedulers.shutdown();
                    disposable.dispose();
                    Call<Game> getUser = apiInterface.gameStart(args.getIdGame());
                    getUser.enqueue(new Callback<Game>() {
                        @Override
                        public void onResponse(Call<Game> call, Response<Game> response) {
                            if(response.body() != null){
                                binding.text.setText("Идет загрузка игры...");
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
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
                if(status.equals("QUESTION_HOST")||status.equals("EXPECTED")) {
                    binding.text.setText("Ожидание другого пользователя..");
                }
                if(status.equals("NOT")) {
                    // если произошла какая-то ошибка, и данной игры нет
                    disposable.dispose();
                    binding.text.setText("Игра была завершена.");
                }
                if(status.equals("STARTED")||status.equals("QUESTION_FRIEND")) {
                    // игра уже была запущена и мы должны вернуться к игре
                   // Schedulers.shutdown();
                    disposable.dispose();
                    binding.text.setText("Идет загрузка игры...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LoadingGameFragmentDirections
                            .ActionLoadingGameFragmentToQuestionGameFragment action =
                            LoadingGameFragmentDirections
                                    .actionLoadingGameFragmentToQuestionGameFragment(args.getIdGame(),"HOST");
                    Navigation.findNavController(getView()).navigate(action);
                }
                if(status.equals("RESULT_FRIEND")||status.equals("RESULT_START")) {
                    // игра уже на этапе результирования
                  //  Schedulers.shutdown();
                    disposable.dispose();
                    binding.text.setText("Идет загрузка игры..");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LoadingGameFragmentDirections
                            .ActionLoadingGameFragmentToCompareGameFragment action =
                            LoadingGameFragmentDirections
                                    .actionLoadingGameFragmentToCompareGameFragment(args.getIdGame(),"HOST");
                    Navigation.findNavController(getView()).navigate(action);
                }
                if(status.equals("END")||status.equals("RESULT_HOST")) {
                    // игра уже была запущена и мы должны вернуться к игре
                   // Schedulers.shutdown();
                    disposable.dispose();
                    binding.text.setText("Идет загрузка игры..");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LoadingGameFragmentDirections
                            .ActionLoadingGameFragmentToResultGameFragment action =
                            LoadingGameFragmentDirections
                                    .actionLoadingGameFragmentToResultGameFragment(args.getIdGame(),"HOST");
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
*/
    @Override
    public void onPause() {
        super.onPause();
        if(disposable != null) disposable.dispose();
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