package com.jacsstuff.joesfilmfinder.preferences;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.jacsstuff.joesfilmfinder.R;

/*

        Simple activity that loads the Settings fragment and is accessed through the options menu in the app bar of the main 3 activities.
 */

public class PreferencesActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_preferences);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE );
        }
        getFragmentManager().beginTransaction().replace(R.id.preference_list_wrapper, new SettingsFragment()).commit();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onFragmentInteraction(Uri uri){
        Log.i("PreferencesActivity", "onFragmentInteraction(), uri: " + uri);
    }

}
