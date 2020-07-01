package com.gayatri.foreverfitness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

class SqlLiteManager extends SQLiteOpenHelper {

    private double UserId;
    private static final String DatabaseName = "foreverFitnessDatabase";
    private static final int DatabaseVersion = 1;

    public SqlLiteManager(Context context) {
        super(context, DatabaseName, null, DatabaseVersion);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String drop = "drop table if exists goalTable";
        db.execSQL(drop);
        drop = "drop table if exists stepTable";
        db.execSQL(drop);
        drop = "drop table if exists steps";
        db.execSQL(drop);
        drop = "drop table if exists userInfo";
        db.execSQL(drop);


        String UserTable = "create table IF NOT EXISTS userInfo(" +
                "  user_ID INTEGER PRIMARY KEY autoincrement," +
                "  user_Name Varchar(255)," +
                "  user_Weight double," +
                "  user_Height double," +
                "  user_isMale boolean," +
                "  user_isImperial boolean," +
                "  user_Birthdate date" +
                ");";
        db.execSQL(UserTable);


        String GoalTable = "create table IF NOT EXISTS goalTable(" +
                "  goal_ID INTEGER PRIMARY KEY autoincrement," +
                "  user_ID INTEGER ," +
                "  weightGoal double," +
                "  stepGoal INTEGER," +
                "   FOREIGN KEY (user_ID) REFERENCES userInfo(user_ID)" +
                ");";
        db.execSQL(GoalTable);


        String steps = "create table IF NOT EXISTS steps(" +
                "  step_ID INTEGER PRIMARY KEY autoincrement," +
                "  step INTEGER,"+
                "  weight double," +
                "  user_ID INTEGER ," +
                "  picture blob," +
                "day date,"+
                "   FOREIGN KEY (user_ID) REFERENCES userInfo(user_ID)" +
                ");";
        db.execSQL(steps);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String UserTable = "drop table if exists userInfo";
        db.execSQL(UserTable);
        //recreates the tables
        onCreate(db);
    }

    public void setDefaultGoals(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_ID", UserId);
        contentValues.put("weightGoal", 65);
        contentValues.put("stepGoal", 10000);
        sqLiteDatabase.insert("goalTable",null, contentValues);
    }

    public void updateUserGoal(int setps, double weight){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("weightGoal", weight);
        contentValues.put("stepGoal", setps);
        sqLiteDatabase.update("goalTable",contentValues,"user_ID = " + UserId,null);
    }

    public double[] getUserGoal(){

        double[] userGoal = new double[2];
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select stepGoal, weightGoal from goalTable where (user_ID = " + UserId + ")";
        Cursor data = sqLiteDatabase.rawQuery(sql,null);
        data.moveToNext();
        if(data.getCount()!= 0) {
            userGoal[0] = Double.parseDouble(data.getString(0));
            userGoal[1] = Double.parseDouble(data.getString(1));
        }

        return userGoal;
    }

    public String getName(){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select user_Name from userInfo where (user_ID = " + UserId + ")";
        Cursor data = sqLiteDatabase.rawQuery(sql,null);
        data.moveToNext();
        return data.getString(0);
    }

    public boolean addUser(String Name, double weight, double height,boolean isMale, boolean isImperial, String birthdate){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_Name", Name);
        contentValues.put("user_Weight", weight);
        contentValues.put("user_Height", height);
        contentValues.put("user_isMale", isMale);
        contentValues.put("user_isImperial", isImperial);
        contentValues.put("user_Birthdate", birthdate);
        long result = sqLiteDatabase.insert("userInfo",null, contentValues);
        if(result ==-1) {
            return false;
        }
        else{
            getUserId(Name);
            setDefaultGoals();
            return true;
        }
    }

    public void deleteUser(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.delete("steps","user_ID = " + UserId,null);
        sqLiteDatabase.delete("goalTable","user_ID = " + UserId,null);
        sqLiteDatabase.delete("userInfo","user_ID = " + UserId,null);

    }

