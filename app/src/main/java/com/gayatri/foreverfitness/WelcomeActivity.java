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
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnCreateUser;
    private EditText txtName, editTextWeight, editTextHeight;
    private Switch switchImperial, switchGender;
    private TextView txtSelectDate, textViewWeight, textViewHeight, txtGender;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private String Birthdate;
    private boolean isMale = true;
    private boolean isImperial = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnCreateUser = (Button) findViewById(R.id.BtnCreateUser);
        switchGender = (Switch) findViewById(R.id.SwitchGender);
        switchImperial = (Switch)findViewById(R.id.SwitchImperial);
        txtSelectDate = (TextView) findViewById(R.id.TxtSelectDate);
        textViewWeight = (TextView) findViewById(R.id.TextViewWeight);
        textViewHeight = (TextView) findViewById(R.id.TextViewHeight);
        switchImperial.setOnClickListener(this);
        switchGender.setOnClickListener(this);
        btnCreateUser.setOnClickListener(this);
        txtName = (EditText) findViewById (R.id.TxtName);
        txtGender = (TextView) findViewById(R.id.textGender);
        editTextWeight = (EditText)findViewById(R.id.EditTextWeight);
        editTextHeight = (EditText)findViewById(R.id.EditTextHeight);

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
                Birthdate = day+"-"+month+"-"+year;
                txtSelectDate.setText(Birthdate);
            }
        };
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.SwitchGender){
            if (isMale == true){
                isMale = false;
                txtGender.setText("Female");
            }else{
                isMale = true;
                txtGender.setText("Male");
            }
        }else if(v.getId() == R.id.SwitchImperial) {
            //If the user changes the program to imperial or metric
            if (isImperial == false) {
                isImperial = true;
                textViewWeight.setText("Weight: Lbs");
                textViewHeight.setText("Height: Feet");

            }else{
                isImperial = false;
                textViewWeight.setText("Weight: Kg");
                textViewHeight.setText("Height: Meters");

            }
        }else if(v.getId() == R.id.BtnCreateUser){
            if(checkInputValidations() && txtName.getText().toString().length() != 0) {
                Intent HomePage = new Intent(this, MainActivity.class);
                finish();
                startActivity(HomePage);
            }
        }
    }

    public boolean checkInputValidations(){
        if (editTextWeight.getText().toString().length() !=0 && editTextHeight.getText().toString().length() != 0 && Birthdate != null) {
            return true;
        }else if(Birthdate == null){

            txtSelectDate.setTextColor(Color.parseColor("#FF0000"));
        }

        if (editTextWeight.getText().toString().length() !=0 ){

        }else{
            editTextWeight.setHint("Please Enter Here");
            editTextWeight.setHintTextColor(Color.parseColor("#FF0000"));
        }

        if ( editTextHeight.getText().toString().length() != 0){

        }else{
            editTextHeight.setHint("Please Enter Here");
            editTextHeight.setHintTextColor(Color.parseColor("#FF0000"));

        }
        return false;
    }
}