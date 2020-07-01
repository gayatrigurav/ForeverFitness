package com.gayatri.foreverfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout linearLayoutHistory;
    private TextView txtDate,txtWeight;
    private Button txtTakeMeBack;
    private boolean isImperial = false;
    private SqlLiteManager sqlLiteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        sqlLiteManager = new SqlLiteManager(HistoryActivity.this);

        String Name = sqlLiteManager.getCurrentUserName();
        sqlLiteManager.getUserId(Name);
        isImperial = sqlLiteManager.getUserImperial();
        linearLayoutHistory = findViewById(R.id.LinearLayoutHistory);

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        Cursor cursor = sqlLiteManager.getUserHistory();
        for (int i = 0; i< cursor.getCount(); i++){
            cursor.moveToNext();
            View view = layoutInflater.inflate(R.layout.activity_history_details, linearLayoutHistory, false);
            txtDate = view.findViewById(R.id.TxtDate);

            txtWeight = view.findViewById(R.id.TxtWeight);
            txtDate.setText(cursor.getString(3));

            if(cursor.getString(1) != null){

                if (isImperial == false){
                    txtWeight.setText( (cursor.getString(1)) + " Kg");
                }else{
                    int imperial = (int) (Integer.parseInt(cursor.getString(1))*2.205);
                    txtWeight.setText(imperial + " Lbs");
                }
            }else{
                txtWeight.setText("No weight recorded");
            }


            linearLayoutHistory.addView(view);
        }

        txtTakeMeBack = findViewById(R.id.TxtTakeMeBack);

        txtTakeMeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}