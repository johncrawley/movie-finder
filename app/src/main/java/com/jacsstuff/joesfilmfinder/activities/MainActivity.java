package com.jacsstuff.joesfilmfinder.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.jacsstuff.joesfilmfinder.DownloadTimeoutException;
import com.jacsstuff.joesfilmfinder.controller.MainController;
import com.jacsstuff.joesfilmfinder.SearchingView;
import com.jacsstuff.joesfilmfinder.db.ProfileCache;
import com.jacsstuff.joesfilmfinder.db.SearchCache;
import com.jacsstuff.joesfilmfinder.parsers.InlineSearchResultParser;
import com.jacsstuff.joesfilmfinder.parsers.NetUtils;
import com.jacsstuff.joesfilmfinder.preferences.AboutAppDialogue;
import com.jacsstuff.joesfilmfinder.preferences.PreferencesActivity;
import com.jacsstuff.joesfilmfinder.Utils;
import com.jacsstuff.joesfilmfinder.R;
import com.jacsstuff.joesfilmfinder.results.SearchResult;
import com.jacsstuff.joesfilmfinder.results.SearchResultsSingleton;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 The main activity displays 2 input fields and a search button.
 Then the two input fields have been filled with search terms for actors or movies,
  pressing the search button causes this class's AsyncTask subclass  to start, which sends 2 requests to
  the website and processes the 2 responses into two SearchResultSets which are stored in a singleton class
  to be handled by the next activity which is called.

 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, SearchingView {

    private ProgressDialog searchingDialog; // the loading dialog should appear when the AsyncTask runs and disappear when it returns.
    boolean hasPreviousSearchRequestCompleted = true; // when set to false, this bool stops the user pressing the search button a bunch of times
    private MainController controller;
    private Button searchButton;
    private EditText editText1, editText2;
    private TextView noResultsText1, noResultsText2;
    private Map <Integer, Boolean> areSearchesComplete;
    private Map <Integer, Boolean> areSearchesSuccessful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = MainActivity.this;
        setContentView(R.layout.activity_main);
        setupViews();
        setupToolbar();
        initSearchStatus();
        controller = new MainController(MainActivity.this, context);

        ProfileCache cache = new ProfileCache(context);
        Log.i("mainActivity onCreate", " profile table cols: "+  cache.getColumnNames());
    }


    private void initSearchStatus(){
      areSearchesComplete =    initMap();
      areSearchesSuccessful =  initMap();
    }

    private Map<Integer, Boolean> initMap(){
        Map<Integer, Boolean> map = new HashMap<>();
        map.put(1, false);
        map.put(2, false);
        return map;
    }

    private void setupViews(){

        searchButton = findViewById(R.id.findButton);
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        noResultsText1 = findViewById(R.id.label_text_no_results_1);
        noResultsText2 = findViewById(R.id.label_text_no_results_2);
        searchButton.setOnClickListener(this);

        // adding a watcher to the input fields to disable/enable the search button
        TextWatcher textWatcher = createTextWatcher();
        editText1.addTextChangedListener(textWatcher);
        editText2.addTextChangedListener(textWatcher);

    }


    private void setupToolbar(){

        // creating and setting up the app bar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar == null){
            return;
        }
        actionBar.setHomeButtonEnabled(true);

    }


    private TextWatcher createTextWatcher(){
       return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(isEditTextEmpty(R.id.editText1) || isEditTextEmpty(R.id.editText2)){
                    searchButton.setEnabled(false);
                    return;
                }
                searchButton.setEnabled(true);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private boolean isEditTextEmpty(int id){
        EditText editText = findViewById(id);
        return editText == null || getText(editText).isEmpty();
    }


    public void onClick(View view){

        // this minimizes the keyboard when the search button is pressed
        if(view.getId() == R.id.findButton){
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(mgr != null){
                mgr.hideSoftInputFromWindow(editText2.getWindowToken(), 0);
            }
            findProfiles();
        }
    }


    // starts the next activity
    public void startSelectResultsActivity(){
        dismissSearchingDialog();
        Intent intent = new Intent(this, SelectResultsActivity.class);
        startActivity(intent);
    }


    // when the 'search' button is clicked, we will take the search terms and
    // use them to download corresponding webpages
    // then parse parsedProfiles from them that work is done by the SearchResultSet instances from an Async inner class
    // We also create a loading dialogue, which will be dismissed by the async instance
    // when it's finished running.
    private void findProfiles(){

        String searchTerm1 = getText(editText1);
        String searchTerm2 = getText(editText2);

        if (inputsAreEmptyOrStillSearching(searchTerm1, searchTerm2) || networkIsDownAndNoCachedResults(searchTerm1, searchTerm2)) {
            return;
        }
        noResultsText1.setVisibility(View.INVISIBLE);
        noResultsText2.setVisibility(View.INVISIBLE);

        //checking a flag so to allow the user to only start 1 request at a time.
        hasPreviousSearchRequestCompleted = false;
        displaySearchingDialog();
        if(searchTerm1.equals(searchTerm2)){
            new AsyncWebpageDownloaderTask(this).execute(new SearchInput(searchTerm1, 1, true));
            return;
        }
        new AsyncWebpageDownloaderTask(this).execute(new SearchInput(searchTerm1, 1, false));
        new AsyncWebpageDownloaderTask(this).execute(new SearchInput(searchTerm2, 2, false));
    }


    private boolean inputsAreEmptyOrStillSearching(String str1, String str2){
        return (str1.isEmpty() && str2.isEmpty()) || !hasPreviousSearchRequestCompleted;
    }


    private boolean networkIsDownAndNoCachedResults(String searchStr1, String searchStr2){
        if(!controller.hasCachedResults(searchStr1, searchStr2)) {
            return Utils.displayDialogueIfNetworkUnavailable(MainActivity.this);
        }
       return false;
    }

    private String getText(EditText editText){
        return editText.getText().toString().trim();
    }

    private void displaySearchingDialog(){
        //bringing up the loading dialogue while we wait for the search parsedProfiles to download
        searchingDialog = new ProgressDialog(MainActivity.this);
        searchingDialog.setMessage(getResources().getString(R.string.searching_for_results_dialogue));
        searchingDialog.setCancelable(false);
        searchingDialog.show();
    }

    public void dismissSearchingDialog(){
        searchingDialog.dismiss();
        hasPreviousSearchRequestCompleted = true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.cache_menu_item) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.about_menu_item) {
            new AboutAppDialogue(MainActivity.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    public void showNoResultsText1(){ showNoResultsText(R.id.label_text_no_results_1);}
    public void showNoResultsText2(){ showNoResultsText(R.id.label_text_no_results_2);}

    public void displayNoResultsText(int searchNumber){
        int labelId = searchNumber == 1 ? R.id.label_text_no_results_1 : R.id.label_text_no_results_2;
        showNoResultsText(labelId);
    }

    public void createServerUnavailableText(){
        Utils.createDialog(MainActivity.this, R.string.server_unavailable);
    }

    private void showNoResultsText(int viewId){
        TextView noResultsText = findViewById(viewId);
        if(noResultsText != null) {
            noResultsText.setVisibility(View.VISIBLE);
        }
    }



    private void startNextActivityIfAllSearchesSuccessful(boolean areSearchTermsIdentical){


        if(checkMapStatus(areSearchesSuccessful, areSearchTermsIdentical)){
            initSearchStatus();
            startSelectResultsActivity();
        }
    }
    private void setSearchComplete(int searchNumber){
        areSearchesComplete.put(searchNumber, true);
    }

    private void setSearchSuccessful(int searchNumber){
        areSearchesSuccessful.put(searchNumber, true);
    }


    private boolean areAllSearchesComplete(boolean areSearchTermsIdentical){
        return checkMapStatus(areSearchesComplete, areSearchTermsIdentical);
    }

    private boolean checkMapStatus(Map<Integer, Boolean> map, boolean areSearchTermsIdentical){
        return  isMapTrue(map) || areSearchTermsIdentical;
    }


    private boolean isMapTrue(Map<Integer, Boolean> map){
        for(boolean value : map.values()){
            if(!value){
                return false;
            }
        }
        return true;
    }


    /*
        Need to use the async task instead of the controller's standard java task executer, because apparently the async task is required for
        the progress dialog to show.
     */
    public static class  AsyncWebpageDownloaderTask extends AsyncTask<SearchInput, Void, SearchResultsCode> {

        private WeakReference<MainActivity> activityReference;
        private SearchResultsSingleton resultsStore;
        private SearchCache searchCache;
        private SearchInput searchInput;

        private AsyncWebpageDownloaderTask(MainActivity activity) {
            activityReference = new WeakReference<>(activity);
            this.resultsStore = SearchResultsSingleton.getInstance();
        }

        protected SearchResultsCode doInBackground(SearchInput... searchInputs){
            MainActivity activity = activityReference.get();

            searchCache = new SearchCache(activity);
            String urlPrefix = activity.getResources().getString(R.string.url_prefix);
            String urlSuffix = activity.getResources().getString(R.string.url_suffix);

            searchInput = searchInputs[0];
            String searchTerm = Utils.sanitizeUrlPart(searchInput.getSearchTerm());

            List<SearchResult> results;
            if(searchCache.contains(searchTerm)){
                results = searchCache.get(searchTerm);
            }
            else {
                InlineSearchResultParser searchResultParser = new InlineSearchResultParser(activity);

                try {
                    NetUtils.downloadAndParseWebpage(urlPrefix + searchTerm + urlSuffix, searchResultParser);
                }catch (IOException | DownloadTimeoutException e){
                    return SearchResultsCode.DOWNLOAD_TIMED_OUT;
                }
                results = searchResultParser.getResults();
                if (results == null || results.size() == 0) {
                    return SearchResultsCode.NO_RESULTS_FOUND;
                }
            }
            resultsStore.add(searchTerm, results, searchInput.getSearchNumber(), searchInput.areSearchesIdentical());
            return SearchResultsCode.FOUND_RESULTS;
        }


        protected void onProgressUpdate(Void... params) { }


        protected void onPostExecute(SearchResultsCode result) {

            MainActivity activity = activityReference.get();
            activity.setSearchComplete(searchInput.getSearchNumber());



            if(result == SearchResultsCode.FOUND_RESULTS){
                Log.i("MainActivity Async", " postExecute() search was successful for :" + searchInput.getSearchTerm());
                activity.setSearchSuccessful(searchInput.getSearchNumber());
                activity.startNextActivityIfAllSearchesSuccessful(searchInput.areSearchesIdentical());
                return;
            }
            if(activity.areAllSearchesComplete(searchInput.areSearchesIdentical())) {
                activity.dismissSearchingDialog();
                activity.initSearchStatus();
            }
            if(result == SearchResultsCode.DOWNLOAD_TIMED_OUT){
                activity.createServerUnavailableText();
            }
            else if(result == SearchResultsCode.NO_RESULTS_FOUND){
               if(searchInput.areSearchesIdentical()){
                   activity.showNoResultsText1();
                   activity.showNoResultsText2();
               }
               else{
                   activity.displayNoResultsText(searchInput.getSearchNumber());
               }
            }
        }

    }


}


enum SearchResultsCode{
    FOUND_RESULTS, NO_RESULTS_FOUND, DOWNLOAD_TIMED_OUT
}

/*
 Simple DTO to pass search info to the async task
 */
class SearchInput{

    private String searchTerm;
    private int searchNumber;
    private boolean areSearchesIdentical;

    SearchInput(String searchTerm, int searchNumber, boolean areSearchesIdentical){

        this.searchTerm = searchTerm;
        this.searchNumber = searchNumber;
        this.areSearchesIdentical = areSearchesIdentical;
    }

    String getSearchTerm(){
        return searchTerm;
    }

    int getSearchNumber(){
        return searchNumber;
    }

    boolean areSearchesIdentical(){
        return this.areSearchesIdentical;
    }
}