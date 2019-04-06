package com.jacsstuff.joesfilmfinder.profiles;

import android.util.Log;
import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.io.Serializable;
import java.util.List;

/**
 * Contains the image and list of people-links for a particular movie
 * Also uses a movie parser, to derive this data from the webapage provided at initialisation,
 *  and a MovieProfileComparator, used when sorting a collection of MovieProfiles.
 */
public class MovieProfile extends AbstractProfile implements ParsedProfile {

    // REQUIRED and called indirectly by cache when loading a movie profile
    public MovieProfile(){
        super();
    }

    MovieProfile(String name, String url, List<ResultLink> resultLinks, byte[] imageBytes){
        super(name, url, resultLinks, imageBytes);
    }
}