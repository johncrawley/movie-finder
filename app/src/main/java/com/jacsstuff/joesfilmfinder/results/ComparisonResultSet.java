package com.jacsstuff.joesfilmfinder.results;

import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by John on 01/03/2016.
 *
 */
public class ComparisonResultSet {

    List<ResultLink> resultLinks;
    boolean isActorMovieComparison;
    private String actorName, movieName, roles;
    private boolean isEmptyComparisonResultSet;


    public List<ResultLink> getResultLinks() {
        return resultLinks;
    }


    public boolean isActorMovieComparison() {
        return isActorMovieComparison;
    }


    public ComparisonResultSet(){
        resultLinks = new ArrayList<>(0);
        isEmptyComparisonResultSet = true;
        isActorMovieComparison = true;
        Log.i("ComparisonResultSet", "No results Comparison Result Set created.");
    }

    // unused boolean here, just to demonstrate that this constructor is used
    // to create a Comparison Result where a problem was encountered.
    public ComparisonResultSet(boolean falseValue){
        resultLinks = new ArrayList<>(0);
        isActorMovieComparison = false;
    }

    public ComparisonResultSet(List<ResultLink> results){
        this.resultLinks = new ArrayList<>(results);
        Collections.sort(this.resultLinks);
        this.isActorMovieComparison = false;
        if(this.resultLinks.isEmpty()){
            this.isEmptyComparisonResultSet = true;
        }

        Log.i("ComparisonResultSet", "normal Comparison Result Set created, isEmptyComparisonResultSet:" +isEmptyComparisonResultSet);
    }

    // might be better to just add a resultLink for the constructor
    // this constructor gets called when a successful comparison has been made between an actor and a movie.
    public ComparisonResultSet(String actorName, String roles, String movieName){
        this.isActorMovieComparison = true;
        this.isEmptyComparisonResultSet = false;
        this.actorName = actorName;
        this.movieName = movieName;
        this.roles = roles;

        Log.i("ComparisonResultSet", "isActorMovieComparison Comparison Result Set created.");
    }

    public boolean isEmpty(){
        return this.isEmptyComparisonResultSet;
    }
    public String getActorName(){
        return this.actorName;
    }
    public String getRoles(){
        return this.roles;
    }
    public int count(){return this.resultLinks.size();}
}
