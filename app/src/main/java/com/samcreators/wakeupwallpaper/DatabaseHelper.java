package com.samcreators.wakeupwallpaper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "WallpaperX";
    public static final String TABLE_NAME = "Recent";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +"(Id INTEGER, Time VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertData(int id, String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Id", id);
        contentValues.put("Time", time);
        db.insert(TABLE_NAME,null, contentValues);
    }

    public Cursor getAllRecent(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT Id FROM " + TABLE_NAME + " ORDER BY Time DESC LIMIT 40",null);
    }
    public boolean checkIdInRecent(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT Id FROM " + TABLE_NAME + " WHERE Id=" + id,null);
        return c.moveToFirst();
    }
}
