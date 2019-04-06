package com.jacsstuff.joesfilmfinder.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.jacsstuff.joesfilmfinder.controller.CompareResultsController;
import com.jacsstuff.joesfilmfinder.preferences.AboutAppDialogue;
import com.jacsstuff.joesfilmfinder.preferences.PreferencesActivity;
import com.jacsstuff.joesfilmfinder.profiles.GeneralProfile;
import com.jacsstuff.joesfilmfinder.results.ComparisonResultSet;
import com.jacsstuff.joesfilmfinder.R;
import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

//import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.HTTP_PREFIX;
/*
    This activity class runs the comparison between the two parsedProfiles selected in the SelectResultsActivity
    and displays the comparison parsedProfiles to the user.

 */

public class CompareResultsActivity extends AppCompatActivity implements View.OnClickListener, CompareResultsView{

    private ProgressDialog mDialog;
    private ComparisonResultSet comparisonResults;
    private List<GeneralProfile> profiles;
    private CompareResultsController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_results);
        setupToolbar();
        setupNewSearchButton();
        setupLoadingDialog();
        controller = new CompareResultsController(this, CompareResultsActivity.this);

        assignDataFromIntents();
        new AsyncWebpageDownloaderTask(this, controller).execute();
    }

    private void assignDataFromIntents(){
        profiles = new ArrayList<>();
        addProfile("actor1_name", "actor1_url", "usesProfilePic1");
        addProfile("actor2_name", "actor2_url", "usesProfilePic2");
    }

    private void addProfile(String nameKey, String urlKey, String usesProfilePicKey){
        Intent intent = getIntent();
        String profileUrl = intent.getStringExtra(urlKey);
        String profileName = intent.getStringExtra(nameKey);
        boolean usesProfilePic = intent.getBooleanExtra(usesProfilePicKey, true);
        profiles.add(new GeneralProfile(profileName, profileUrl, usesProfilePic));
    }

    private void setupToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar == null){
            return;
        }

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE );
    }

    private void setupLoadingDialog(){
        mDialog = new ProgressDialog(CompareResultsActivity.this);
        mDialog.setMessage(getResources().getString(R.string.loading_results_dialogue));
        mDialog.setCancelable(false);
        mDialog.show();
    }

    private void setupNewSearchButton(){
        View newSearchButton = findViewById(R.id.newSearchButton);
        if(newSearchButton == null){
            return;
        }
        newSearchButton.setOnClickListener(this);
    }

    /*
        This is used to display a server-timeout message in a dialog and return the user back to the
        previous activity. Would have liked to use the method from the Utils class but unable to
        finish() this activity from a call written there.
     */
    public void createFinishDialog( int stringID){
        Context context = CompareResultsActivity.this;
        String message = context.getResources().getString(stringID);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage(message);

        alertBuilder.setNeutralButton(context.getResources().getString(R.string.cannot_connect_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        AlertDialog dialog = alertBuilder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //log("finish() to be called on activity");
                finish();
            }
        });

        dialog.show();
    }


    //private void log(String msg){
    //    Log.i("CompareResultsActivity", msg);
    //}


    private void log(String msg){
        Log.i("CompareResultsActivity", msg);
    }

    public void onClick(View view){
        if(view.getId() == R.id.newSearchButton){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);

        }
    }

    // Used to launch an actor or movie profile in the web view when the result is clicked on by the user
    private void startWebActivity(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.menu_compare_results, menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
                onBackPressed();
                return true;
        }
        if(id == R.id.cache_menu_item) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
        }
        if (id == R.id.about_menu_item) {
            new AboutAppDialogue(CompareResultsActivity.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setComparisonResults(ComparisonResultSet comparisonResults){
        this.comparisonResults = comparisonResults;
    }

    public void setProfilePic(Bitmap bitmap, final String url, int profilePicViewId){

        ImageView profilePicImageView = (ImageView) findViewById(profilePicViewId);
        if(profilePicImageView == null){
            log("ui elements cannot be found, returning from postExecute()");
            return;
        }
        profilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWebActivity(url);
            }
        });
        if (bitmap != null) {
            profilePicImageView.setImageBitmap(bitmap);
        }

    }


    public void setProfileTitle(final String title, final int index){
        this.runOnUiThread(new Runnable(){
            public void run() {
                int[] titleViews = new int[]{R.id.actor1Name, R.id.actor2Name};
                setTextOnView(titleViews[index], title);
            }
        });
    }


    private void setTextOnView(int id, String text){
        TextView textView = (TextView) findViewById(id);
        if(textView != null){
            textView.setText(text);
        }
    }

    public void showHiddenViews(){

        showView(R.id.newSearchButton);
        showView(R.id.results_layout);
    }

    private void showView(int id){
        View view = findViewById(id);
        if(view != null){
            view.setVisibility(View.VISIBLE);
        }
    }

    public void setProfilePic(final int index, final Bitmap bitmap, final String url){

        this.runOnUiThread(new Runnable(){
            public void run() {
                int[] viewIds = new int[]{R.id.imageViewActor1, R.id.imageViewActor2};
                setProfilePic(bitmap, url, viewIds[index]);
            }
        });
    }
    public void addResultView(List<ResultLink> resultLinks){
        addResultView(resultLinks, R.id.commonMovieResultsLayout);
    }


    public void showNoResultsFound(){
        this.runOnUiThread(new Runnable(){
            public void run() {
                showComparisonText(getResources().getString(R.string.no_results_found));
            }
        });
    }

    public void setTitleText(final int numberOfResultsFound){
        this.runOnUiThread(new Runnable(){
            public void run(){
                ActionBar actionBar = getSupportActionBar();
                if(actionBar == null){
                    return;
                }
                int resultTextId = R.string.toolbar_result_found;
                if(numberOfResultsFound > 1){
                    resultTextId = R.string.toolbar_results_found;
                }
                actionBar.setTitle(numberOfResultsFound + " " + getResources().getString(resultTextId));
            }
        });
    }

    public void showActorMovieComparison(final String actorName, final String roles){
        this.runOnUiThread(new Runnable(){
            public void run() {
                String msg = actorName + getResources().getString(R.string.worked_on) + roles;
                showComparisonText(msg);
            }});
    }

    private void showComparisonText(final String message){
        this.runOnUiThread(new Runnable(){
            public void run() {

                TextView resultCountTextView = (TextView) findViewById(R.id.results_count_text);
                if(resultCountTextView == null){
                    log("result count view cannot be found, returning from postExecute()");
                    return;
                }
                resultCountTextView.setVisibility(View.VISIBLE);
                resultCountTextView.setText(message);
            }});
    }

    private void addResultView(final List<ResultLink> resultsList, final int insertPointId) {

        this.runOnUiThread(new Runnable(){
            public void run() {
                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup insertPoint = findViewById(insertPointId);
                int rowCount = 0; //used to select the background color for the row

                for (final ResultLink resultLink : resultsList) {

                    View resultView = vi.inflate(R.layout.movie_comparison, null);
                    TextView rolesView1 = resultView.findViewById(R.id.textView1);
                    TextView nameLinkView =  resultView.findViewById(R.id.textView2);
                    TextView rolesView2 = resultView.findViewById(R.id.textView3);

                    rolesView1.setText(resultLink.getRolesAsString());
                    nameLinkView.setText(resultLink.getNameAndYear());
                    rolesView2.setText(resultLink.getSecondActorRolesAsString());

                    resultView.setBackgroundColor(ContextCompat.getColor(CompareResultsActivity.this, getRowColor(rowCount)));
                    assignUrlToTextView(nameLinkView, resultLink.getUrl());
                    rowCount++;

                    if(insertPoint == null){
                        return;
                    }
                    insertPoint.addView(resultView, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }});
    }

    private int getRowColor(int count){
        if(count % 2 == 0)return R.color.compareRowEven;
        return R.color.compareRowOdd;
    }

    private void assignUrlToTextView(TextView textView, final String url){

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWebActivity(url);
            }
        });
    }

    public void dismissLoadingDialog(){

        mDialog.dismiss();
    }

    // This task downloads and parses the two webpages that correspond to the parsedProfiles selected by the
    // user. Once parsed, the two profiles are compared and a Comparison Result Set holds the common
    // people or movies that the two profiles have in common.
    public static class AsyncWebpageDownloaderTask extends AsyncTask<String, Void, Integer> {


        private WeakReference<CompareResultsActivity> activityReference;
        private WeakReference<CompareResultsController> controllerReference;



        private AsyncWebpageDownloaderTask(CompareResultsActivity activity, CompareResultsController controller) {
            activityReference = new WeakReference<>(activity);
            controllerReference = new WeakReference<>(controller);
        }

        protected Integer doInBackground(String... params){
            CompareResultsActivity activity = activityReference.get();
            CompareResultsController controller = controllerReference.get();
            controller.executeProfileSearches(activity.profiles);
            return 1;
        }

        protected void onProgressUpdate(Void... params) {

        }

        protected void onPostExecute(Integer result) {

            CompareResultsActivity activity = activityReference.get();
            CompareResultsController controller = controllerReference.get();
            activity.dismissLoadingDialog();
            if(controller.hasDownloadTimedOut()){
                activity.createFinishDialog(R.string.server_unavailable);
                return;
            }

            if(!activity.comparisonResults.isActorMovieComparison()) {
                activity.addResultView(activity.comparisonResults.getResultLinks(), R.id.commonMovieResultsLayout);
            }
            activity.showHiddenViews();
        }
    }


}
