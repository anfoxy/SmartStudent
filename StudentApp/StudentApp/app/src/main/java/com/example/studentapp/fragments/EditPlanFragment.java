package com.example.studentapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.TextView;
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
import com.example.studentapp.db_local.MyDBManager;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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

    private Bitmap selectedImage;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getActivity()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    if(selectedImage!=null){
                        Mat imgMat = new Mat();
                        Utils.bitmapToMat(selectedImage, imgMat);

                        // Преобразование в серое изображение
                        Mat grayMat = new Mat();
                        Imgproc.cvtColor(imgMat, grayMat, Imgproc.COLOR_BGR2GRAY);

//                        // Удаление шума изображения с помощью медианного фильтра
                        Imgproc.medianBlur(grayMat, grayMat, 3);

                        // Улучшение четкости изображения с помощью фильтра "unsharp masking"
                        Mat blurred = new Mat();
                        Imgproc.GaussianBlur(grayMat, blurred, new Size(0, 0), 3);
                        Mat sharpened = new Mat();
                        Core.addWeighted(grayMat, 1.5, blurred, -0.5, 0, sharpened);

                        // Применение бинаризации изображения
                        Mat binaryMat = new Mat();
                        Imgproc.threshold(sharpened, binaryMat, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

                        // Копирование изображения из Mat в Bitmap
                        selectedImage = Bitmap.createBitmap(selectedImage.getWidth(), selectedImage.getHeight(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(binaryMat, selectedImage);
                    }
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        subject = MainActivity.myDBManager.getFromDB().stream()
                .filter(c -> c.getSub().getNameOfSubme().equals(args.getId())).collect(Collectors.toList()).get(0);
        localDate = subject.getDateOfExams();

        if (localDate.isBefore(LocalDate.now())) binding.editPlan.setVisibility(View.GONE);

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
                LocalDate minDay =  LocalDate.now().plusDays(1);
                minDate.set(Calendar.YEAR, LocalDate.now().getYear());
                minDate.set(Calendar.MONTH, LocalDate.now().getMonth().getValue() - 1);
                minDate.set(Calendar.DAY_OF_MONTH, minDay.getDayOfMonth());
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

    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, getActivity(), mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
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
                MainActivity.myDBManager.updateSubTodayLearned(subject);
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
                selectedImage = BitmapFactory.decodeStream(imageStream);
                TextView textView = tvAnswer;
                com.example.myapplication2.TesseractOCR tesseractOCR = new com.example.myapplication2.TesseractOCR(getActivity(), textView);
                tesseractOCR.execute(selectedImage);
                Log.d(TAG, "Text recognition success ");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}