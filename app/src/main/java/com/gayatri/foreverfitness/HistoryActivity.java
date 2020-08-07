package com.gayatri.foreverfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import android.widget.Toast;

import java.util.zip.Inflater;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout linearLayoutHistory;
    private TextView txtDate,txtWeight, txtSteps, txtMilestoneId;
    private Button txtTakeMeBack, btnDeleteAllHistory, btnDeleteHistory;

    private ImageView imgUser;
    private boolean isImperial = false;
    private SqlLiteManager sqlLiteManager;
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        sqlLiteManager = new SqlLiteManager(HistoryActivity.this);

        String Name = sqlLiteManager.getCurrentUserName();
        sqlLiteManager.getUserId(Name);
        isImperial = sqlLiteManager.getUserImperial();
        linearLayoutHistory = findViewById(R.id.LinearLayoutHistory);

        layoutInflater = LayoutInflater.from(this);

        txtTakeMeBack = findViewById(R.id.TxtTakeMeBack);
        txtTakeMeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlLiteManager.close();
                finish();
            }
        });
        btnDeleteAllHistory = findViewById(R.id.BtnDeleteAllHistory);
        btnDeleteAllHistory.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   sqlLiteManager.deleteUserHistory();
                   Toast.makeText(HistoryActivity.this, "All History Deleted!", Toast.LENGTH_SHORT).show();
                   linearLayoutHistory.removeAllViews();
               }
           }
        );

        LoadDetails(layoutInflater);
    }

    private void LoadDetails(LayoutInflater layoutInflater) {
        Cursor cursor = sqlLiteManager.getUserHistory();
        if(cursor.getCount()!=0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                final View view = layoutInflater.inflate(R.layout.activity_history_details, linearLayoutHistory, false);
                txtMilestoneId = view.findViewById(R.id.milestoneId);
                txtDate = view.findViewById(R.id.TxtDate);
                txtWeight = view.findViewById(R.id.TxtWeight);
                txtSteps = view.findViewById(R.id.TxtSteps);
                imgUser = view.findViewById(R.id.ImgUser);
                btnDeleteHistory = view.findViewById(R.id.BtnDeleteHistory);
                btnDeleteHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int milestoneId = Integer.parseInt(txtMilestoneId.getTag().toString());
                        sqlLiteManager.deleteUserHistoryEntry(milestoneId);
                        Toast.makeText(HistoryActivity.this, "This Entry Deleted!", Toast.LENGTH_SHORT).show();
                        linearLayoutHistory.removeView(view);
                        //linearLayoutHistory.removeDetachedView(view,false);

                    }
                });

                LoadHistory(cursor, view);
            }
        }
    }

    private void LoadHistory(Cursor cursor, View view) {
        cursor.moveToNext();
        String date = cursor.getString(5);
        txtDate.setText(date);
        String steps = cursor.getString(1);
        txtSteps.setText(steps);
        ///read id for entry
        int milestoneId = cursor.getInt(0);
        txtMilestoneId.setTag(Integer.valueOf(milestoneId));

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