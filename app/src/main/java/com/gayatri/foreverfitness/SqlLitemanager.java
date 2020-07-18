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
    private static final int DatabaseVersion = 2;

    public SqlLiteManager(Context context) {
        super(context, DatabaseName, null, DatabaseVersion);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //onCreate(sqLiteDatabase);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String drop = "drop table if exists goalTable";
        db.execSQL(drop);
        drop = "drop table if exists milestone";
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
                "  dateGoal text," +
                "   FOREIGN KEY (user_ID) REFERENCES userInfo(user_ID)" +
                ");";
        db.execSQL(GoalTable);


        String milestone = "create table IF NOT EXISTS milestone(" +
                "  milestone_ID INTEGER PRIMARY KEY autoincrement," +
                "  weight double," +
                "  user_ID INTEGER ," +
                "  daytime text,"+
                "   FOREIGN KEY (user_ID) REFERENCES userInfo(user_ID)" +
                ");";
        db.execSQL(milestone);
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
        contentValues.put("dateGoal", "");
        sqLiteDatabase.insert("goalTable",null, contentValues);
    }

    public void updateUserGoal(double weight, String date){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("weightGoal", weight);
        contentValues.put("dateGoal", date);
        sqLiteDatabase.update("goalTable",contentValues,"user_ID = " + UserId,null);
    }

    public double getUserWeightGoal(){

        double userWeightGoal=0.0;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select weightGoal from goalTable where (user_ID = " + UserId + ")";
        Cursor data = sqLiteDatabase.rawQuery(sql,null);
        data.moveToNext();
        if(data.getCount()!= 0) {
            userWeightGoal = Double.parseDouble(data.getString(0));
        }

        return userWeightGoal;
    }

    public String getUserDateGoal(){
        String userDateGoal="";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select dateGoal from goalTable where (user_ID = " + UserId + ")";
        Cursor data = sqLiteDatabase.rawQuery(sql,null);
        data.moveToNext();
        if(data.getCount()!= 0) {
            userDateGoal = data.getString(0);
        }
        return userDateGoal;
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

        sqLiteDatabase.delete("milestone","user_ID = " + UserId,null);
        sqLiteDatabase.delete("goalTable","user_ID = " + UserId,null);
        sqLiteDatabase.delete("userInfo","user_ID = " + UserId,null);

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


    private void setDate() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

        String currentDateTime = simpleDate.format(new Date(System.currentTimeMillis())); //Gets the current Date to check if data exists
        String sql = "Select day from milestone where (user_ID = " + UserId + " and daytime = '" + currentDateTime + "')";
        try{
            Cursor data = sqLiteDatabase.rawQuery(sql, null);
            int testing = data.getCount();

            if (data.getCount() == 0) { //If the Date Does not Exist
                ContentValues contentValues = new ContentValues();
                contentValues.put("user_ID", this.UserId);
                contentValues.put("daytime", currentDateTime);
                sqLiteDatabase.insert("milestone", null, contentValues); //if not, create Date
            }
        }catch (Exception e){                                                           //------------------Check if needed
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_ID", this.UserId);
            contentValues.put("daytime", currentDateTime);
            sqLiteDatabase.insert("milestone", null, contentValues); //if not, create Date
        }


    }


    public void saveWeight(double weight){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        String currentDateTime = simpleDate.format(new Date(System.currentTimeMillis())); //Gets the current Date to check if data exists

        ContentValues contentValues = new ContentValues();
        contentValues.put("user_ID",this.UserId);
        contentValues.put("weight",weight);
        contentValues.put("daytime", currentDateTime);

        long result = sqLiteDatabase.update("milestone",contentValues,"user_ID = " + UserId + " and daytime = '" + currentDateTime + "'",null);
        if (result == 0){
            sqLiteDatabase.insert("milestone",null, contentValues);
        }



    }

    public int loadWeight(){
        //SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        //String currentDateTime = simpleDate.format(new Date(System.currentTimeMillis())); //Gets the current Date to check if data exists
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select weight from milestone where (user_ID = " + UserId + " ORDER BY daytime DESC)";
        try{
            Cursor data = sqLiteDatabase.rawQuery(sql, null);
            data.moveToNext();
            return Integer.parseInt(data.getString(0));

        }catch (Throwable t) {
            return  70; //Returns default Weight
        }
    }

    public double loadHeight(){
        //SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");
        //String currentDate = simpleDate.format(new Date()); //Gets the current Date to check if data exists
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

    public String loadBirthdate(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select user_Birthdate from userInfo where (user_ID = " + UserId + ")";
        try{
            Cursor data = sqLiteDatabase.rawQuery(sql, null);
            data.moveToNext();
            return data.getString(0);
        }catch (Throwable t) {
            return  ""; //Returns default empty
        }
    }

    public boolean loadGender(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select user_isMale from userInfo where (user_ID = " + UserId + ")";
        try{
            Cursor data = sqLiteDatabase.rawQuery(sql, null);
            data.moveToNext();
            return (data.getInt(0)==1);
        }catch (Throwable t) {
            return  false; //Returns default false
        }
    }

    public Cursor getUserHistory(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "Select * from milestone where (user_ID = " + UserId + ") and weight > 0.0 ORDER BY milestone_ID DESC";
        Cursor data = sqLiteDatabase.rawQuery(sql,null);
        return data;
    }
}
