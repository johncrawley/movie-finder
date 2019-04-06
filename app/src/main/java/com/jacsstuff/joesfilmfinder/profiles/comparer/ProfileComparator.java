package com.jacsstuff.joesfilmfinder.profiles.comparer;

import com.jacsstuff.joesfilmfinder.profiles.ParsedProfile;
import com.jacsstuff.joesfilmfinder.results.ComparisonResultSet;

/**
 * Created by John on 21/03/2016.
 * This interface is used by the Actor and Movie Profile Comparators, which are used to sort
 * lists of profiles.
 */
public interface ProfileComparator {
    ComparisonResultSet compare(ParsedProfile p1, ParsedProfile p2);
}