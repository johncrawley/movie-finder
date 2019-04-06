package com.jacsstuff.joesfilmfinder.activities;

import android.graphics.Bitmap;

import com.jacsstuff.joesfilmfinder.R;
import com.jacsstuff.joesfilmfinder.results.ComparisonResultSet;
import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.util.List;

/**
 * Created by John on 01/04/2018.
 * List of operations expected by the CompareResults Controller
 *
 */
public interface CompareResultsView {

    void setComparisonResults(ComparisonResultSet comparisonResults);
    void setProfilePic(int index,Bitmap bitmap, String url);
    void setProfileTitle(String title, int index);
    void showNoResultsFound();
    void showActorMovieComparison(String actorName, String roles);
    void setTitleText(int numberOfResultsFound);
    void showHiddenViews();
}
