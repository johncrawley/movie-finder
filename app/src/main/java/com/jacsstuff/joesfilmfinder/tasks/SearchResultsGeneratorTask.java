package com.jacsstuff.joesfilmfinder.tasks;

import android.content.Context;
import android.util.Log;

import com.jacsstuff.joesfilmfinder.DownloadTimeoutException;
import com.jacsstuff.joesfilmfinder.controller.MainController;
import com.jacsstuff.joesfilmfinder.R;
import com.jacsstuff.joesfilmfinder.Utils;
import com.jacsstuff.joesfilmfinder.db.SearchCache;
import com.jacsstuff.joesfilmfinder.parsers.InlineSearchResultParser;
import com.jacsstuff.joesfilmfinder.parsers.NetUtils;
import com.jacsstuff.joesfilmfinder.results.SearchResult;
import com.jacsstuff.joesfilmfinder.results.SearchResultsSingleton;

import java.io.IOException;
import java.util.List;

/**
 * Created by John on 04/12/2017.
 * A task that accesses and parses a webpage for search results
 */
public class SearchResultsGeneratorTask implements Runnable {
    private SearchResultsSingleton resultsStore;
    private String urlPrefix, urlSuffix;
    private Context context;
    private String searchTerm;
    private MainController mainController;
    private int searchResultNumber; //currently either 1 or 2
    private boolean areSearchesIdentical;
    private SearchCache searchCache;



    public SearchResultsGeneratorTask(String searchTerm, Context context, MainController mainController, int searchResultNumber, boolean areSearchesIdentical){

        this.context = context;
        this.resultsStore = SearchResultsSingleton.getInstance();
        this.urlPrefix = context.getResources().getString(R.string.url_prefix);
        this.urlSuffix = context.getResources().getString(R.string.url_suffix);
        this.searchTerm = Utils.sanitizeUrlPart(searchTerm);
        this.mainController = mainController;
        this.searchResultNumber = searchResultNumber;
        this.areSearchesIdentical = areSearchesIdentical;

        searchCache = new SearchCache(context);
    }

    private void generateResultSet() throws DownloadTimeoutException, IOException {

        List<SearchResult> results;
        if(searchCache.contains(searchTerm)){
            results = searchCache.get(searchTerm);
        }
        else {
            InlineSearchResultParser searchResultParser = new InlineSearchResultParser(this.context);
            NetUtils.downloadAndParseWebpage(urlPrefix + searchTerm + urlSuffix, searchResultParser);

            results = searchResultParser.getResults();
            if (results == null) {
                return;
            }
        }
        resultsStore.add(searchTerm, results, searchResultNumber, areSearchesIdentical);

    }



    public void run(){
        try{
            generateResultSet();
        }catch(DownloadTimeoutException e){
            e.printStackTrace();
            mainController.setDownloadTimeoutFlag();
        }catch(IOException e){
            Log.i("SearchResultsTask", "IO Exception found.");
            e.printStackTrace();
            mainController.setDownloadTimeoutFlag();

        }
    }
}
