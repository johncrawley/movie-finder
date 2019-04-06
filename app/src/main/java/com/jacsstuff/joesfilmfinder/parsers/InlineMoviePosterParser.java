package com.jacsstuff.joesfilmfinder.parsers;

import android.util.Log;

import com.jacsstuff.joesfilmfinder.Utils;
import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.util.List;

/**
 * Created by John on 27/09/2016.
 *
 * Also contains a method to retrieve the profile "poster" pic for the movie.
 * The web page that contains the full cast is different than the one that contains the large profile pic.
 */
public class InlineMoviePosterParser implements InlineParser {

    private boolean isFinishedParsing = false;

    private String imageUrl = null;
    private final String MOVIE_POSTER_CLASS_TAG = "class=\"poster\"";
    private final String MOVIE_IMAGE_URL_TAG_START = "src=\"";
    private final String MOVIE_IMAGE_URL_TAG_END = "\"";

    final String SIDEBAR_TAG = "<div id=\"sidebar\">";
    String mode = "find poster class tag";


    public boolean parseLine(String line) {
        if (mode.equals("find poster class tag")) {
            if (line.contains(MOVIE_POSTER_CLASS_TAG)) {
                mode = "get url";
                Log.i("InlinePosterParser", "found the poster class tag");
            }
        }
        else if (mode.equals("get url")) {

            if (line.contains(MOVIE_IMAGE_URL_TAG_START)) {

                Log.i("InlineMoviePosterParser", "Image start tag located");
                String url = Utils.parseString(line, MOVIE_IMAGE_URL_TAG_START, MOVIE_IMAGE_URL_TAG_END, false);
                if (!url.equals("")) {
                    this.imageUrl = url;
                    isFinishedParsing = true;
                    Log.i("InlineMoviePosterParser", "url discovered:" + url);
                }
            }
        }
        if(line.contains(SIDEBAR_TAG)){
            isFinishedParsing = true;
            Log.i("InlineMoviePosterParser", "Sidebar tag located");
        }
        return isFinishedParsing;
    }


    public boolean hasNoResults(){
        return this.imageUrl == null;
    }

    public List<ResultLink> getResults(){
        return null;
    }

    public String getImageUrl(){
        return this.imageUrl;
    }
}