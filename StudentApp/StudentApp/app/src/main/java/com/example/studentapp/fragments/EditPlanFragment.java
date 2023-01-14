package com.example.studentapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.adapters.QuestionAddRecycler;
import com.example.studentapp.al.PlanToDay;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.al.Question;
import com.example.studentapp.databinding.FragmentEditPlanBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditPlanFragment extends Fragment {

    private FragmentEditPlanBinding binding;
    private EditPlanFragmentArgs args;
    private QuestionAddRecycler.OnItemClickListener itemClick;
    private final Calendar myCalendar = Calendar.getInstance();
    private PlanToSub subject;
    private LocalDate localDate;
    private ApiInterface apiInterface;

    private static final String TAG = "AddPlanFragment";
    private static final int REQUEST_GALLERY = 1;

    TessBaseAPI tessBaseAPI;
    EditText tvAnswer;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        subject = MainActivity.myDBManager.getFromDB().stream()
                .filter(c -> c.getSub().getNameOfSubme().equals(args.getId())).collect(Collectors.toList()).get(0);
        localDate = subject.getDateOfExams();

        if (localDate.isBefore(LocalDate.now())) binding.editPlan.setVisibility(View.INVISIBLE);

        itemClick = new QuestionAddRecycler.OnItemClickListener() {
            @Override
            public void onClickQuestion(Question ques, int position) {
                showItemDialog(view, ques, position);
            }
        };

        setQuestions();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };

        binding.editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar minDate = Calendar.getInstance();
                minDate.set(Calendar.YEAR, LocalDate.now().getYear());
                minDate.set(Calendar.MONTH, LocalDate.now().getMonth().getValue() - 1);
                minDate.set(Calendar.DAY_OF_MONTH, LocalDate.now().getDayOfMonth());
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        binding.b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(view);
            }
        });

        binding.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder
                        = new AlertDialog.Builder(getContext());

                // set the custom layout
                final View customLayout
                        = getLayoutInflater()
                        .inflate(
                                R.layout.dialog_delete_sub,
                                null);
                builder.setView(customLayout);

                AlertDialog dialog
                        = builder.create();


                AppCompatButton edit = customLayout.findViewById(R.id.cancel_window);
                AppCompatButton clsBtn = customLayout.findViewById(R.id.delete_sub);


                clsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.myDBManager.delete_SUB(subject.getSub().getNameOfSubme());
                        Users.getUser().currentUpdateDbTime();
                        NavDirections action = EditPlanFragmentDirections.actionEditPlanFragmentToListFragment();
                        Navigation.findNavController(getView()).navigate(action);
                        dialog.dismiss();
                    }
                });

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        binding.editPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subject.getSub().getQuestion().isEmpty()) {
                    Toast.makeText(getContext(), "Добавьте вопросы", Toast.LENGTH_SHORT).show();
                } else if (binding.Text1.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Добавьте имя предмета", Toast.LENGTH_SHORT).show();
                } else {
                    save();
                    NavDirections action = EditPlanFragmentDirections.actionEditPlanFragmentToEditItemPlanFragment2(args.getId());
                    Navigation.findNavController(getView()).navigate(action);
                }
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!subject.getSub().getNameOfSubme().equals(binding.Text1.getText().toString())) {
                    List<PlanToSub> list = MainActivity.myDBManager.getFromDB()
                            .stream()
                            .filter(c -> c.getSub().getNameOfSubme().equals(binding.Text1.getText().toString()))
                            .collect(Collectors.toList());
                    if (!list.isEmpty()){
                        Toast.makeText(getContext(),
                            "Предмет с таким именем уже существует", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (subject.getSub().getQuestion().isEmpty()) {
                    Toast.makeText(getContext(), "Добавьте вопросы", Toast.LENGTH_SHORT).show();
                } else if (binding.Text1.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Добавьте имя предмета", Toast.LENGTH_SHORT).show();
                } else {
                    save();
                    Users.getUser().currentUpdateDbTime();
                    NavDirections action = EditPlanFragmentDirections.actionEditPlanFragmentToListFragment();
                    Navigation.findNavController(getView()).navigate(action);
                }
            }
        });
    }

    private void save() {
        checkPlan();
        subject.setDateOfExams(localDate);
        MainActivity.myDBManager.updateNameSubAndDateExams(subject, binding.Text1.getText().toString());
        subject.getSub().setNameOfSub(binding.Text1.getText().toString());
        MainActivity.myDBManager.updateQuestionsToSubject(subject);
        MainActivity.myDBManager.updatePlan(subject);
    }

    private void checkPlan(){
        for (PlanToDay plan:subject.getFuturePlan()) {
            if(plan.getDate().isAfter(localDate)||plan.getDate().isEqual(localDate)) subject.minusDayToPlan(plan.getDate());
        }
    }
    private void updateLabel(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
        localDate =  myCalendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        binding.editTextDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void setQuestions(){
        binding.Text1.setText(subject.getSub().getNameOfSubme());
        binding.editTextDate.setText(subject.dateToString().split("T")[0]);
        binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listVop.setHasFixedSize(true);
        binding.listVop.setAdapter(new QuestionAddRecycler(getContext(), subject.getSub().getQuestion(), itemClick));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_plan, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = EditPlanFragmentArgs.fromBundle(getArguments());
        return binding.getRoot();
    }

    private void showItemDialog(View view, Question questions, int pos) {
        // Create an alert builder
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());

        // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.dialog_add_question,
                        null);
        builder.setView(customLayout);



        AlertDialog dialog
                = builder.create();

        tvAnswer = customLayout.findViewById(R.id.tv_answer);
        EditText tvQ = customLayout.findViewById(R.id.tv_question);
        Button addBtn = customLayout.findViewById(R.id.add_question);
        AppCompatButton clsBtn = customLayout.findViewById(R.id.cancel_window);

        tvAnswer.setText(questions.getAnswer());
        tvQ.setText(questions.getQuestion());

        addBtn.setText("Сохранить");
        clsBtn.setText("Удалить");
        ImageButton foto;
        foto= customLayout.findViewById(R.id.camera_btn);
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GALLERY);
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvQ.getText().toString().trim().isEmpty()
                        || tvAnswer.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                }else {
                    subject.changeQuestion(pos,tvQ.getText().toString(),tvAnswer.getText().toString());
                    setQuestions();
                    dialog.dismiss();


                }
            }
        });

        clsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subject.delQuestion(pos);
                setQuestions();
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    public void showAlertDialogButtonClicked(View view)
    {

        // Create an alert builder
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());

        // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.dialog_add_question,
                        null);
        builder.setView(customLayout);



        AlertDialog dialog
                = builder.create();

        tvAnswer = customLayout.findViewById(R.id.tv_answer);
        EditText tvQ = customLayout.findViewById(R.id.tv_question);
        Button addBtn = customLayout.findViewById(R.id.add_question);
        AppCompatButton clsBtn = customLayout.findViewById(R.id.cancel_window);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvQ.getText().toString().trim().isEmpty()
                        || tvAnswer.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                }else {
                    subject.setNewQuestion(new Question(tvQ.getText().toString(),tvAnswer.getText().toString()));
                    setQuestions();
                    dialog.dismiss();

                }
            }
        });

        clsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                tessBaseAPI = new TessBaseAPI();
                String datapath = getContext().getFilesDir() + "/tesseract/";
                File dir = new File(datapath + "tessdata/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String lang = "rus";
                String lang1 = "eng";
                File file = new File(datapath + "tessdata/" + lang + lang1 + ".traineddata");
                if (!file.exists()) {
                    try {
                        InputStream in = getContext().getAssets().open("tessdata/" + lang + ".traineddata");
                        OutputStream out = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                }
                tessBaseAPI.init(datapath, lang+lang1);
                tessBaseAPI.setImage(selectedImage);
                String text = tessBaseAPI.getUTF8Text();
                tvAnswer.setText(text);
                tessBaseAPI.end();
                Log.d(TAG, "Text recognition success: " + text);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}