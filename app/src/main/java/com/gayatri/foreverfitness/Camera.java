package com.gayatri.foreverfitness;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera extends AppCompatActivity {
    Context context;
    String pathToFile;
    ImageView imageView;
    Button btnSave, btnRetake, btnBack;
    private String Name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context =  getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        imageView = findViewById(R.id.ImageViewPhoto);
        btnSave = findViewById(R.id.BtnSave);
        btnRetake = findViewById(R.id.BtnRetake);
        btnBack = findViewById(R.id.BtnBack);
        final SqlLiteManager sqlLiteManager = new SqlLiteManager(this);
        Bundle bundle = getIntent().getExtras();
        Name = sqlLiteManager.getCurrentUserName();

        sqlLiteManager.getUserId(Name);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            OpenCameraActivity();
        }else{
            OpenCameraActivity();
        }
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//              saves to the database
                sqlLiteManager.saveImage(pathToFile);
                finish();
                Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show();
            }
        });


        btnRetake.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OpenCameraActivity();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlLiteManager.close();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
         if(requestCode==1){
        // decode the path to File

             Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
             Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                     bitmap, 768, 1024, false); //makes the file smaller

        //Set the results to Bitmap
             imageView.setImageBitmap(resizedBitmap);
         }
    }


        private void OpenCameraActivity(){

        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Check if there is an App to handle the intent
        if(takePic.resolveActivity(getPackageManager()) != null){
            //Define a file where a photo will be stored
            File photoFile  = null;
            //Create this method to handle file creation
            photoFile = createPhotoFile();
            //Check if photo file is not empty
            if(photoFile!=null){
                //Get Path
                pathToFile = photoFile.getAbsolutePath();
                // Create a URL for all the apps to access the file
                Uri photoURI = FileProvider.getUriForFile(this,"com.thecodecity.comeraandroid.fileprovider",photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //Start the intent
                startActivityForResult(takePic,1);
            }else{
            }}

    }

    private File createPhotoFile(){
        String name = new SimpleDateFormat("yyyymmdd").format(new Date());

       // for it to be accessible by other applications

        File storageDir = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES);
        File photo = null;
        try{
            photo = File.createTempFile(name,".jpg",storageDir);

        }catch (IOException e){ //Exception message
            Log.d("mylog", "Exception :"+ e.toString());    }
    return photo;
        }


}




