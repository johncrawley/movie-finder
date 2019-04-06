package com.jacsstuff.joesfilmfinder.controller;

import android.content.Context;
import android.util.Log;


import com.jacsstuff.joesfilmfinder.activities.CompareResultsView;
import com.jacsstuff.joesfilmfinder.profiles.GeneralProfile;
import com.jacsstuff.joesfilmfinder.profiles.ParsedProfile;
import com.jacsstuff.joesfilmfinder.results.ComparisonResultSet;
import com.jacsstuff.joesfilmfinder.results.EmptyComparisonResultsSet;
import com.jacsstuff.joesfilmfinder.tasks.ParsedProfileGeneratorTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by John on 31/03/2018
 */
public class CompareResultsController {

    private CompareResultsView view;
    private Context context;
    private boolean hasDownloadTimedOut;
    private List<ParsedProfile> parsedProfiles;

    public CompareResultsController(CompareResultsView view, Context context){
        this.view = view;
        this.context = context;
        this.parsedProfiles = new ArrayList<>();
        parsedProfiles.add(null);
        parsedProfiles.add(null);
        log("init: parsedProfiles list size: " + parsedProfiles.size());
    }

    public void executeProfileSearches(List<GeneralProfile> profiles){
        ExecutorService executorService = Executors.newCachedThreadPool();

        for(int i = 0; i < profiles.size(); i++){
            ParsedProfileGeneratorTask task = new ParsedProfileGeneratorTask(context, this, profiles.get(i), i);
            executorService.execute(task);
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // wait until all results are in.
        }

        ComparisonResultSet comparisonResults;
        if(ifHasNoNullEntries(parsedProfiles)) {
            comparisonResults = parsedProfiles.get(0).compareTo(parsedProfiles.get(1));
        }
        else{
            logNullProfiles();
            comparisonResults = new EmptyComparisonResultsSet();
        }
        view.setComparisonResults(comparisonResults);
        if(comparisonResults.isActorMovieComparison()){
            view.showActorMovieComparison(comparisonResults.getActorName(), comparisonResults.getRoles());
        }
        else {
            if(comparisonResults.isEmpty()){
                view.showNoResultsFound();
            }
            else{
                view.setTitleText(comparisonResults.count());
            }
        }
        setProfilePicsAndTitles(parsedProfiles);
    }

    private void setProfilePicsAndTitles(List<ParsedProfile> parsedProfiles){
        int index = 0;
        for(ParsedProfile p: parsedProfiles){
            if(p==null){
                continue;
            }
            if((p.getProfileBitmap() != null) && (p.getName() != null)){
                view.setProfilePic(index, p.getProfileBitmap(), p.getUrl());
            }
            if(p.getName() != null){
                view.setProfileTitle(p.getName(), index);
            }
            index++;
        }
    }


    private void logNullProfiles(){
        int counter = 1;
        for(ParsedProfile profile: parsedProfiles){
            if(profile == null){
                log("Result " + counter + " is null.");
            }
            counter++;
        }

    }

    private boolean ifHasNoNullEntries(List<?> list){
        for(int i = 0; i<list.size(); i++){
            if(list.get(i) == null)return false;
        }
        return true;
    }

    private void log(String msg){
        Log.i("CompareController", msg);
    }

    public void addParsedProfile(ParsedProfile profile, int profileIndex){
        parsedProfiles.set(profileIndex, profile);
    }

    public boolean hasDownloadTimedOut(){
        return this.hasDownloadTimedOut;
    }

    public void notifyException(){
        this.hasDownloadTimedOut = true;
    }



}
