package com.gayatri.foreverfitness;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NavigationAdapter extends PagerAdapter implements SensorEventListener {
    @Override
    public void onSensorChanged(SensorEvent event) {
        steps++;
        sqlLiteManager.saveSteps(steps);

        try{
            createPieChart();
            txtStepsDisplay.setText(steps + " ");

        }catch (Throwable t) {
            Throwable e = t;
            String james = "1";
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface MyCustomObjectListener {
        // need to pass relevant arguments related to the event triggered
        public void onObjectReady(String title);
    }

    public void setCustomObjectListener(MyCustomObjectListener myCustomObjectListener) {
        this.listener = myCustomObjectListener;
    }

    public String[] title = {
            "0oo",
            "o0o",
            "oo0",
            "o00"
    };
    Context context;
    LayoutInflater inflater;
    private String AccountName;
    private MyCustomObjectListener listener;
    private int position;
    View view;

    //Home Stat Page
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int steps = 0;
    private LineChart lineChartDiagram;

    private TextView txtWeNeedMoreDays, txtStepsDisplay;
    List<PieEntry> weightPieList;
    PieChart weightPieLimit,stepPie;

    //Dashboard Page
    private SeekBar weightBar;
    private TextView displayWeight, txtViewBMI;
    private Button btnSubmitWeight;
    private TextView txtCurrentDateSelect;
    private DatePickerDialog.OnDateSetListener onCurrentDateSetListener;
    private String currentDate;
    private ImageView imageView;

    //Camera page
    private Button btnTakePhoto;
    private ImageView imgCurrentPhoto;

    //Settings Page
    private Button btnDeleteAccount;
    private TextView name, textViewSetWeightGoal, txtChangeToImperialHeader, txtMeasurementHeader, txtSelectDate, txtGoalDate, textViewWeight, txtViewHeight, txtGender;
    private Switch switchImperial, switchGender;
    private SeekBar seekBarWeightGoal, seekBarStepGoal;
    private boolean isImperial;
    private Button btnViewHistory;

    private EditText editTextName, editTextWeight, editTextHeight;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private DatePickerDialog.OnDateSetListener onGoalDateSetListener;
    private String birthday, goalDate;
    private boolean isMale = true;
    private TextView textViewSetStepGoal;


    //Database
    private SqlLiteManager sqlLiteManager;
    public NavigationAdapter(Context context, String name, boolean isImperial)
    {
        this.context = context;
        this.AccountName = name;
        this.listener = null;
        sqlLiteManager = new SqlLiteManager(context);
        sqlLiteManager.getUserId(this.AccountName);
        this.isImperial = sqlLiteManager.getUserImperial();
        steps = sqlLiteManager.loadSteps();
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view ==(LinearLayout)object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.position = position;
        if (position == 0) {
            //o position page
            view = inflater.inflate(R.layout.fragment_home, container, false);
            LinearLayout layoutslide = view.findViewById(R.id.slidelinearlayout);
            LinearLayout pielayout = view.findViewById(R.id.PieLayout);
            stepPie = view.findViewById(R.id.StepPie);
            txtWeNeedMoreDays = view.findViewById(R.id.TxtWeNeedMoreDays);
            lineChartDiagram = view.findViewById(R.id.LineChartDiagram);

            //When called, it generated the graph to be displayed to the user
            createPieChart();//Populates the Graph

            txtStepsDisplay = view.findViewById(R.id.TxtStepsDisplay);
            txtStepsDisplay.setText(steps + " ");

            createLineGraph();
        }else if (position == 1) {
            //Dashboard
            view = inflater.inflate(R.layout.fragment_dashboard, container, false);
            weightBar = (SeekBar) view.findViewById(R.id.seekBar);
            displayWeight = (TextView) view.findViewById(R.id.txtCalories);
            weightPieLimit = view.findViewById(R.id.WeightLimitPie);
            btnSubmitWeight = view.findViewById(R.id.BtnSubmitWight);
            txtViewBMI = view.findViewById(R.id.TxtViewBMI);
            txtCurrentDateSelect = view.findViewById(R.id.TxtCurrentDateSelect);
            imageView = view.findViewById(R.id.ImgDashboardPhoto);

            LoadCurrentWeightAndDate();
            getPhotoForDashboard();

            txtCurrentDateSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(
                            context,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            onCurrentDateSetListener,
                            year,month,day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });

            onCurrentDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month = month + 1;
                    currentDate = (day < 10 ? "0" : "") + day +"-"+ (month < 10 ? "0" : "") + month +"-"+year;
                    txtCurrentDateSelect.setTextColor(Color.parseColor("#000000"));
                    txtCurrentDateSelect.setText(currentDate);
                }
            };

            weightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    boolean isImperial = true;//read later
                    if (!sqlLiteManager.getUserImperial()){
                        displayWeight.setText((double)progress/10 + " Kg");
                    }else{
                        displayWeight.setText((int)((progress/10)*2.205) + " Lbs");
                    }
                    createPieChartWeight();
                    calculateBMI(progress/10);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            createPieChartWeight();
            calculateBMI(0);
            btnSubmitWeight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentDate!=null && imageView.getDrawable() != null) {
                        double weightSet = weightBar.getProgress() / 10;
                        Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                        sqlLiteManager.saveWeightWithDateAndImage(weightSet, currentDate, image);
                        createPieChartWeight();
                        calculateBMI(weightSet);
                    }
                    else
                    {
                        if(currentDate==null) {
                            txtCurrentDateSelect.setText("Tap to select");
                            txtCurrentDateSelect.setTextColor(Color.parseColor("#FF0000"));
                        }

                        if(imageView.getDrawable() == null)
                        {
                            Toast.makeText(v.getContext(), "Please capture picture from Camera First!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });

        }else if (position == 2) {
            //Camera Page
            //Start Camera Page
            view = inflater.inflate(R.layout.activity_takephoto, container, false);
            btnTakePhoto = view.findViewById(R.id.BtnTakePhoto);
            btnViewHistory = view.findViewById(R.id.BtnViewHistory);
            imgCurrentPhoto = view.findViewById(R.id.ImgCurrentPhoto);
            btnTakePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cameraPage = new Intent(view.getContext(), Camera.class);
                    cameraPage.putExtra("Account",AccountName);
                    view.getContext().startActivity(cameraPage);
                    getTodayPhoto();
                }
            });

            btnViewHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent history = new Intent(view.getContext(), HistoryActivity.class);
                    history.putExtra("Account",AccountName);
                    history.putExtra("isImperial",sqlLiteManager.getUserImperial());
                    view.getContext().startActivity(history);
                }
            });

            getTodayPhoto();

        }else if (position == 3) {
            //3 position page Settings
            view = inflater.inflate(R.layout.fragment_settings, container, false);
            btnDeleteAccount = view.findViewById(R.id.BtnDeleteAccount);
            name = view.findViewById(R.id.Name);
            switchImperial = view.findViewById(R.id.SwitchImperial);

            textViewSetWeightGoal = view.findViewById(R.id.TextViewSetWeightGoal);
            txtChangeToImperialHeader = view.findViewById(R.id.TxtChangeToImperialHeader);
            txtMeasurementHeader = view.findViewById(R.id.TxtMeasurementHeader);
            seekBarWeightGoal = (SeekBar) view.findViewById(R.id.SeekBarWeightGoal);
            seekBarWeightGoal.setMax(8);
            //additional setting entities
            editTextName = (EditText) view.findViewById(R.id.EditTxtName);
            editTextWeight = (EditText) view.findViewById(R.id.EditTextWeight);
            editTextHeight = (EditText) view.findViewById(R.id.EditTextHeight);
            txtGender = (TextView) view.findViewById(R.id.txtGender);
            switchGender = (Switch) view.findViewById(R.id.SwitchGender);
            txtSelectDate = (TextView) view.findViewById(R.id.TxtSelectDate);
            txtGoalDate = (TextView) view.findViewById(R.id.TxtGoalDateSelect);
            textViewWeight = (TextView) view.findViewById(R.id.TextViewWeight);
            txtViewHeight = (TextView) view.findViewById(R.id.TextViewHeight);
            textViewSetStepGoal = view.findViewById(R.id.TextViewSetStepGoal);
            seekBarStepGoal = (SeekBar) view.findViewById(R.id.SeekBarStepGoal);
            seekBarStepGoal.setMax(10);

            seekBarStepGoal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViewSetStepGoal.setText("Current Step Goal: " + (progress+5)*1000 + " steps");

                    sqlLiteManager.setUserStepGoal((progress+5)*1000);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            editTextHeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString().length()>0)
                        sqlLiteManager.setHeight(Double.parseDouble(s.toString()));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            editTextWeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString().length()>0)
                        sqlLiteManager.setRegisteredWeight(Double.parseDouble(s.toString()));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            editTextName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String newName = s.toString();
                    sqlLiteManager.setName(newName);
                    name.setText(newName);
                    AccountName = newName;
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            txtGoalDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(
                            context,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            onGoalDateSetListener,
                            year, month, day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });

            onGoalDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month = month + 1;
                    goalDate = day + "-" + month + "-" + year;
                    txtGoalDate.setText(goalDate);
                    sqlLiteManager.setUserDateGoal(goalDate);
                }
            };

            txtSelectDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(
                            context,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            onDateSetListener,
                            year, month, day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });

            onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month = month + 1;
                    birthday = day + "-" + month + "-" + year;
                    txtSelectDate.setText(birthday);
                    sqlLiteManager.setBirthday(birthday);
                }
            };

            switchGender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleGender();
                }
            });

            txtGender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleGender();
                }
            });

            final DecimalFormat df2 = new DecimalFormat("#.##");
            txtChangeToImperialHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleMeasurementSystem(df2);
                }
            });

            switchImperial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleMeasurementSystem(df2);
                }
            });

            btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onObjectReady("btnDeleteAccount");
                }
            });


            seekBarWeightGoal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (sqlLiteManager.getUserImperial() == false) {
                        textViewSetWeightGoal.setText("Current Weight Goal: " + (progress + 4) * 10 + "Kg");
                    } else {
                        textViewSetWeightGoal.setText("Current Weight Goal: " + (int) (((progress + 4) * 10) * 2.205) + "Lbs");
                    }
                    sqlLiteManager.setUserWeightGoal((progress + 4) * 10);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });



            //Get User Details
            String name = sqlLiteManager.getName();
            this.name.setText(name);
            switchImperial.setChecked(isImperial);
            double userGoal = sqlLiteManager.getUserWeightGoal();
            if (isImperial) {
                textViewSetWeightGoal.setText("Current Weight Goal: " + userGoal + "Lbs");
                txtMeasurementHeader.setText("Measurement System - Imperial");
                txtChangeToImperialHeader.setText("Change to Metric");
                textViewWeight.setText("Weight: Lbs");
                txtViewHeight.setText("Height: Feet");
            }
            else {
                textViewSetWeightGoal.setText("Current Weight Goal: " + userGoal + "Kg");
                txtMeasurementHeader.setText("Measurement System - Metric");
                txtChangeToImperialHeader.setText("Change to Imperial");
                textViewWeight.setText("Weight: Kg");
                txtViewHeight.setText("Height: Meters");
            }
            seekBarWeightGoal.setProgress((int)(userGoal/10-4));
            int stepGoal = sqlLiteManager.getUserStepGoal();
            textViewSetStepGoal.setText("Current Step Goal: " + stepGoal + " steps");
            //set all info from db to controls
            editTextName.setText(name);
            editTextHeight.setText(Double.toString(sqlLiteManager.getHeight()));
            editTextWeight.setText(Double.toString(sqlLiteManager.getRegisteredWeight()));
            txtSelectDate.setText(sqlLiteManager.getBirthday());
            txtGoalDate.setText(sqlLiteManager.getUserDateGoal());
            boolean isMale = sqlLiteManager.getGender();
            if(isMale)
                txtGender.setText("Male");
            else
                txtGender.setText("Female");
            switchGender.setChecked(!isMale);
        }
        container.addView(view);
        return view;
    }

    private void LoadCurrentWeightAndDate() {
        //load Weight from database
        int loadWeight = sqlLiteManager.getWeight();//default value 70
        String date = sqlLiteManager.getDate();
        if(weightBar!=null)
            weightBar.setProgress(loadWeight * 10);
        if(txtCurrentDateSelect!=null) {
            if(date.isEmpty())
                txtCurrentDateSelect.setText("Tap to select");
            else
                txtCurrentDateSelect.setText(date);
        }

        if(displayWeight!=null) {
            if (sqlLiteManager.getUserImperial() == false) {
                displayWeight.setText((double) loadWeight + " Kg");
            } else {
                displayWeight.setText((int) ((loadWeight) * 2.205) + " Lbs");
            }
        }
    }

    private void ToggleGender() {
        isMale = sqlLiteManager.getGender();
        if (isMale == true) {
            isMale = false;
            sqlLiteManager.setGender(false);
            txtGender.setText("Female");
            switchGender.setChecked(true);
        } else {
            isMale = true;
            sqlLiteManager.setGender(true);
            txtGender.setText("Male");
            switchGender.setChecked(false);
        }
    }

    private void ToggleMeasurementSystem(DecimalFormat df2) {
        //isImperial = switchImperial.isChecked();
        //sqlLiteManager.setUserImperial(isImperial);
        if (isImperial == false){
            isImperial = true;
            sqlLiteManager.setUserImperial(true);
            switchImperial.setChecked(true);
            txtMeasurementHeader.setText("Measurement System - Imperial");
            txtChangeToImperialHeader.setText("Change to Metric");
            textViewWeight.setText("Weight: Lbs");
            txtViewHeight.setText("Height: Feet");
            if (!editTextWeight.getText().toString().isEmpty()) {
                editTextWeight.setText(Double.toString(Double.parseDouble(df2.format(Double.parseDouble(editTextWeight.getText().toString()) * 2.205))));
            }
            if (!editTextHeight.getText().toString().isEmpty()) {
                editTextHeight.setText(Double.toString(Double.parseDouble(df2.format(Double.parseDouble(editTextHeight.getText().toString()) * 3.28084))));
            }
            textViewSetWeightGoal.setText("Current Weight Goal: " + (int)(((seekBarWeightGoal.getProgress()+4)*10)*2.205) + "Lbs");

        }else{
            isImperial = false;
            sqlLiteManager.setUserImperial(false);
             switchImperial.setChecked(false);
            txtMeasurementHeader.setText("Measurement System - Metric");
            txtChangeToImperialHeader.setText("Change to Imperial");
            textViewWeight.setText("Weight: Kg");
            txtViewHeight.setText("Height: Meters");
            if (!editTextWeight.getText().toString().isEmpty()) {
                editTextWeight.setText(Double.toString(Double.parseDouble(df2.format(Double.parseDouble(editTextWeight.getText().toString()) / 2.205))));
            }
            if (!editTextHeight.getText().toString().isEmpty()) {
                editTextHeight.setText(Double.toString(Double.parseDouble(df2.format(Double.parseDouble(editTextHeight.getText().toString()) / 3.28084))));
            }
            textViewSetWeightGoal.setText("Current Weight Goal: " + (seekBarWeightGoal.getProgress()+4)*10 + "Kg");
        }
        //Load Dashboard with Correct measurement system
        LoadCurrentWeightAndDate();
    }

    private void calculateBMI(double weight){
        try{
            double bodyMassIndex = 0;
            double userWeight = 0;
            //weight will ve in kgs always
            if(weight==0)
                userWeight = sqlLiteManager.getWeight();//70 default
            else
                userWeight = weight;
            //height will be depends on imperial or metrics

            double userHeight = sqlLiteManager.getHeight();//70 default
            if(isImperial)
                userHeight = userHeight / 3.28084;


            bodyMassIndex = userWeight/(userHeight*userHeight);
            bodyMassIndex = (int)(bodyMassIndex*10); //used to get to 2 decimal places
            bodyMassIndex = bodyMassIndex/10;
            txtViewBMI.setText("BMI: "+bodyMassIndex);
        }catch (Exception e){
            txtViewBMI.setText("Please Enter your weight today first!");
        }
    }

    private void createPieChartWeight(){ //creates a graph to visually see the users progress in there goal

        weightPieLimit.setUsePercentValues(true);
        List<PieEntry> weightPieList = new ArrayList<>();
        double userWeight = sqlLiteManager.getWeight();//70 defaults
        int getWeight = (int)sqlLiteManager.getUserWeightGoal();;

        if((int)userWeight< getWeight){
            if (sqlLiteManager.getUserImperial() == false) {
                weightPieList.add(new PieEntry((int) userWeight, (int) userWeight + " KG"));
                weightPieList.add(new PieEntry(getWeight - (int) userWeight, getWeight - (int) userWeight + "KG"));
            }else{

                weightPieList.add(new PieEntry((int) userWeight, (int) (userWeight * 2.205) + " Lbs"));
                weightPieList.add(new PieEntry(getWeight - (int) userWeight, (int) (getWeight*2.205) - (int) (userWeight * 2.205) + " Lbs"));
            }

        }else{

            if (sqlLiteManager.getUserImperial() == false) {
                weightPieList.add(new PieEntry((int) userWeight,(int)userWeight + " KG"));
                weightPieList.add(new PieEntry(((getWeight-(int)userWeight)*-1),(int)((getWeight-userWeight)*-1)+ " KG"));

            }else{

                weightPieList.add(new PieEntry((int) userWeight,(int)(userWeight*2.205) + " Lbs"));
                weightPieList.add(new PieEntry(((getWeight-(int)userWeight)*-1),(int)(((getWeight-userWeight)*-1)*2.205)+ " Lbs"));
            }

        }
        PieDataSet weightPieDataSet;
        if((int)userWeight< getWeight){
            weightPieDataSet = new PieDataSet(weightPieList,"Weight to Gain");
        }else{
            weightPieDataSet = new PieDataSet(weightPieList,"Weight to Lose");
        }


        weightPieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        weightPieDataSet.setDrawValues(false);
        weightPieLimit.setHoleRadius(30f);
        weightPieLimit.setTransparentCircleRadius(25f);
        weightPieLimit.animateXY(1400,1400);
        PieData caloriePieData = new PieData(weightPieDataSet);
        weightPieLimit.setData(caloriePieData);
        weightPieLimit.setNoDataText("");
        //Used to set the description for the Pie chart
        Description description = new Description();
        description.setText("");
        weightPieLimit.setDescription(description);

    }

    private void createPieChart(){

        stepPie.setUsePercentValues(true);
        List<PieEntry> stepPieList = new ArrayList<>();
        double[] setGoal = sqlLiteManager.getUserGoal();
        int toGo = (int)(setGoal[0]);
        String pieData = String.valueOf(steps);
        String toGoSteps = String.valueOf(toGo-steps);
        stepPieList.add(new PieEntry(steps,pieData)); //What the user has done
        stepPieList.add(new PieEntry(toGo-steps,toGoSteps)); //What the user still needs to do

        PieDataSet stepPieDataSet = new PieDataSet(stepPieList,"Steps To Reach");
        stepPieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        stepPieDataSet.setDrawValues(false);
        stepPie.setHoleRadius(30f);
        stepPie.setTransparentCircleRadius(25f);

        PieData stepPieData = new PieData(stepPieDataSet);
        stepPie.setData(stepPieData);
        stepPie.setNoDataText("");
        //Used to set the discription for the Pie chart
        Description description = new Description();
        description.setText("");
        stepPie.setDescription(description);
        stepPie.invalidate();
    }

    private void createLineGraph(){

        ArrayList<Entry>  yAxesstepHistory = new ArrayList<>();
        ArrayList<Entry>  yAxesweightHistory = new ArrayList<>();
        ArrayList<ILineDataSet> multipleData = new ArrayList<>();
        Cursor data = sqlLiteManager.getUserHistory();

        if(data.getCount() != 0){
            for (int i = 0; i<data.getCount(); i++) {
                data.moveToNext();

                float stepdata;
                float weightdata;

                if (data.getString(2) != null) {

                    if (isImperial == false) { //convert the weight to Lbs or KG
                        weightdata = Float.parseFloat(data.getString(2));
                    } else {
                        weightdata = (int) (Integer.parseInt(data.getString(2)) * 2.205);
                    }
                    yAxesweightHistory.add(new Entry(i, weightdata));


                }
                if (data.getString(1) != null) {
                    stepdata = Float.parseFloat(data.getString(1));
                    yAxesstepHistory.add(new Entry(i, stepdata));


                }
            }

            //if(data.getString(2) != null){
            if(yAxesweightHistory.size() > 1){
                LineDataSet dataSetWeightHistory;
                if (isImperial == false){ //convert the weight to Lbs or KG
                    dataSetWeightHistory = new LineDataSet(yAxesweightHistory, "Weight Kg");
                }else{
                    dataSetWeightHistory = new LineDataSet(yAxesweightHistory, "Weight Lbs");
                }
                dataSetWeightHistory.setDrawValues(false);
                dataSetWeightHistory.setDrawCircles(false);
                dataSetWeightHistory.setColor(Color.GREEN);
                dataSetWeightHistory.setLineWidth(5);
                multipleData.add(dataSetWeightHistory);

            }

            if(data.getString(1) != null){
                LineDataSet dataSetStepHistory = new LineDataSet(yAxesstepHistory, "Steps");
                dataSetStepHistory.setDrawValues(false);
                dataSetStepHistory.setDrawCircles(false);
                dataSetStepHistory.setColor(Color.RED);
                dataSetStepHistory.setLineWidth(5);
                multipleData.add(dataSetStepHistory);

            }
        }

        lineChartDiagram.setData(new LineData(multipleData));

        Description description = new Description();
        if(data.getCount() < 2){
            txtWeNeedMoreDays.setText("We Need More Days to create this Graph");
            txtWeNeedMoreDays.setVisibility(View.VISIBLE);

        }else{
            txtWeNeedMoreDays.setVisibility(View.GONE);
        }

        description.setText("");
        lineChartDiagram.setDescription(description);



        //lineChartDiagram.setVisibleXRangeMaximum(65f);
        lineChartDiagram.animateY(1000);
    }

    private void getTodayPhoto(){
        Cursor img = sqlLiteManager.getImage();

        try{
            if(img.getCount() != -1) {
                img.moveToNext();
                byte[] byteArray = img.getBlob(0); //gets the Bytes that the database holds
                if(byteArray != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length); //Converts the Bytes to BitMap

                    imgCurrentPhoto.setImageBitmap(bitmap);
                }
            }
        }catch (Exception e){

        }
    }

    private void getPhotoForDashboard(){
        Cursor img = sqlLiteManager.getImage();

        try{
            if(img.getCount() != -1) {
                img.moveToNext();
                byte[] byteArray = img.getBlob(0); //gets the Bytes that the database holds
                if(byteArray != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length); //Converts the Bytes to BitMap

                    imageView.setImageBitmap(bitmap);
                }
            }
        }catch (Exception e){

        }
    }

    public int getCurrentPageIndex(){
        return position;
    }
}
