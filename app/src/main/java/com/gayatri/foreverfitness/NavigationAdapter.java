package com.gayatri.foreverfitness;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NavigationAdapter extends PagerAdapter {
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
            "oo0"
    };
    Context context;
    LayoutInflater inflater;
    private String AccountName;
    private MyCustomObjectListener listener;
    private int position;
    View view;

    //Dashboard Page
    private SeekBar weightBar;
    private TextView displayWeight, txtViewBMI;
    PieChart weightPieLimit;
    private Button btnSubmitWight;
    List<PieEntry> weightPieList;
    private TextView txtCurrentDateSelect;
    private DatePickerDialog.OnDateSetListener onCurrentDateSetListener;
    private String currentDate;

    //Settings Page
    private Button btnDeleteAcount;
    private TextView name, textViewSetWeightGoal, txtChangeToImperialHeader, txtMeasurementHeader, txtSelectDate, txtGoalDate, textViewWeight, txtViewHeight, txtGender;
    private Switch switchImperial, switchGender;
    private SeekBar seekBarWeightGoal;
    private boolean isImperial;
    private Button btnViewHistory;

    private EditText editTextName, editTextWeight, editTextHeight;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private DatePickerDialog.OnDateSetListener onGoalDateSetListener;
    private String birthday, goalDate;
    private boolean isMale = true;


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
        if (position % 3 == 0) {
            //o position page
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }else if (position % 3 == 1) {
            //Dashboard
            view = inflater.inflate(R.layout.fragment_dashboard, container, false);
            weightBar = (SeekBar) view.findViewById(R.id.seekBar);
            displayWeight = (TextView) view.findViewById(R.id.txtCalories);
            weightPieLimit = view.findViewById(R.id.WeightLimitPie);
            btnSubmitWight = view.findViewById(R.id.BtnSubmitWight);
            txtViewBMI = view.findViewById(R.id.TxtViewBMI);
            txtCurrentDateSelect = view.findViewById(R.id.TxtCurrentDateSelect);

            LoadCurrentWeightAndDate();

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
                    currentDate = day+"-"+month+"-"+year;
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
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            createPieChartWeight();
            calculateBMI();
            btnSubmitWight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double weightSet = weightBar.getProgress()/10;
                    sqlLiteManager.saveWeightAndDate(weightSet, currentDate);
                    createPieChartWeight();
                    calculateBMI();
                }
            });

        }else if (position % 3 == 2) {
            //2 position page Settings
            view = inflater.inflate(R.layout.fragment_settings, container, false);
            btnDeleteAcount = view.findViewById(R.id.BtnDeleteAccount);
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


            editTextHeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
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

            btnDeleteAcount.setOnClickListener(new View.OnClickListener() {
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

            btnViewHistory = view.findViewById(R.id.BtnViewHistory);

            btnViewHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent history = new Intent(view.getContext(), HistoryActivity.class);
                    history.putExtra("Name", AccountName);
                    history.putExtra("isImperial", true);
                    view.getContext().startActivity(history);
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
        //loadweight from database
        int loadWeight = sqlLiteManager.getWeight();//default value 70
        String date = sqlLiteManager.getDate();
        weightBar.setProgress(loadWeight * 10);
        txtCurrentDateSelect.setText(date);

        if (sqlLiteManager.getUserImperial() == false){
            displayWeight.setText((double)loadWeight + " Kg");
        }else{
            displayWeight.setText((int)((loadWeight)*2.205) + " Lbs");
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

    private void calculateBMI(){
        try{
            double bodyMassIndex = 0;
            double userWeight = sqlLiteManager.getWeight();//70 default
            double userHeight = sqlLiteManager.getHeight();//70 default

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
        int getweight = (int)sqlLiteManager.getUserWeightGoal();;

        if((int)userWeight< getweight){
            if (sqlLiteManager.getUserImperial() == false) {
                weightPieList.add(new PieEntry((int) userWeight, (int) userWeight + " KG"));
                weightPieList.add(new PieEntry(getweight - (int) userWeight, getweight - (int) userWeight + "KG"));
            }else{

                weightPieList.add(new PieEntry((int) userWeight, (int) (userWeight * 2.205) + " Lbs"));
                weightPieList.add(new PieEntry(getweight - (int) userWeight, (int) (getweight*2.205) - (int) (userWeight * 2.205) + " Lbs"));
            }

        }else{

            if (sqlLiteManager.getUserImperial() == false) {
                weightPieList.add(new PieEntry((int) userWeight,(int)userWeight + " KG"));
                weightPieList.add(new PieEntry(((getweight-(int)userWeight)*-1),(int)((getweight-userWeight)*-1)+ " KG"));

            }else{

                weightPieList.add(new PieEntry((int) userWeight,(int)(userWeight*2.205) + " Lbs"));
                weightPieList.add(new PieEntry(((getweight-(int)userWeight)*-1),(int)(((getweight-userWeight)*-1)*2.205)+ " Lbs"));
            }

        }
        PieDataSet weightPieDataSet;
        if((int)userWeight< getweight){
            weightPieDataSet = new PieDataSet(weightPieList,"Weight to gain");
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
        //Used to set the discription for the Pie chart
        Description description = new Description();
        description.setText("");
        weightPieLimit.setDescription(description);

    }


    public int getCurrentPageIndex(){
        return position;
    }
}
