package com.gayatri.foreverfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout linearLayoutHistory;
    private TextView txtDate,txtWeight, txtSteps;
    private Button txtTakeMeBack;
    private ImageView imgUser;
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

        txtTakeMeBack = findViewById(R.id.TxtTakeMeBack);
        txtTakeMeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlLiteManager.close();
                finish();
            }
        });

        Cursor cursor = sqlLiteManager.getUserHistory();
        if(cursor.getCount()!=0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                View view = layoutInflater.inflate(R.layout.activity_history_details, linearLayoutHistory, false);
                txtDate = view.findViewById(R.id.TxtDate);
                txtWeight = view.findViewById(R.id.TxtWeight);
                txtSteps = view.findViewById(R.id.TxtSteps);
                imgUser = view.findViewById(R.id.ImgUser);

                cursor.moveToNext();
                String date = cursor.getString(5);
                txtDate.setText(date);
                String steps = cursor.getString(1);
                txtSteps.setText(steps);

                if (cursor.getString(2) != null) {
                    if (isImperial == false) {
                        txtWeight.setText((cursor.getString(2)) + " Kg");
                    } else {
                        int imperial = (int) (Integer.parseInt(cursor.getString(2)) * 2.205);
                        txtWeight.setText(imperial + " Lbs");
                    }
                } else {
                    txtWeight.setText("No weight recorded");
                }
                byte[] byteArray = cursor.getBlob(4); //gets the Bytes that the database holds
                if (byteArray != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length); //Converts the Bytes to BitMap
                    imgUser.setImageBitmap(bitmap);
                }
                linearLayoutHistory.addView(view);
            }
        }
    }
}