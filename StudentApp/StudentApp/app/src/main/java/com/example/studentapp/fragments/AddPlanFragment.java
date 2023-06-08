package com.example.studentapp.fragments;

import static java.time.temporal.ChronoUnit.DAYS;

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
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.al.Question;
import com.example.studentapp.databinding.FragmentAddPlanBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Users;
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


public class AddPlanFragment extends Fragment {

    ApiInterface apiInterface;
    FragmentAddPlanBinding binding;
    final Calendar myCalendar = Calendar.getInstance();
    int idSub;
    QuestionAddRecycler.OnItemClickListener itemClick;
    PlanToSub planToSub;
    LocalDate localDate;

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

                        // Удаление шума изображения с помощью медианного фильтра
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        planToSub = new PlanToSub();
 /*       Paper.book("questions").destroy();
        Paper.book("plan").destroy();*/

        itemClick = new QuestionAddRecycler.OnItemClickListener() {

            @Override
            public void onClickQuestion(Question ques, int position) {
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
                tvAnswer.setText(ques.getAnswer());
                tvQ.setText(ques.getQuestion());

                addBtn.setText("Сохранить");
                clsBtn.setText("Удалить");

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!tvQ.getText().toString().trim().isEmpty()
                                || !tvAnswer.getText().toString().trim().isEmpty()) {
                            planToSub.getSub().getQuestion().get(position).setQuestion(tvQ.getText().toString());
                            planToSub.getSub().getQuestion().get(position).setAnswer(tvAnswer.getText().toString());
                            setQuestions(planToSub.getSub().getQuestion());
                            dialog.dismiss();
                        }  Toast.makeText(getContext(),
                                "Заполните все поля", Toast.LENGTH_SHORT).show();
                    }
                });

                clsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        planToSub.getSub().getQuestion().remove(position);
                        setQuestions(planToSub.getSub().getQuestion());
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        };
        setQuestions(planToSub.getSub().getQuestion());

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
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        binding.AddPlan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.Text1.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(),
                            "Добавьте имя предмета", Toast.LENGTH_SHORT).show();
                } else {
                    List<PlanToSub> list = MainActivity.myDBManager.getFromDB()
                            .stream()
                            .filter(c -> c.getSub().getNameOfSubme().equals(binding.Text1.getText().toString()))
                            .collect(Collectors.toList());
                    if (!list.isEmpty()) {
                        Toast.makeText(getContext(),
                                "Предмет с таким именем уже существует", Toast.LENGTH_SHORT).show();
                    } else if (planToSub.getSub().getQuestion().isEmpty()) {
                        Toast.makeText(getContext(),
                                "Добавьте вопросы", Toast.LENGTH_SHORT).show();
                    } else if (localDate == null) {
                        Toast.makeText(getContext(),
                                "Добавьте дату экзамена", Toast.LENGTH_SHORT).show();
                    } else {
                        int id = MainActivity.myDBManager.getFromDB()
                                .stream()
                                .mapToInt(PlanToSub::getId).min().orElse(0) - 1;
                        if (id > -1) id = -1;
                        planToSub.setId(id);
                        planToSub.setDateOfExams(localDate);
                        planToSub.getSub().setNameOfSub(binding.Text1.getText().toString());
                        setNewPlan();

                        MainActivity.myDBManager.setFromDB(planToSub);
                        Users.getUser().currentUpdateDbTime();
                        NavDirections action =
                                AddPlanFragmentDirections.actionAddPlanFragmentToSettingPlanFragment2(planToSub.getSub().getNameOfSubme());
                        Navigation.findNavController(getView()).navigate(action);
                    }
                }
            }
        });

        binding.addque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(view);
            }
        });
    }

    private void setNewPlan(){
        LocalDate date = LocalDate.parse(planToSub.dateToString().split("T")[0]);
        long days = DAYS.between(LocalDate.now(), date);
        for(int i=0; i<days; i++){
            planToSub.plusDayToPlan(LocalDate.now().plusDays(i));
        }
    }

    private void updateLabel(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
        localDate =  myCalendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        binding.editTextDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void setQuestions(ArrayList<Question> questions){
        binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listVop.setHasFixedSize(true);
        binding.listVop.setAdapter(new QuestionAddRecycler(getContext(), questions, itemClick));
    }

    public void showAlertDialogButtonClicked(View view) {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_question, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();

        tvAnswer = customLayout.findViewById(R.id.tv_answer);
        EditText tvQ = customLayout.findViewById(R.id.tv_question);
        Button addBtn = customLayout.findViewById(R.id.add_question);
        ImageButton addBtnCam = customLayout.findViewById(R.id.camera_btn);

        AppCompatButton clsBtn = customLayout.findViewById(R.id.cancel_window);
        ImageButton foto;
        foto= customLayout.findViewById(R.id.camera_btn);
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GALLERY);
            }
        });

        addBtnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ДИАНА. При нажатии на кнопку, вызываем эту функцию с Tesseract
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
                    planToSub.getSub().addQuestion(
                            new Question(tvQ.getText().toString(),tvAnswer.getText().toString()));
                    setQuestions(planToSub.getSub().getQuestion());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(MainActivity.myDBManager.tr_From_DB_Add_Sub()==0) {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());

        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.dialog_info,
                        null);
        builder.setView(customLayout);

        AlertDialog dialog
                = builder.create();
        Button out = customLayout.findViewById(R.id.okay);
        TextView text = customLayout.findViewById(R.id.text_info);

        text.setText("По мимо стандартного занесения информации для вопроса - можно загрузить фото с текстом и мы сделаем это за тебя\n\nПримечание: фото должно быть скрином либо изображением хорошего качества.");

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
            MainActivity.myDBManager.update_TRAINING(1, 3);
        }
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_plan, container, false);
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        Paper.init(getContext());
        return binding.getRoot();
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