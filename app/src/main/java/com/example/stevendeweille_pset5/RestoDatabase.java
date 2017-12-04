package com.example.stevendeweille_pset5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestoDatabase extends SQLiteOpenHelper {
    private RestoDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private static RestoDatabase instance;

    public static RestoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new RestoDatabase(context.getApplicationContext(), "resto", null, 3);
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE resto (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price DOUBLE, amount INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS resto");
        onCreate(db);
    }

    public Cursor selectAll() {
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("SELECT * FROM resto",null);
    }


    public void addItem(String name, Double price) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        // Check if item already in database
        Cursor cursor = db.rawQuery("SELECT * FROM resto WHERE name = '"+name+"'",null);
        // Insert item if not already in database
        if (cursor.getCount() < 1) {
            values.put("name", name);
            values.put("price", price);
            values.put("amount", 1);
            db.insert("resto", null, values);
        }
        // If already in database increase amount of item with 1
        else {
            cursor.moveToFirst();
            values.put("amount", cursor.getInt(cursor.getColumnIndex("amount"))+1);
            db.execSQL("UPDATE resto SET amount = amount+1 where name ='"+name+"';");
        }
    }

    // Clear database
    public void clear() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("resto", null, null);
    }
}
