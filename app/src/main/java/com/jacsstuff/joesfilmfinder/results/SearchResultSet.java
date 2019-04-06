package com.jacsstuff.joesfilmfinder.results;


import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by John on 02/03/2016.
 * Holds a list of Search Results and is used by the Select Results Activity to store the ID of
 * the currently selected result as well as a counter of the number of results. The counter isn't just
 * the number of results stored in an instance of this object but rather a number that is used to generate
 * the UI element ID that represents this object. Therefore, when initialising each Search Result Set,
 * numbers for the counters should be set to be quite far apart - e.g. 100,200,300 etc.
 *
 */
public class SearchResultSet {

    private Map<Integer, SearchResult> searchResultsMap;
    private int currentlySelectedId; // the ID of the UI element that is currently selected by the user - and corresponds to a Search Result ID.

    // the initial counter value should be different for each instance
    // good to have them 100 apart.
    public SearchResultSet(){
        searchResultsMap = new HashMap<>();
        resetSelectedId();
    }

    public SearchResultSet(Map<Integer,SearchResult> resultsMap){
        this();
        this.searchResultsMap = resultsMap;
    }


    public SearchResult getSearchResult(int key){
        return searchResultsMap.get(key);
    }

    public SearchResult getSelectedSearchResult(){
        return searchResultsMap.get(currentlySelectedId);
    }

    public Set<Integer> getKeySet(){
        return searchResultsMap.keySet();
    }

    public void resetSelectedId(){
        currentlySelectedId = -1;
    }
    public int getCurrentlySelectedId(){
        return currentlySelectedId;
    }
    public void setCurrentlySelectedId(int newId){
        this.currentlySelectedId = newId;
    }

    public void log(String msg){
        Log.i("SearchResultSet", msg);
    }

    public boolean isEmpty(){
        return searchResultsMap.isEmpty();
    }

}
