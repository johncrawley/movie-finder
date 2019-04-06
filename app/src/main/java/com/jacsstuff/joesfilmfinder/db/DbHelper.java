package com.jacsstuff.joesfilmfinder.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper instance;


    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "Quiz.db";

    private static final String OPENING_BRACKET = " (";
    private static final String CLOSING_BRACKET = " );";
    private static final  String INTEGER = " INTEGER";
    private static final String TEXT = " TEXT";
    private static final String BLOB = " BLOB";
    private static final String DATETIME = " DATETIME";
    private static final String COMMA = ",";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";



    private static final String SQL_CREATE_PROFILES_TABLE =
            CREATE_TABLE_IF_NOT_EXISTS + DbContract.ProfilesEntry.TABLE_NAME + OPENING_BRACKET +
                    DbContract.ProfilesEntry._ID                          + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.ProfilesEntry.COL_URL                         + TEXT               + COMMA +
                    DbContract.ProfilesEntry.COL_NAME                        + TEXT               + COMMA +
                    DbContract.ProfilesEntry.COL_IMAGE                       + BLOB               + COMMA +
                    DbContract.ProfilesEntry.COL_DATE_CREATED               +  DATETIME +  " DEFAULT CURRENT_TIMESTAMP" + CLOSING_BRACKET;

    private static final String SQL_CREATE_RESULT_LINKS_TABLE =
            CREATE_TABLE_IF_NOT_EXISTS + DbContract.ResultLinksEntry.TABLE_NAME + OPENING_BRACKET +
                    DbContract.ResultLinksEntry._ID     + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.ResultLinksEntry.COL_PROFILE_ID + INTEGER + COMMA +
                    DbContract.ResultLinksEntry.COL_NAME + TEXT + COMMA +
                    DbContract.ResultLinksEntry.COL_URL + TEXT + COMMA +
                    DbContract.ResultLinksEntry.COL_YEAR + TEXT + COMMA +
                    DbContract.ResultLinksEntry.COL_ROLES + TEXT + COMMA +
                    DbContract.ResultLinksEntry.COL_CHARACTER_NAME + TEXT + COMMA +
                    DbContract.ResultLinksEntry.COL_CHARACTER_URL + TEXT + CLOSING_BRACKET;

    private static final String SQL_CREATE_SEARCH_RESULTS_TABLE =
            CREATE_TABLE_IF_NOT_EXISTS + DbContract.SearchResultEntry.TABLE_NAME + OPENING_BRACKET +
                    DbContract.SearchResultEntry._ID + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.SearchResultEntry.COL_SEARCH_TERM + TEXT + COMMA +
                    DbContract.SearchResultEntry.COL_NAME + TEXT + COMMA +
                    DbContract.SearchResultEntry.COL_URL + TEXT + COMMA +
                    DbContract.SearchResultEntry.COL_IMAGE + BLOB + COMMA +
                    DbContract.SearchResultEntry.COL_DATE_CREATED + DATETIME + " DEFAULT CURRENT_TIMESTAMP" + CLOSING_BRACKET;

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DbHelper getInstance(Context context){
        if(instance == null){
            instance = new DbHelper(context);
        }
        return instance;
    }
    public void onCreate(SQLiteDatabase db) {
        Log.i("DBHelper", " Entering onCreate() ...creating the db tables.");
        db.execSQL(SQL_CREATE_PROFILES_TABLE);
        db.execSQL(SQL_CREATE_RESULT_LINKS_TABLE);
        db.execSQL(SQL_CREATE_SEARCH_RESULTS_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }






}
