package com.gayatri.foreverfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnCreateUser;
    private EditText editTextName, editTextWeight, editTextHeight;
    private Switch switchImperial, switchGender;
    private TextView txtSelectDate, textViewWeight, txtViewHeight, txtGender, txtChangeToImperialHeader, txtMeasurementHeader;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private String birthday;
    private String name;
    private boolean isMale = true;
    private boolean isImperial = false;
    private SqlLiteManager sqlLiteManager;
    private SeekBar seekBarWeightGoal;
    private String goalDate;
    private TextView textViewSetWeightGoal, txtGoalDate;
    private DatePickerDialog.OnDateSetListener onGoalDateSetListener;
    private double goalWeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnCreateUser = (Button) findViewById(R.id.BtnCreateUser);
        switchGender = (Switch) findViewById(R.id.SwitchGender);
        switchImperial = (Switch)findViewById(R.id.SwitchImperial);
        txtSelectDate = (TextView) findViewById(R.id.TxtSelectDate);
        textViewWeight = (TextView) findViewById(R.id.TextViewWeight);
        txtViewHeight = (TextView) findViewById(R.id.TextViewHeight);
        txtChangeToImperialHeader =  (TextView) findViewById(R.id.TxtChangeToImperialHeader);
        txtMeasurementHeader = (TextView) findViewById(R.id.TxtMeasurementHeader);
        txtChangeToImperialHeader.setOnClickListener(this);
        switchImperial.setOnClickListener(this);
        switchGender.setOnClickListener(this);
        btnCreateUser.setOnClickListener(this);
        editTextName = (EditText) findViewById (R.id.EditTxtName);
        txtGender = (TextView) findViewById(R.id.txtGender);
        txtGender.setOnClickListener(this);
        editTextWeight = (EditText)findViewById(R.id.EditTextWeight);
        editTextHeight = (EditText)findViewById(R.id.EditTextHeight);
        sqlLiteManager = new SqlLiteManager(this);
        textViewSetWeightGoal = findViewById(R.id.TextViewSetWeightGoal);
        txtGoalDate = (TextView) findViewById(R.id.TxtGoalDateSelect);
        seekBarWeightGoal = (SeekBar) findViewById(R.id.SeekBarWeightGoal);
        seekBarWeightGoal.setMax(8);
        seekBarWeightGoal.setProgress(0);
        goalWeight = (0+4)*10;

        IsUserExist();

        txtGoalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        WelcomeActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onGoalDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        onGoalDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                goalDate = day+"-"+month+"-"+year;
                txtGoalDate.setText(goalDate);
            }
        };

        seekBarWeightGoal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isImperial == false){
                    textViewSetWeightGoal.setText("Current Weight Goal: " + (progress+4)*10 + "Kg");
                    goalWeight = (progress+4)*10;
                }else{
                    textViewSetWeightGoal.setText("Current Weight Goal: " + (int)(((progress+4)*10)*2.205) + "Lbs");
                    goalWeight = (int)(((progress+4)*10)*2.205);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        txtSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        WelcomeActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                birthday = day+"-"+month+"-"+year;
                txtSelectDate.setText(birthday);
            }
        };
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.SwitchGender || v.getId() == R.id.txtGender) {
            if (isMale == true) {
                isMale = false;
                txtGender.setText("Female");
                switchGender.setChecked(true);
            } else {
                isMale = true;
                txtGender.setText("Male");
                switchGender.setChecked(false);
            }
        } else if (v.getId() == R.id.SwitchImperial || v.getId() == R.id.TxtChangeToImperialHeader) {
            //If the user changes the program to imperial or metric
           DecimalFormat df2 = new DecimalFormat("#.##");

            if (isImperial == false) {
                isImperial = true;
                textViewWeight.setText("Weight: Lbs");
                txtViewHeight.setText("Height: Feet");
                switchImperial.setChecked(true);
                txtMeasurementHeader.setText("Measurement System - Imperial");
                txtChangeToImperialHeader.setText("Change to Metric");
                if (!editTextWeight.getText().toString().isEmpty()) {
                    editTextWeight.setText(Double.toString(Double.parseDouble(df2.format(Double.parseDouble(editTextWeight.getText().toString()) * 2.205))));
                }
                if (!editTextHeight.getText().toString().isEmpty()) {
                    editTextHeight.setText(Double.toString(Double.parseDouble(df2.format(Double.parseDouble(editTextHeight.getText().toString()) * 3.28084))));
                }
                textViewSetWeightGoal.setText("Current Weight Goal: " + (int)(((seekBarWeightGoal.getProgress()+4)*10)*2.205) + "Lbs");
                goalWeight = (int)(((seekBarWeightGoal.getProgress()+4)*10)*2.205);

            } else {
                isImperial = false;
                textViewWeight.setText("Weight: Kg");
                txtViewHeight.setText("Height: Meters");
                switchImperial.setChecked(false);
                txtMeasurementHeader.setText("Measurement System - Metric");
                txtChangeToImperialHeader.setText("Change to Imperial");
                if (!editTextWeight.getText().toString().isEmpty()) {
                    editTextWeight.setText(Double.toString(Double.parseDouble(df2.format(Double.parseDouble(editTextWeight.getText().toString()) / 2.205))));
                }
                if (!editTextHeight.getText().toString().isEmpty()) {
                    editTextHeight.setText(Double.toString(Double.parseDouble(df2.format(Double.parseDouble(editTextHeight.getText().toString()) / 3.28084))));
                }
                textViewSetWeightGoal.setText("Current Weight Goal: " + (seekBarWeightGoal.getProgress()+4)*10 + "Kg");
                goalWeight = (seekBarWeightGoal.getProgress()+4)*10;
            }
        } else if (v.getId() == R.id.BtnCreateUser) {


            if (checkInputValidations()) {
                String name = editTextName.getText().toString();
                Double weight = Double.parseDouble(editTextWeight.getText().toString());
                Double height = Double.parseDouble(editTextHeight.getText().toString());
                /*if (isImperial == true) {
                    weight = weight / 2.20462;
                    height = height / 3.28084;
                }*/
                String goalDate = txtGoalDate.getText().toString();
                sqlLiteManager.addUser(name, weight, height, isMale, isImperial, birthday, goalWeight, goalDate);

                this.name = editTextName.getText().toString();
                GotoMainActivity();
            }
        }
    }

    private void GotoMainActivity() {
        Intent MainActivity = new Intent(this, com.gayatri.foreverfitness.MainActivity.class);
        MainActivity.putExtra("Name",this.name);
        MainActivity.putExtra("IsImperial",this.isImperial);
        finish();
        startActivity(MainActivity);
    }

    private void IsUserExist(){

        if (sqlLiteManager.checkAnyUserExits()){
            //USER EXISTS IN DATABASE
            this.name = sqlLiteManager.getCurrentUserName();
            GotoMainActivity();
        }
    }

    public boolean checkInputValidations(){


        if (editTextName.getText().toString().length() != 0 &&
                editTextWeight.getText().toString().length() !=0 &&
                editTextHeight.getText().toString().length() != 0 &&
                birthday != null &&
                goalDate != null &&
                goalWeight > 0) {
            return true;
        }else if(birthday == null || goalDate == null){
            if (birthday == null)
            txtSelectDate.setTextColor(Color.parseColor("#FF0000"));
            if (goalDate == null)
                txtGoalDate.setTextColor(Color.parseColor("#FF0000"));
        }



        if(editTextName.getText().toString().isEmpty())
        {
            editTextName.setHint("Please Enter Your Name Here");
            editTextName.setHintTextColor(Color.parseColor("#FF0000"));
        }

        if (editTextWeight.getText().toString().isEmpty()){
            editTextWeight.setHint("Please Enter Your Weight Here");
            editTextWeight.setHintTextColor(Color.parseColor("#FF0000"));
        }

        if ( editTextHeight.getText().toString().isEmpty()){
            editTextHeight.setHint("Please Enter Your Height Here");
            editTextHeight.setHintTextColor(Color.parseColor("#FF0000"));
        }

        if(goalWeight == 40.0)
        {
            textViewSetWeightGoal.setHint("Please set desired weight");
            textViewSetWeightGoal.setHintTextColor(Color.parseColor("#FF0000"));

        }



        return false;
    }
}