    public Cursor userDebug(int number){

        if ( number ==1) {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            String sql = "Select * from userInfo where (user_ID = " + UserId + ")";
            Cursor data = sqLiteDatabase.rawQuery(sql, null);
            return data;
        }else if (number == 2){
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            String sql = "Select * from goalTable where (user_ID = " + UserId + ")";
            Cursor data = sqLiteDatabase.rawQuery(sql, null);
            return data;

        }else if (number == 3){
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            String sql = "Select step_ID,step,weight,user_ID, day from steps where (user_ID = " + UserId + ")";
            Cursor data = sqLiteDatabase.rawQuery(sql, null);
            return data;


        }else{

            SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");
            String currentDate = simpleDate.format(new Date());
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            String sql = "Select picture from steps where (user_ID = " + UserId + " and day = '" + currentDate + "')";
            Cursor data = sqLiteDatabase.rawQuery(sql,null);
            return data;

        }

    } //--------------------------------------Not needed, delete Later


    public boolean userExists(String Name){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select * from userInfo where (user_Name = \'" + Name + "\')";


        Cursor data = sqLiteDatabase.rawQuery(sql,null);


        if (data.getCount() != 0){

            return true;
        }else {
            return false;
        }

    }

    public boolean checkUserExits(String Name){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select * from userInfo where (user_Name = \'" + Name + "\')";
        try {
            Cursor data = sqLiteDatabase.rawQuery(sql,null);
            data.moveToNext();

            UserId = Double.parseDouble(data.getString(0));
            return true;
        }catch(Throwable t) {
            return false;
        }

    }

    public boolean checkAnyUserExits(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select * from userInfo";

        Cursor data = sqLiteDatabase.rawQuery(sql,null);

        if (data.getCount() != 0){

            return true;
        }else {
            return false;
        }

    }

    public String getCurrentUserName(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select user_Name from userInfo";
        Cursor data = sqLiteDatabase.rawQuery(sql,null);
        data.moveToNext();
        return data.getString(0);
    }

    public void getUserId(String Name){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select * from userInfo where (user_Name = \'" + Name + "\')";
        Cursor data = sqLiteDatabase.rawQuery(sql,null);
        data.moveToNext();

        UserId = Double.parseDouble(data.getString(0));
        setDate(); //checks if the date is new, then sets new row
    }

    public void setUserImperial(boolean isImperial){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_isImperial", isImperial);
        sqLiteDatabase.update("userInfo",contentValues,"user_ID = " + UserId,null);
    }

    public boolean getUserImperial(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select user_isImperial from userInfo where (user_ID = " + UserId + ")";
        Cursor data = sqLiteDatabase.rawQuery(sql, null);
        data.moveToNext();

        if (data.getInt(0) == 0){
            return false;
        }else{
            return true;
        }



    }

    public Cursor getImage(){
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = simpleDate.format(new Date());
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select picture from steps where (user_ID = " + UserId + " and day = '" + currentDate + "')";
        Cursor data = sqLiteDatabase.rawQuery(sql,null);
        return data;
    }

    public Boolean saveImage(String imagePath) {

        SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = simpleDate.format(new Date()); //Gets the current Date to check if data exists

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try{
            FileInputStream fs = new FileInputStream(imagePath); //gets the location of the photo
            byte[] imgbyte = new byte[fs.available()];
            fs.read(imgbyte);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length);

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                    bitmap, 768, 1024, false); //makes the file smaller

            ByteArrayOutputStream stream = new ByteArrayOutputStream(); //Convert BitsFactory to Bytes
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imgbyte = stream.toByteArray();



            ContentValues contentValues = new ContentValues();

            contentValues.put("picture",imgbyte);
            try{
                sqLiteDatabase.update("steps",contentValues,"user_ID = " + UserId  +" and day = '" + currentDate + "'",null);
            }catch (Throwable t) {
                sqLiteDatabase.insert("steps",null, contentValues);
            }
            fs.close();
            return true;
        }catch (Throwable t){
            return false;
        }

    }

    public void saveSteps(int steps){


        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = simpleDate.format(new Date()); //Gets the current Date to check if data exists

        ContentValues contentValues = new ContentValues();
        contentValues.put("user_ID",this.UserId);
        contentValues.put("step",steps);
        contentValues.put("day", currentDate);


        long result = sqLiteDatabase.update("steps",contentValues,"user_ID = " + UserId + " and day = '" + currentDate + "'",null);
        if (result == 0){
            sqLiteDatabase.insert("steps",null, contentValues);
        }



    }

    public int loadSteps(){ //Need to add dates
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = simpleDate.format(new Date()); //Gets the current Date to check if data exists
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select step from steps where (user_ID = " + UserId + " and day = '" + currentDate +"')";
        try{
            Cursor data = sqLiteDatabase.rawQuery(sql, null);
            data.moveToNext();

            int test = Integer.parseInt(data.getString(0));
            return Integer.parseInt(data.getString(0));


        }catch (Throwable t) {
            return  0;
        }
    }

    private void setDate() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");

        String currentDate = simpleDate.format(new Date()); //Gets the current Date to check if data exists
        String sql = "Select day from steps where (user_ID = " + UserId + " and day = '" + currentDate + "')";
        try{
            Cursor data = sqLiteDatabase.rawQuery(sql, null);
            int testing = data.getCount();

            if (data.getCount() == 0) { //If the Date Does not Exist
                ContentValues contentValues = new ContentValues();
                contentValues.put("user_ID", this.UserId);
                contentValues.put("day", currentDate);
                contentValues.put("step", 0);
                sqLiteDatabase.insert("steps", null, contentValues); //if not, create Date
            }
        }catch (Exception e){                                                           //------------------Check if needed
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_ID", this.UserId);
            contentValues.put("day", currentDate);
            sqLiteDatabase.insert("steps", null, contentValues); //if not, create Date
        }


    }



