package com.jacsstuff.joesfilmfinder.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacsstuff.joesfilmfinder.db.ProfileCache;
import com.jacsstuff.joesfilmfinder.preferences.AboutAppDialogue;
import com.jacsstuff.joesfilmfinder.preferences.PreferencesActivity;
import com.jacsstuff.joesfilmfinder.R;
import com.jacsstuff.joesfilmfinder.Utils;
import com.jacsstuff.joesfilmfinder.results.SearchResult;
import com.jacsstuff.joesfilmfinder.results.SearchResultSet;
import com.jacsstuff.joesfilmfinder.results.SearchResultsSingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
    The activity generates the 2nd page and allows users to select 2 of the parsedProfiles from the two searches
    and then compare them. The compare button leads the user to the 3rd activity where the comparison is
    performed and displayed.

 */
public class SelectResultsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button compareButton;
    private SearchResultSet resultSet1, resultSet2;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_results);
        setupToolbar();
        assignResultsToLayouts();
        setupCompareButton();
        context = SelectResultsActivity.this;
    }

    private void assignResultsToLayouts(){

        resultSet1 = getResultsFromSingleton(1);
        resultSet2 = getResultsFromSingleton(2);

        addResultView(resultSet1,R.id.resultsLayout1, resultSet2);
        addResultView(resultSet2,R.id.resultsLayout2, resultSet1);
    }

    private SearchResultSet getResultsFromSingleton(int resultNumber){
        SearchResultSet searchResultSet = SearchResultsSingleton.getInstance().getSearchResultSet(resultNumber);
        if(searchResultSet == null) {
            finish();
        }
        return searchResultSet;
    }

    private void setupToolbar(){

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar ==null){
            return;
        }
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE );
    }

    private void setupCompareButton(){

        compareButton = findViewById(R.id.compareButton);
        if(compareButton ==null){
            return;
        }
        compareButton.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_select_results, menu);
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

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if(id == R.id.cache_menu_item) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.about_menu_item) {
            new AboutAppDialogue(SelectResultsActivity.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClick(View view){

        if(view.getId() == R.id.compareButton){
            startCompareResultsActivity();
        }
    }

    private void log(String msg){
        Log.i("SelectResultsActivity", msg);
    }

    private boolean containsBoth(ProfileCache profileCache, SearchResult searchResult1, SearchResult searchResult2){
        return profileCache.contains(searchResult1.getUrl()) && profileCache.contains(searchResult2.getUrl());
    }

    private void startCompareResultsActivity(){

        SearchResult result1 = resultSet1.getSelectedSearchResult();
        SearchResult result2 = resultSet2.getSelectedSearchResult();
        if(result1 == null || result2 == null){
            return;
        }


        // We want to check if the network is up and proceed no further if it is down,
        //  unless the parsedProfiles we have selected to compare already exist in the cache.
        //  Then we won't require the network availability to go to the next activity.
        ProfileCache profileCache = new ProfileCache(context);
        if(!containsBoth(profileCache, result1, result2)){
            //profileCache.printCacheList("startCompareResultsActivity");
            log("startCompareActivity() - 1 or more reuslts are not cached... checking network status");
            boolean networkDown = Utils.displayDialogueIfNetworkUnavailable(SelectResultsActivity.this);
            if (networkDown) {
                log("Network down, aborting compare activity initiation.");
                return;
            }
        }

        Intent intent = new Intent(this, CompareResultsActivity.class);
        assignProfileExtras(intent, result1.getName(), result1.getUrl(), result1.usesPlaceHolderImage(), 1);
        assignProfileExtras(intent, result2.getName(), result2.getUrl(), result2.usesPlaceHolderImage(), 2);

        Log.i("CompareButton_onClick()", "parsedProfiles are actors - starting activity...");
        startActivity(intent);

    }


    private void assignProfileExtras(Intent intent, String name, String url, boolean usesProfilePic, int index){
        intent.putExtra("actor" + index + "_name", name);
        intent.putExtra("actor" + index + "_url", url);
        intent.putExtra("usesProfilePic" + index, usesProfilePic);
    }

    // this is called to populate the scroll views with parsedProfiles from the search requests.
    // there's an inbuilt click listener that allows the user to select parsedProfiles.
    private void addResultView(final SearchResultSet resultSet, int insertPointId, final SearchResultSet otherResultSet) {

        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup insertPoint = findViewById(insertPointId);

        if(resultSet == null){
            return;
        }
        List<Integer> keySet = new ArrayList<>(resultSet.getKeySet());

        if(keySet.isEmpty()){
            return;
        }
        Collections.sort(keySet);

        for(int resultId : keySet) {

            SearchResult result = resultSet.getSearchResult(resultId);
            View resultView = vi.inflate(R.layout.query_result, null);
            ImageView imageView = resultView.findViewById(R.id.imageView);
            if (imageView != null) {
                imageView.setImageBitmap(result.getPhoto());
                imageView.setMinimumWidth(100);
            }
            TextView textView = resultView.findViewById(R.id.textView2);
            if (textView != null) {
                textView.setText(result.getName());
            }
            // we are assigning the URL of the result to the view, so that when the view is
            // selected, we will be able to retrieve the URL from it and use it in the comparison
            //resultView.setTag(R.string.type_tag, result.getName());

            // setting a click listener - note a listener is set for every result View within the
            // linearLayout, within the Scroll View

            setOnClickListener(resultView, resultSet, otherResultSet);
            resultView.setId(resultId);
            if (insertPoint != null) {
                insertPoint.addView(resultView, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
    }

    private void setOnClickListener(View resultView, final SearchResultSet resultSet, final SearchResultSet otherResultSet){


        resultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // first we find the sibling view that is currently selected, if any, and deselect it
                // by changing its background colour to normal.
                View parentLayout = (ViewGroup) view.getParent();

                View previouslySelectedView = parentLayout.findViewById(resultSet.getCurrentlySelectedId());
                if (isViewNotCurrentlySelected(previouslySelectedView, view, resultSet)) {
                    previouslySelectedView.setBackgroundColor(ContextCompat.getColor(SelectResultsActivity.this, R.color.unselectedQueryResultBackground)); //R.style.selectedQueryResultBackground));
                    previouslySelectedView.setSelected(false);
                }

                // setting the view as "selected" and changing the background colour.
                view.setSelected(true);
                view.setBackgroundColor(ContextCompat.getColor(SelectResultsActivity.this, R.color.selectedQueryResultBackground));
                //view.setBackgroundColor(getResources().getColor(R.color.background_material_dark));

                // saving the ID of the currently selected view, so we will know to deselect it when
                // another sibling view is selected.
                resultSet.setCurrentlySelectedId(view.getId());

                enableCompareButtonIfBothResultsAreSelected(resultSet,  otherResultSet);
                printCurrentlySelected(resultSet,view.getId());

            }
        });
    }



    private void enableCompareButtonIfBothResultsAreSelected(SearchResultSet resultSet, SearchResultSet otherResultSet){
       if(compareButton == null){
           return;
       }
        if(!compareButton.isEnabled()) {
            // check to see if the other result is selected, and if so, enable the compareTo button
            if ((resultSet.getCurrentlySelectedId() != -1) && (otherResultSet.getCurrentlySelectedId() != -1)) {
                Button compareButton = findViewById(R.id.compareButton);
                if(compareButton == null){
                    return;
                }
                compareButton.setEnabled(true);
            }
        }

    }

    private boolean isViewNotCurrentlySelected(View view, View currentView, SearchResultSet resultSet){
       return  view != null && resultSet.getCurrentlySelectedId() != currentView.getId();
    }

    private void printCurrentlySelected(SearchResultSet searchResultSet, int id) {

        SearchResult result = searchResultSet.getSearchResult(id);
        if(result == null){
            log("printing: result for " + id + "  is null");
            return;
        }
        log("clicked: " + result.getName() + " " + result.getUrl());
    }


}
