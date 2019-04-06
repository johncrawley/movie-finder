package com.jacsstuff.joesfilmfinder;

/**
 * Created by John on 05/12/2017.
 * Represents the actions that can be called by the main controller
 *  when updating the mainActivity
 */
public interface SearchingView {

    void dismissSearchingDialog();
    void showNoResultsText1();
    void showNoResultsText2();
    void createServerUnavailableText();
    void startSelectResultsActivity();
}
