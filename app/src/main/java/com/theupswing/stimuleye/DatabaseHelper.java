package com.theupswing.stimuleye;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "StimulEye.database";
    public static final String TABLE_NAME = "OutputData";
    public static final String COL_ID = "ID";
    public static final String COL_TIME = "Time";
    public static final String COL_DIAM = "Diameter";
    public static final String COL_FLASH = "Flash";

    private SQLiteDatabase database;
    private Context context;

    // Constructor to create a new database instance
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        database = this.getWritableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " ("+ COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_TIME+" INTEGER, "+COL_DIAM+" REAL, "+COL_FLASH+" INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void clearTable(){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        database.execSQL("CREATE TABLE " + TABLE_NAME + " ("+ COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_TIME+" INTEGER, "+COL_DIAM+" REAL, "+COL_FLASH+" INTEGER)");
    }

    public int insertData(int time, double diameter, int flash){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TIME, time);
        contentValues.put(COL_DIAM, diameter);
        contentValues.put(COL_FLASH, flash);

        return (int) database.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getAllData(){
        Cursor res = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

}
