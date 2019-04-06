package com.jacsstuff.joesfilmfinder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jacsstuff.joesfilmfinder.profiles.AbstractProfile;
import com.jacsstuff.joesfilmfinder.profiles.ParsedProfile;
import com.jacsstuff.joesfilmfinder.results.ResultLink;
import com.jacsstuff.joesfilmfinder.db.DbContract.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileCache {
    private SQLiteDatabase db;
    private CacheUtils cacheUtils;

    public ProfileCache(Context context){
        DbHelper mDbHelper = DbHelper.getInstance(context);
        db = mDbHelper.getWritableDatabase();
        cacheUtils = new CacheUtils();
    }

    public boolean add(ParsedProfile parsedProfile){

        db.beginTransaction();
        save(parsedProfile);
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    private void save(ParsedProfile profile){
        ContentValues contentValues = new ContentValues();

        contentValues.put(ProfilesEntry.COL_URL, profile.getUrl());
        contentValues.put(ProfilesEntry.COL_NAME, profile.getName());
        contentValues.put(ProfilesEntry.COL_IMAGE, profile.getProfilePicBytes());
        contentValues.put(ProfilesEntry.COL_DATE_CREATED, getDateTime());

        long profileId = db.insert(ProfilesEntry.TABLE_NAME, null, contentValues);

        for(ResultLink resultLink : profile.getResultLinks()) {
            saveResultLink(resultLink, profileId);
        }
    }

    private void saveResultLink(ResultLink resultLink, long profileId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ResultLinksEntry.COL_PROFILE_ID, profileId);
        contentValues.put(ResultLinksEntry.COL_URL, resultLink.getUrl());
        contentValues.put(ResultLinksEntry.COL_NAME, resultLink.getName());
        contentValues.put(ResultLinksEntry.COL_YEAR, resultLink.getYear());
        contentValues.put(ResultLinksEntry.COL_CHARACTER_NAME, resultLink.getCharacterName());
        contentValues.put(ResultLinksEntry.COL_CHARACTER_URL, resultLink.getCharacterUrl());
        contentValues.put(ResultLinksEntry.COL_ROLES, resultLink.getRolesAsSingleLine());

        db.insert(ResultLinksEntry.TABLE_NAME, null, contentValues);
    }

    public String getColumnNames(){
      Cursor cursor = db.rawQuery("SELECT sql FROM sqlite_master WHERE tbl_name = " + cacheUtils.quotes(ProfilesEntry.TABLE_NAME) + " AND type = 'table'", null);
      cursor.moveToFirst();
      return cacheUtils.getStr(cursor, "sql");

    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public <T extends AbstractProfile> T getProfile(String url, Class <T> profileClass) {
        T profile = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + ProfilesEntry.TABLE_NAME +
                                        " WHERE " + ProfilesEntry.COL_URL +
                                        " = " + cacheUtils.quotes(url), null);
        cursor.moveToFirst();

        try {
            profile = profileClass.newInstance();
            profile.setUrl(cacheUtils.getStr(cursor, ProfilesEntry.COL_URL));
            profile.setName(cacheUtils.getStr(cursor, ProfilesEntry.COL_NAME));
            profile.setImageBytes(cacheUtils.getBytes(cursor, ProfilesEntry.COL_IMAGE));
            long id = cacheUtils.getLong(cursor, ProfilesEntry._ID);
            cursor.close();
            profile.setResultLinks(getResultLinks(id));
        }catch(InstantiationException | IllegalAccessException e){
            Log.e("ProfileCache", e.getMessage());
            cursor.close();
        }
        return profile;
    }


    private List<ResultLink> getResultLinks(long id){
        List<ResultLink> resultLinks = new ArrayList<>();
        String query = "SELECT * FROM " + ResultLinksEntry.TABLE_NAME + " WHERE " + ResultLinksEntry.COL_PROFILE_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        Log.i("profileCache", "getResultLinks() query: "+  query);
        while(cursor.moveToNext()){
            ResultLink resultLink = new ResultLink();
            resultLink.setUrl(cacheUtils.getStr(cursor,  ResultLinksEntry.COL_URL));
            resultLink.setName(cacheUtils.getStr(cursor, ResultLinksEntry.COL_NAME));
            resultLink.setYear(cacheUtils.getStr(cursor, ResultLinksEntry.COL_YEAR));
            resultLink.addRoles(cacheUtils.getStr(cursor, ResultLinksEntry.COL_ROLES));
            resultLinks.add(resultLink);
        }
        Log.i("ProfileCache", "getResultLinks() results size :" + cursor.getCount());
        cursor.close();
        return resultLinks;
    }



    public void deleteAll(){
        cacheUtils.runQuery(db, "DELETE FROM  "+  ProfilesEntry.TABLE_NAME);
        cacheUtils.runQuery(db, "DELETE FROM  "+  ResultLinksEntry.TABLE_NAME);
    }


    private void deleteIfOlderThan(int days){

        List<Long> idsToDelete = cacheUtils.getExpiredIds(db, ProfilesEntry.TABLE_NAME, ProfilesEntry.COL_DATE_CREATED, days, ProfilesEntry._ID);
        for(long id : idsToDelete){
            deleteProfile(id);
        }
    }


    private void deleteProfile(long id){

        String deleteProfileQuery = "DELETE FROM " + ProfilesEntry.TABLE_NAME + " WHERE " + ProfilesEntry._ID + " = " + id;
        db.execSQL(deleteProfileQuery);
        String deleteResultLinksQuery = "DELETE FROM " + ResultLinksEntry.TABLE_NAME + " WHERE " + ResultLinksEntry.COL_PROFILE_ID + " = " + id;
        db.execSQL(deleteResultLinksQuery);
    }


    public boolean contains(String url) {
        return cacheUtils.contains(db, ProfilesEntry.TABLE_NAME, ProfilesEntry.COL_URL, url);
    }


}
