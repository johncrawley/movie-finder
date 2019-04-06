package com.jacsstuff.joesfilmfinder.results;

import java.util.ArrayList;

/**
 * Created by John on 03/04/2018.
 * An extension of the Comparison Result Set that gets created by the compare controller
 *  if one or more of the profiles can't be downloaded.
 */
public final class EmptyComparisonResultsSet extends ComparisonResultSet {

    public EmptyComparisonResultsSet(){
        this.resultLinks = new ArrayList<>(0);
        this.isActorMovieComparison = false;
    }
}
