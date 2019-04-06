package com.jacsstuff.joesfilmfinder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jacsstuff.joesfilmfinder.db.DbContract.*;

import com.jacsstuff.joesfilmfinder.results.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class SearchCache {

    private SQLiteDatabase db;
    private CacheUtils cacheUtils;

    public SearchCache(Context context){
        DbHelper mDbHelper = DbHelper.getInstance(context);
        db = mDbHelper.getWritableDatabase();
        cacheUtils = new CacheUtils();
    }


    public boolean contains(String searchTerm){
        String searchTermCol = SearchResultEntry.COL_SEARCH_TERM;
        String table = SearchResultEntry.TABLE_NAME;

        String query = "SELECT " + searchTermCol + " FROM " + table + " WHERE " + searchTermCol + " = " + cacheUtils.quotes(searchTerm);
        Cursor cursor = db.rawQuery(query, null);
        boolean found = cursor.getCount() > 0;
        cursor.close();
        return found;
    }

    public void add(String searchTerm, List<SearchResult> searchResults){

        for(SearchResult searchResult : searchResults){

            ContentValues contentValues = new ContentValues();

            contentValues.put(SearchResultEntry.COL_SEARCH_TERM, searchTerm);
            contentValues.put(SearchResultEntry.COL_NAME, searchResult.getName());
            contentValues.put(SearchResultEntry.COL_URL, searchResult.getUrl());
            contentValues.put(SearchResultEntry.COL_IMAGE, searchResult.getPhotoBytes());

            db.insert(SearchResultEntry.TABLE_NAME, null, contentValues);
        }
    }

    public List<SearchResult> get(String searchTerm){

        String query = "SELECT * FROM " + SearchResultEntry.TABLE_NAME +
                        "WHERE " + SearchResultEntry.COL_SEARCH_TERM +
                        " = " + cacheUtils.quotes(searchTerm);

        List<SearchResult> searchResults = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()){

            SearchResult searchResult = new SearchResult();
            searchResult.setName(cacheUtils.getStr(cursor, SearchResultEntry.COL_NAME));
            searchResult.setUrl(cacheUtils.getStr(cursor, SearchResultEntry.COL_URL));
            searchResult.setPhotoBytes(cacheUtils.getBytes(cursor, SearchResultEntry.COL_IMAGE));
            searchResults.add(searchResult);
        }

        return searchResults;
    }

    public void deleteOlderThan(int days){

        List<Long> idsToDelete = cacheUtils.getExpiredIds(db, SearchResultEntry.TABLE_NAME, SearchResultEntry.COL_DATE_CREATED, days, SearchResultEntry._ID);
        for(long id : idsToDelete){
            db.execSQL("DELETE * FROM " + SearchResultEntry.TABLE_NAME + " WHERE " + SearchResultEntry._ID + " = " + id);
        }
    }


    public void deleteAll(){
        cacheUtils.runQuery(db, "DELETE FROM " + SearchResultEntry.TABLE_NAME);
    }
}
