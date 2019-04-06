package com.jacsstuff.joesfilmfinder.controller;

import android.content.Context;
import android.util.Log;

import com.jacsstuff.joesfilmfinder.SearchingView;
import com.jacsstuff.joesfilmfinder.tasks.SearchResultsGeneratorTask;
import com.jacsstuff.joesfilmfinder.results.SearchResultsSingleton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by John on 05/12/2017.
 * Handles the searching for movies and actors invoked via the main activity
 */
public class MainController {

    private Context context;
    private boolean downloadTimedOut = false;
    private SearchingView searchingView;
    private  SearchResultsSingleton resultsSingleton;


    public MainController(SearchingView searchingView, Context context) {

        this.context = context;
        this.searchingView = searchingView;
        resultsSingleton = SearchResultsSingleton.getInstance();
    }

    public void setDownloadTimeoutFlag(){
        downloadTimedOut = true;
    }

    public void executeSearches(String...searchTerms){
        ExecutorService executorService  = Executors.newFixedThreadPool(3);

        if(searchTerms[0].equals(searchTerms[1])){
            executeSearch(searchTerms[0], executorService,1, true);
        }
        else {
            executeSearch(searchTerms[0], executorService, 1, false);
            executeSearch(searchTerms[1], executorService, 2, false);
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            //
        }

        postExecute();

    }

    private void executeSearch(String searchTerm, ExecutorService executorService, int searchNumber, boolean areSearchesIdentical){
        executorService.execute(new SearchResultsGeneratorTask(searchTerm, this.context, this, searchNumber, areSearchesIdentical));
    }

    public boolean hasCachedResults(String searchTerm1, String searchTerm2){
        return resultsSingleton.hasResults(searchTerm1, searchTerm2);
    }

    private void postExecute(){
        searchingView.dismissSearchingDialog();

        if(downloadTimedOut){
            downloadTimedOut = false;
            searchingView.createServerUnavailableText();
        }

        if(resultsSingleton.hasResults()){
            searchingView.startSelectResultsActivity();
            return;
        }
        Log.i("MainController", "postExecute: The resultsSingleton has no results");
        if(resultsSingleton.hasNoResults(1)) searchingView.showNoResultsText1();
        if(resultsSingleton.hasNoResults(2)) searchingView.showNoResultsText2();
    }

}
