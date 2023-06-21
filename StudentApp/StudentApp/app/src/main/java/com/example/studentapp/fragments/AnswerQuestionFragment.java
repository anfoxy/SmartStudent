package com.example.studentapp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Color;
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
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Users;
import java.io.IOException;
import java.text.BreakIterator;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Locale;
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
    int number = 1;
    private Spannable spannable;
    private int selectedCount = 0;
    private int totalCount = 0;
    private int CountAnswerText = 0;
    private int emptyStringCount = 0;
    private double textKnowGet; //процент
    OkHttpClient client = new OkHttpClient();
    MediaPlayer mediaPlayer = new MediaPlayer();
    okhttp3.Response response;
    // Переменная, хранящая текущее состояние карточки (сторона с текстом1 или с текстом2)
    private boolean isText1Visible = true;
    // Переменная, хранящая текущее состояние анимации (запущена или нет)
    private boolean isAnimationRunning = false;
    Study study;
    Question nowQuest;
    PlanToSub plan;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setData();
        plan= MainActivity.myDBManager.getFromDB().stream()
                .filter( c -> c.getSub().getNameOfSubme().equals(args.getId())).collect(Collectors.toList()).get(0);
        plan.plusDayToPlan(LocalDate.now());
        study=new Study(plan);
        nowQuest=study.GetNewQuestion();
        setQuestions();
        // Обрабатываем нажатие на карточку
        binding.textNumber.setText(plan.getTodayLearned() + "/ "+plan.getFuturePlan().get(0).getSizeOfQuetion());
        binding.textQuestion.setText(nowQuest.getQuestion().toString());
        binding.textAnswer.setText(nowQuest.getAnswer().toString());
        binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipCard();
            }
        });
        //кнопка нажатия на посмотреть ответ

        binding.voiceBtn.setOnClickListener(new View.OnClickListener() {
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
                isAnimationRunning = false;
                flipCard();
                number++;
                study.clickReady(0.0);
                plan.newSizeQuestionOnFuture();
                MainActivity.myDBManager.updateQuestionsToSubject(plan);
                MainActivity.myDBManager.updateSubTodayLearned(plan);
                Users.getUser().currentUpdateDbTime();
                if(study.isEndOfPlan()){
                    stopAudio();
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
                isAnimationRunning = false;
                flipCard();
                number++;
                study.clickReady(1.0);
                MainActivity.myDBManager.updateQuestionsToSubject(plan);
                MainActivity.myDBManager.updateSubTodayLearned(plan);
                Users.getUser().currentUpdateDbTime();
                if(study.isEndOfPlan()){
                    stopAudio();
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
                isAnimationRunning = false;
                flipCard();
                // после выделения текста который мы знаем
                number++;
                selectedCount = 0;
                emptyStringCount=0;
                System.out.println("СНИЗУ ПРИ НАЖАТИИ НА НЕКСТ БТН");
                System.out.println(textKnowGet);
                study.clickReady(textKnowGet*0.01);
                emptyStringCount=0;
                plan.newSizeQuestionOnFuture();
                MainActivity.myDBManager.updateQuestionsToSubject(plan);
                MainActivity.myDBManager.updateSubTodayLearned(plan);
                Users.getUser().currentUpdateDbTime();
                if(study.isEndOfPlan()){
                    stopAudio();
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

    private void flipCard() {
        // Проверяем, не запущена ли уже анимация
        if (isAnimationRunning) {
            return;
        }
        stopAudio();
        binding.cardView.setCameraDistance(27000);

        // Создаем анимацию переворота
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.cardView, "rotationY", 0, 90);animator.setDuration(200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                // Устанавливаем флаг, что анимация запущена
                isAnimationRunning = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Когда анимация закончилась, меняем видимость текстовых полей
                CountAnswerText = CountSpen();
                if(isText1Visible) { // Если нажали на карточку, то на обратной стороне..
                    if(CountAnswerText <5) { //5 предложений
                        binding.textQuestion.setVisibility(View.GONE);
                        binding.scrollText2.setVisibility(View.VISIBLE);
                        binding.yesAnswerButton.setVisibility(View.VISIBLE);
                        binding.noAnswerButton.setVisibility(View.VISIBLE);
                        binding.voiceBtn.setVisibility(View.VISIBLE);
                        binding.textViewInfo.setText("Проверив себя, нажмите на одну из кнопок 'Знаю' или 'Не знаю'");
                    } else {
                        binding.textQuestion.setVisibility(View.GONE);
                        binding.scrollText2.setVisibility(View.VISIBLE);
                        binding.yesAnswerButton.setVisibility(View.GONE);
                        binding.noAnswerButton.setVisibility(View.GONE);
                        binding.voiceBtn.setVisibility(View.VISIBLE);
                        binding.nextBtn.setVisibility(View.VISIBLE);
                        binding.textViewInfo.setText("Нажмите на те предложения, на котрые вы вероятно ответили, перед тем, как перевернуть карту");
                        TextSpen();
                    }
                }
                else {
                    isAnimationRunning = false;
                    binding.textViewInfo.setText("Пожалуйста, ознакомьтесь с вопросом темы, подумайте, как на него необходимо ответить, нажмите на карту, чтобы посмотреть ответ");
                    binding.yesAnswerButton.setVisibility(View.INVISIBLE);
                    binding.noAnswerButton.setVisibility(View.INVISIBLE);
                    binding.nextBtn.setVisibility(View.GONE);
                    binding.voiceBtn.setVisibility(View.INVISIBLE);
                    binding.scrollText2.setVisibility(View.GONE);
                    binding.textQuestion.setVisibility(View.VISIBLE);
                    binding.textNumber.setText(plan.getTodayLearned() + "/ "+plan.getFuturePlan().get(0).getSizeOfQuetion());
                    binding.textQuestion.setText(nowQuest.getQuestion().toString());
                    binding.textAnswer.setText(nowQuest.getAnswer().toString());
                }

// Меняем текущее состояние карточки
                isText1Visible = !isText1Visible;
// Запускаем анимацию обратного переворота
                ObjectAnimator reverseAnimator = ObjectAnimator.ofFloat(binding.cardView, "rotationY", 270, 360);
                reverseAnimator.setDuration(200);
                reverseAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
// Когда анимация закончилась, сбрасываем флаг
//                        isAnimationRunning = false;
                    }
                });
                reverseAnimator.start();
            }
        });
        animator.start();
    }

    private void setQuestions() {
        if (!study.isEndOfPlan()){ //проверка на конец тестирования
            if(!isText1Visible) flipCard();
        } else {
            stopAudio();
            Toast.makeText(getContext(), "Вопросы по данному предмету закончились", Toast.LENGTH_SHORT).show();
            @NonNull NavDirections action = AnswerQuestionFragmentDirections.actionAnswerQuestionFragmentToCalendarFragment();
            Navigation.findNavController(getView()).navigate(action);
        }
    }

    private void TextSpen() {
        String text = nowQuest.getAnswer();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(text);
        int start = iterator.first();
        int end = iterator.next();
        spannable = new SpannableString(text);
        totalCount = 0;
        while (end != BreakIterator.DONE) {
            final String sentence = text.substring(start, end);
            if (!sentence.trim().isEmpty()) {
                ClickableSpan clickableSpan = new SentenceSpan();
                spannable.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                totalCount++;
            }
            start = end;
            end = iterator.next();
        }
        binding.textAnswer.setText(spannable);
        binding.textAnswer.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private class SentenceSpan extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            int start = spannable.getSpanStart(this);
            int end = spannable.getSpanEnd(this);
            BackgroundColorSpan[] spans = spannable.getSpans(start, end, BackgroundColorSpan.class);
            if (spans.length > 0) {
                spannable.removeSpan(spans[0]);
                selectedCount--;
            } else {
                spannable.setSpan(new BackgroundColorSpan(Color.YELLOW), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                selectedCount++;
            }
            textKnowGet=(double) selectedCount / (totalCount-emptyStringCount) * 100;
            binding.textAnswer.setText(spannable);
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }
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
                .header("Authorization", "API-key AQVN1rVBvaVf2jdxdbldi3FMBQrulX_WmNlDKMl6")
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
        stopAudio();
        try {
            String url = "data:audio/mp3;base64," + base64Audio;
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }

    public void stopAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
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