package com.jacsstuff.joesfilmfinder.results;

import android.graphics.Bitmap;
import android.util.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by John on 08/03/2016.
 * This class is used to contain a list of search results for a given search term.
 * The class follows the singleton pattern so that the results can be cached between activities and
 *  for as long as the app is running.
 * It follows the same kind of pattern as the Profiles Cache object - once the limit has been exceeded,
 *  older results will get deleted. However, unlike Profiles Cache, it will not save anything to permanent storage.
 *
 *  This class is used by the Search Result Set class. Perhaps, instead of a nested map, we should use a map of SearchResultSets here,
 *  but all we really need to store is the lists of lists of Search Results. The Search Result Set contains runtime vars such as 'currentlySelectedID'
 *  that we don't need to store here.
 *
 */
public class SearchResultsSingleton {
    private static SearchResultsSingleton uniqueInstance;

    private final int MAX_STORED_RESULTS = 200;
    private final int MAX_IMAGE_MAP_SIZE = 100;


    private HashMap<String, List<SearchResult>> searchResultMap;
    private Map<String, Bitmap> downloadedImageMap;
    private final int NUMBER_OF_SEARCH_TERMS = 2;
    private String[] searchTerms;


    public static SearchResultsSingleton getInstance(){

        if(uniqueInstance == null){
            uniqueInstance = new SearchResultsSingleton();
        }
        return uniqueInstance;
    }


    private SearchResultsSingleton(){
        searchResultMap = new HashMap<>(MAX_STORED_RESULTS);
        downloadedImageMap = new HashMap<>();
        searchTerms = new String[NUMBER_OF_SEARCH_TERMS];
    }


    public void log(String msg){
        Log.i("SearchResultsSingleton", msg);
    }


    //NEW IN 2017
    public void add(String searchTerm, List<SearchResult> results, int searchNumber, boolean areSearchTermsIdentical){
        log("Adding " +  results.size() + " results for " + searchTerm + " on searchNumber: "+  searchNumber);
        if(searchNumber > NUMBER_OF_SEARCH_TERMS){
            return;
        }
        if(areSearchTermsIdentical){
            setSearchTerm(searchTerm, 1);
            setSearchTerm(searchTerm, 2);
        }
        else {
            setSearchTerm(searchTerm, searchNumber);
        }
        searchResultMap.put(searchTerm, results);
    }


    public void setSearchTerm(String searchTerm, int searchNumber){
        searchTerms[searchNumber-1] = searchTerm;
    }

    public boolean hasResults(){
        boolean allResultsFound = true;
        for(String searchTerm : searchTerms){
            if(!containsSearchResultsFor(searchTerm)){
                allResultsFound = false;
            }
        }
        log("hasResults() - allResultsFound: " + allResultsFound);
        log("hasResults() - areSearchTermsNullOrEmpty(): " + areSearchTermsNullOrEmpty());
        return !areSearchTermsNullOrEmpty() && allResultsFound;
    }

    public boolean hasExistingResult(String searchTerm){
        return containsSearchResultsFor(searchTerm);
    }

    public boolean hasResults(String input1, String input2){
        return (!areSearchTermsNullOrEmpty()) && containsSearchResultsFor(input1) && containsSearchResultsFor(input2);
    }

    public boolean hasNoResults(int index){
        return !hasExistingResult(searchTerms[index-1]);
    }

    private boolean areSearchTermsNullOrEmpty(){
        for(String searchTerm: searchTerms){
            log("Search term: " + searchTerm);
            if(searchTerm == null || searchTerm.isEmpty()){
                return true;
            }
        }
        return false;
    }

    private boolean containsSearchResultsFor(String searchTerm){
        List<SearchResult> searchResults = searchResultMap.get(searchTerm);
        return searchResults != null && !searchResults.isEmpty();
    }

    public SearchResultSet getSearchResultSet(String searchTerm, int searchNumber){

        int idCounter = searchNumber * 100;
        List<SearchResult> searchResults = searchResultMap.get(searchTerm);
        if(searchResults == null){
            return null;
        }
        HashMap<Integer, SearchResult> searchResultsMap = new HashMap<>(searchResults.size());
        for(SearchResult result : searchResults){
            searchResultsMap.put(idCounter++, result);
        }
        return new SearchResultSet(searchResultsMap);
    }


    public SearchResultSet getSearchResultSet(int number){
        return getSearchResultSet(searchTerms[number-1], number);
    }


    public boolean containsImage(String key){
        return downloadedImageMap.containsKey(key);

    }

    public Bitmap getImage(String key){
        return downloadedImageMap.get(key);
    }

    public void putImage(String key, Bitmap bm){
        if(downloadedImageMap.size() <= MAX_IMAGE_MAP_SIZE){
            downloadedImageMap.put(key, bm);
        }
    }

}
