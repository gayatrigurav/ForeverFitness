package com.gayatri.foreverfitness;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
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

    //Settings Page
    private Button btnDeleteAcount;
    private TextView name, textViewSetWeightGoal, txtChangeToImperialHeader, txtMeasurementHeader;
    private Switch switchImperial;
    private SeekBar seekBarWeightGoal;
    private boolean isImperial;
    private Button btnViewHistory;


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

            //loadweight from database
            int loadWeight = sqlLiteManager.loadWeight();//default value 70
            weightBar.setProgress(loadWeight * 10);

            if (sqlLiteManager.getUserImperial() == false){
                displayWeight.setText((double)loadWeight + " Kg");
            }else{
                displayWeight.setText((int)((loadWeight)*2.205) + " Lbs");
            }

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
                    sqlLiteManager.saveWeight(weightSet);
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
            txtChangeToImperialHeader =  view.findViewById(R.id.TxtChangeToImperialHeader);
            txtMeasurementHeader = view.findViewById(R.id.TxtMeasurementHeader);
            seekBarWeightGoal = (SeekBar) view.findViewById(R.id.SeekBarWeightGoal);
            seekBarWeightGoal.setMax(8);

            txtChangeToImperialHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    isImperial = sqlLiteManager.getUserImperial();
                        //If the user changes the program to imperial or metric
                        if (isImperial == false) {
                            isImperial = true;
                            sqlLiteManager.setUserImperial(true);
                            switchImperial.setChecked(true);
                            txtMeasurementHeader.setText("Measurement System - Imperial");
                            txtChangeToImperialHeader.setText("Change to Metric");
                            textViewSetWeightGoal.setText("Current Weight Goal: " + (int)(((seekBarWeightGoal.getProgress()+4)*10)*2.205) + "Lbs");
                        }else{
                            isImperial = false;
                            sqlLiteManager.setUserImperial(false);
                            switchImperial.setChecked(false);
                            txtMeasurementHeader.setText("Measurement System - Metric");
                            txtChangeToImperialHeader.setText("Change to Imperial");
                            textViewSetWeightGoal.setText("Current Weight Goal: " + (seekBarWeightGoal.getProgress()+4)*10 + "Kg");
                        }

                }
            });

            switchImperial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isImperial = switchImperial.isChecked();
                    sqlLiteManager.setUserImperial(isImperial);
                    if (isImperial == false){
                        textViewSetWeightGoal.setText("Current Weight Goal: " + (seekBarWeightGoal.getProgress()+4)*10 + "Kg");
                        txtMeasurementHeader.setText("Measurement System - Metric");
                        txtChangeToImperialHeader.setText("Change to Imperial");
                    }else{
                        textViewSetWeightGoal.setText("Current Weight Goal: " + (int)(((seekBarWeightGoal.getProgress()+4)*10)*2.205) + "Lbs");
                        txtMeasurementHeader.setText("Measurement System - Imperial");
                        txtChangeToImperialHeader.setText("Change to Metric");
                    }
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
                    if (sqlLiteManager.getUserImperial() == false){
                        textViewSetWeightGoal.setText("Current Weight Goal: " + (progress+4)*10 + "Kg");
                    }else{
                        textViewSetWeightGoal.setText("Current Weight Goal: " + (int)(((progress+4)*10)*2.205) + "Lbs");
                    }
                    sqlLiteManager.updateUserGoal((progress+4)*10);
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
                    Intent history = new Intent(view.getContext(), HistoryActivity.class );
                    history.putExtra("Name",AccountName);
                    history.putExtra("isImperial",true);
                    view.getContext().startActivity(history);
                }
            });

            //Get User Details
            String name = sqlLiteManager.getName();
            this.name.setText(name);
            switchImperial.setChecked(isImperial);
            double userGoal = sqlLiteManager.getUserGoal();
            textViewSetWeightGoal.setText("Current Weight Goal: " + userGoal + "Kg");
            seekBarWeightGoal.setProgress((int)(userGoal/10-4));
        }
        container.addView(view);
        return view;
    }

    private void calculateBMI(){
        try{
            double bodyMassIndex = 0;
            double userWeight = sqlLiteManager.loadWeight();//70 default
            double userHeight = sqlLiteManager.loadHeight();//70 default

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
        double userWeight = sqlLiteManager.loadWeight();//70 defaults
        int getweight = (int)sqlLiteManager.getUserGoal();;

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
