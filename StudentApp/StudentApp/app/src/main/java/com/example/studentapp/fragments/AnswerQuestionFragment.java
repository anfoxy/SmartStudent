package com.example.studentapp.fragments;

import android.graphics.Color;
import android.graphics.fonts.SystemFonts;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.fragment.app.Fragment;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.al.Question;
import com.example.studentapp.al.Study;
import com.example.studentapp.databinding.FragmentAnswerQuestionBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Users;

import java.io.IOException;
import java.text.BreakIterator;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import io.paperdb.Paper;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class AnswerQuestionFragment extends Fragment {

    FragmentAnswerQuestionBinding binding;
    ApiInterface apiInterface;
    AnswerQuestionFragmentArgs args;
    //ArrayList<Questions> questions;
    int number = 1;
    private Spannable spannable;
    private int selectedCount = 0;
    private int totalCount = 0;
    private int CountAnswerText = 0;
    private double textKnowGet; //процент
    OkHttpClient client = new OkHttpClient();
    okhttp3.Response response;
    Study study;
    Question nowQuest;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setData();
        PlanToSub plan= MainActivity.myDBManager.getFromDB().stream()
                .filter( c -> c.getSub().getNameOfSubme().equals(args.getId())).collect(Collectors.toList()).get(0);
        plan.plusDayToPlan(LocalDate.now());
        study=new Study(plan.getSub(),
                plan.getFuturePlan().get(0).getSizeOfQuetion(), plan.getTodayLearned());
        nowQuest=study.GetNewQuestion();
        setQuestions();

        //кнопка нажатия на посмотреть ответ
        binding.lookAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.lookAnswerButton.setVisibility(View.INVISIBLE);
                binding.scrollView2.setVisibility(View.VISIBLE);
                binding.imageView5.setVisibility(View.VISIBLE);
                CountAnswerText = CountSpen();
                if(CountAnswerText <5) { //5 предложений
                    binding.textAnswer.setVisibility(View.VISIBLE);
                    binding.yesAnswerButton.setVisibility(View.VISIBLE);
                    binding.noAnswerButton.setVisibility(View.VISIBLE);
                    binding.speakButton.setVisibility(View.VISIBLE);
                    binding.textYesNo.setVisibility(View.VISIBLE);
                    binding.textYesNo.setText("Проверив себя, нажмите на одну из кнопок 'Знаю' или 'Не знаю'");
                    binding.nextBtn.setVisibility(View.INVISIBLE);
                } else {
                    TextSpen();
                    binding.textAnswer.setVisibility(View.VISIBLE);
                    binding.speakButton.setVisibility(View.VISIBLE);
                    binding.nextBtn.setVisibility(View.VISIBLE);
                    binding.textYesNo.setVisibility(View.VISIBLE);
                    binding.textYesNo.setText("Нажмите на те предложения, на котрые вы вероятно ответили, перед тем, как нажать 'Посмотреть ответ'");
                }
            }
        });
        binding.speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        apiRequest();
                    }
                });
                thr.start();
            }
        });

        //не знаю
        binding.noAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number++;
                study.clickReady(0.0);
                MainActivity.myDBManager.updateQuestionsToSubject(plan);
                plan.progress();
                MainActivity.myDBManager.updateSubTodayLearned(plan);
                Users.getUser().currentUpdateDbTime();
                if(study.isEndOfPlan()){
                    Toast.makeText(getContext(),
                            "Вы прошли план на сегодня по этому предмету.", Toast.LENGTH_SHORT).show();
                    NavDirections action = AnswerQuestionFragmentDirections.actionAnswerQuestionFragmentToCalendarFragment();
                    Navigation.findNavController(getView()).navigate(action);
                }
                else{
                    nowQuest=study.GetNewQuestion();
                    setQuestions();
                }
            }
        });
        //знаю
        binding.yesAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number++;
                study.clickReady(1.0);
                MainActivity.myDBManager.updateQuestionsToSubject(plan);
                plan.progress();
                MainActivity.myDBManager.updateSubTodayLearned(plan);
                Users.getUser().currentUpdateDbTime();
                if(study.isEndOfPlan()){
                    Toast.makeText(getContext(),
                            "Вы прошли план на сегодня по этому предмету.", Toast.LENGTH_SHORT).show();
                    NavDirections action = AnswerQuestionFragmentDirections.actionAnswerQuestionFragmentToCalendarFragment();
                    Navigation.findNavController(getView()).navigate(action);
                }
                else{
                    nowQuest=study.GetNewQuestion();
                    setQuestions();
                }

            }
        });
        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // после выделения текста который мы знаем
                number++;
                selectedCount = 0;
                study.clickReady(textKnowGet*0.01);
                MainActivity.myDBManager.updateQuestionsToSubject(plan);
                plan.progress();
                MainActivity.myDBManager.updateSubTodayLearned(plan);
                Users.getUser().currentUpdateDbTime();
                if(study.isEndOfPlan()){
                    Toast.makeText(getContext(),
                            "Вы прошли план на сегодня по этому предмету.", Toast.LENGTH_SHORT).show();
                    NavDirections action = AnswerQuestionFragmentDirections.actionAnswerQuestionFragmentToCalendarFragment();
                    Navigation.findNavController(getView()).navigate(action);
                }
                else{
                    nowQuest=study.GetNewQuestion();
                    setQuestions();
                }

            }
        });

    }


    private void setQuestions() {
        if (!study.isEndOfPlan()){ //проверка на конец тестирования
            binding.textYesNo.setText("Пожалуйста, ознакомьтесь с вопросом темы, подумайте, как на него необходимо ответить, нажмите 'Посмотреть ответ' и сделайте вердикт.");
            binding.lookAnswerButton.setVisibility(View.VISIBLE);
            binding.textAnswer.setVisibility(View.INVISIBLE);
            binding.yesAnswerButton.setVisibility(View.INVISIBLE);
            binding.noAnswerButton.setVisibility(View.INVISIBLE);
            binding.speakButton.setVisibility(View.INVISIBLE);
            binding.nextBtn.setVisibility(View.INVISIBLE);
            binding.scrollView2.setVisibility(View.INVISIBLE);
            binding.imageView5.setVisibility(View.INVISIBLE);
            binding.textNumber.setText("Вопрос "+number);
            binding.textQuestion.setText(nowQuest.getQuestion().toString());
            binding.textAnswer.setText(nowQuest.getAnswer().toString());
        } else {
            Toast.makeText(getContext(), "Вопросы по данному предмету закончились", Toast.LENGTH_SHORT).show();
            @NonNull NavDirections action = AnswerQuestionFragmentDirections.actionAnswerQuestionFragmentToCalendarFragment();
            Navigation.findNavController(getView()).navigate(action);
        }
    }

