package com.jacsstuff.joesfilmfinder.profiles;

import android.graphics.Bitmap;

import com.jacsstuff.joesfilmfinder.results.ComparisonResultSet;
import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.util.List;

/**
 * Created by John on 09/02/2016.
 */
public interface ParsedProfile {
    Bitmap getProfileBitmap();
    List<ResultLink> getResultLinks();
    String getName();
    String getUrl();
    ComparisonResultSet compareTo(ParsedProfile parsedProfile);
    String getKey();
    String toString();
    byte[] getProfilePicBytes();
}
