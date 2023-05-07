package com.example.studentapp.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Spannable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.al.Question;
import com.example.studentapp.al.Study;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.fragments.StatisticFragment;

import java.util.ArrayList;

public class SubjectsCardsRecycler extends RecyclerView.Adapter<SubjectsCardsRecycler.ViewHolder> {
    public interface OnItemClickListener {
        void onClickQuestions(Question ques, int position);
    }
    private Context context;
    private ArrayList<Question> questions;
    private OnItemClickListener onItemClickListener;

    private boolean[] isText1Visible;
    private boolean[] isAnimationRunning;

    public SubjectsCardsRecycler(Context context, ArrayList<Question> questions, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.questions = questions;
        this.onItemClickListener = onItemClickListener;
        this.isText1Visible = new boolean[questions.size()];
        this.isAnimationRunning = new boolean[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            isText1Visible[i] = true;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_sub_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.question.setText(questions.get(position).getQuestion());
        holder.answer.setText(questions.get(position).getAnswer());

        holder.btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(context);

                final View customLayout
                        = LayoutInflater.from(context)
                        .inflate(
                                R.layout.dialog_expand,
                                null);
                builder.setView(customLayout);

                AlertDialog dialog
                        = builder.create();
                Button out = customLayout.findViewById(R.id.okay);
                TextView text = customLayout.findViewById(R.id.text_info);

                text.setText(questions.get(position).getAnswer());
                out.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.setView(customLayout);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        String text_que = holder.question.getText().toString();
        String text_ans = holder.answer.getText().toString();
        int count = text_que.length();

        if (count > 205 || text_que.split("\n").length >= 7) {
            holder.question.setTextSize(15);
        }
        count = text_ans.length();
        if (count > 205 || text_ans.split("\n").length >= 7) {
            holder.answer.setTextSize(13);
        }

        if (questions.size() > 1) {
            holder.lauout.setLayoutParams(
                    new RecyclerView.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT-50,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );

            if (position == 0) {
                holder.itemView.setPadding(100, 20, 2, 20); // Применение отступов
            }
            // Последний элемент
            else if (position == questions.size() - 1) {
                holder.itemView.setPadding(2, 20, 100, 20); // Применение отступов
            } else {
                holder.itemView.setPadding(2, 20, 2, 20); // Применение отступов
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Проверяем, не запущена ли уже анимация для данной карточки
                if (isAnimationRunning[position]) {
                    return;
                }

                holder.cardView.setCameraDistance(27000);

                // Создаем анимацию переворота
                ObjectAnimator animator = ObjectAnimator.ofFloat(holder.cardView, "rotationX", 0, 90);
                animator.setDuration(200);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        // Устанавливаем флаг, что анимация запущена
                        isAnimationRunning[position] = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        if (isText1Visible[position]) {
                            holder.question.setVisibility(View.GONE);
                            holder.answer.setVisibility(View.VISIBLE);
                            holder.btnExpand.setVisibility(View.VISIBLE);
                        }
                        else {
                            isAnimationRunning[position] = false;
                            holder.btnExpand.setVisibility(View.GONE);
                            holder.answer.setVisibility(View.GONE);
                            holder.question.setVisibility(View.VISIBLE);
                            holder.question.setText(questions.get(position).getQuestion());
                            holder.answer.setText(questions.get(position).getAnswer());
                        }

// Меняем текущее состояние карточки
                        isText1Visible[position] = !isText1Visible[position];
// Запускаем анимацию обратного переворота
                        ObjectAnimator reverseAnimator = ObjectAnimator.ofFloat(holder.cardView, "rotationX", 270, 360);
                        reverseAnimator.setDuration(200);
                        reverseAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                        isAnimationRunning[position] = false;
                            }
                        });
                        reverseAnimator.start();
                    }
                });
                animator.start();
            }
        });
    }



    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView question;
        TextView answer;
        CardView cardView;
        ScrollView scrollText2;
        LinearLayout lauout;

        ImageButton btnExpand;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.text_question);
            answer = itemView.findViewById(R.id.text_answer);
            cardView = itemView.findViewById(R.id.card_view);
            scrollText2 = itemView.findViewById(R.id.scroll_text2);
            lauout = itemView.findViewById(R.id.lau);
            btnExpand = itemView.findViewById(R.id.expand_btn);
        }
    }
}