//    private void setData(){
//        Call<Subjects> subjectsCall = apiInterface.getSubjectById(args.getId());
//        subjectsCall.enqueue(new Callback<Subjects>() {
//            @Override
//            public void onResponse(Call<Subjects> call, Response<Subjects> response) {
//                questions = response.body().getQuestions();
//                setQuestions();
//            }
//
//            @Override
//            public void onFailure(Call<Subjects> call, Throwable t) {
//                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void TextSpen() {
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(nowQuest.getAnswer());
        int start = iterator.first();
        int end = iterator.next();
        spannable = new SpannableString(nowQuest.getAnswer());
        totalCount = 0;
        while (end != BreakIterator.DONE) {
            final String sentence = nowQuest.getAnswer().substring(start, end);
            ClickableSpan clickableSpan = new SentenceSpan();
            spannable.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = end;
            end = iterator.next();
            totalCount++;
        }

        binding.textAnswer.setText(spannable);
        binding.textAnswer.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private int CountSpen() {
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(nowQuest.getAnswer());
        int count = 0;
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            count++;
        }
        return count;
    }

    private void apiRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("text", nowQuest.getAnswer())
                .add("lang", "ru-RU")
                .add("voice", "filipp")
                .add("format", "mp3")
                .add("folderId", "")
                .build();

        Request request = new Request.Builder()
                .url("https://tts.api.cloud.yandex.net/speech/v1/tts:synthesize")
                .header("Authorization", "API-key AQVNwoX88VhKO5IYmYMvl4IsgdDq3MmnrVSp56gW")
                .post(formBody)
                .build();
        okhttp3.Call call = client.newCall(request);
        response = null;
        try {
            response = call.execute();
            byte[] resp = Objects.requireNonNull(response.body()).bytes();
            String stringText = Base64.getEncoder().encodeToString(resp);
            PlayAudio(stringText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void PlayAudio(String base64Audio) {
        try {
            String url = "data:audio/mp3;base64," + base64Audio;
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }

    // Кастомный класс для обработки нажатий на предложения
    private class SentenceSpan extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            int start = spannable.getSpanStart(this);
            int end = spannable.getSpanEnd(this);
            BackgroundColorSpan[] spans = spannable.getSpans(start, end, BackgroundColorSpan.class);
            if (spans.length > 0) {
                // Если предложение уже выделено, то удаляем выделение
                spannable.removeSpan(spans[0]);
                selectedCount--;
            } else {
                // Если предложение не выделено, то выделяем его
                spannable.setSpan(new BackgroundColorSpan(Color.YELLOW), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                selectedCount++;
            }
            // Обновляем текст с процентом выделенных предложений
//            String selectedPercentage = String.format(Locale.US, "Выделено: %d/%d (%.1f%%)", selectedCount, totalCount, (double) selectedCount / totalCount * 100);

            textKnowGet=(double) selectedCount / totalCount * 100;
            binding.textAnswer.setText(spannable);
//            binding.textAnswer.append("\n" + selectedPercentage);
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_answer_question, container, false);
        args = AnswerQuestionFragmentArgs.fromBundle(getArguments());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        Paper.init(getContext());
        return binding.getRoot();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}