    public Cursor getInfoFromDate(String date){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "Select day from steps where (user_ID = " + UserId + "day = " + date + ")";
        Cursor data = sqLiteDatabase.rawQuery(sql, null);
        return data;
    }

    public void saveWeight(double weight){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = simpleDate.format(new Date()); //Gets the current Date to check if data exists

        ContentValues contentValues = new ContentValues();
        contentValues.put("user_ID",this.UserId);
        contentValues.put("weight",weight);
        contentValues.put("day", currentDate);


        long result = sqLiteDatabase.update("steps",contentValues,"user_ID = " + UserId + " and day = '" + currentDate + "'",null);
        if (result == 0){
            sqLiteDatabase.insert("steps",null, contentValues);
        }



    }

    public int loadWeight(){ //Need to add dates
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = simpleDate.format(new Date()); //Gets the current Date to check if data exists
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select weight from steps where (user_ID = " + UserId + " and day = '" + currentDate +"')";
        try{
            Cursor data = sqLiteDatabase.rawQuery(sql, null);
            data.moveToNext();


            return Integer.parseInt(data.getString(0));


        }catch (Throwable t) {
            return  70; //Returns default Weight
        }
    }

    public double loadHeight(){ //Need to add dates
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = simpleDate.format(new Date()); //Gets the current Date to check if data exists
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select user_Height from userInfo where (user_ID = " + UserId + ")";
        try{
            Cursor data = sqLiteDatabase.rawQuery(sql, null);
            data.moveToNext();


            return Double.parseDouble(data.getString(0));


        }catch (Throwable t) {
            return  70; //Returns default Weight
        }
    }

    public Cursor getUserHistory(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select * from steps where (user_ID = " + UserId + ") ORDER BY step_ID DESC";
        Cursor data = sqLiteDatabase.rawQuery(sql,null);
        return data;


    }
}
