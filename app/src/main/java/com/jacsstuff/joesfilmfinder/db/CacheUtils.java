package com.jacsstuff.joesfilmfinder.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class CacheUtils {


    public boolean contains(SQLiteDatabase db, String table, String colName, String value) {
        String query = "SELECT * FROM " + table + " WHERE " + colName + " = " + quotes(value);
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() > 0){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


    String quotes(String str){
        return "'" + str + "'";
    }
    List<Long> getExpiredIds(SQLiteDatabase db, String tableName, String dateColumn, int days, String idColumn) {

        String expirationQuery = generateExpirationQuery(tableName, dateColumn, days);
        Cursor cursor = db.rawQuery(expirationQuery, null);
        List<Long> expiredIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            expiredIds.add(getLong(cursor, idColumn));
        }
        cursor.close();
        return expiredIds;
    }


    boolean runQuery(SQLiteDatabase db, String query){
        boolean isSuccessful = true;
        try {
            db.execSQL(query);
        }catch(SQLException e){
            e.printStackTrace();
            isSuccessful = false;
        }
        return isSuccessful;
    }

    String generateExpirationQuery(String table, String dateColumn, int days){

        return "SELECT * FROM  " + table + " WHERE " + dateColumn + " <= date('now','" + days + " day')";

    }

    String getStr(Cursor cursor, String name){
        return cursor.getString(cursor.getColumnIndexOrThrow(name));
    }

    byte[] getBytes(Cursor cursor, String name){
        return cursor.getBlob(cursor.getColumnIndexOrThrow(name));
    }


    int getInt(Cursor cursor, String name){
        return cursor.getInt(cursor.getColumnIndexOrThrow(name));
    }

    long getLong(Cursor cursor, String name){
        return cursor.getLong(cursor.getColumnIndexOrThrow(name));
    }
}
