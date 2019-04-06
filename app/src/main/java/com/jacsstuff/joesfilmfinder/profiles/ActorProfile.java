package com.jacsstuff.joesfilmfinder.profiles;
import android.util.Log;
import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.io.Serializable;
import java.util.List;

/**
 * Created by John on 08/12/2015.
 *  Represents the profile of a person, be it, actor, director, cinematographer etc.
 *  Implements an Actor Profile Comparator, used to sort profiles in a collection.
 *  Also implements an Actor Profile Parser, to extract data from a downloaded webpage.
 *   This is done in the constructor.
 *
 */
public class ActorProfile extends AbstractProfile implements ParsedProfile {

    private final String NAME_TAG = "/name/";

    // REQUIRED: used by cache - it creates a new instance when loading a cached profile.
    public ActorProfile(){
        super();
    }

    ActorProfile(String name, String url, List<ResultLink> resultLinks, byte[] profilePic){
        super(name,url,resultLinks,profilePic);
    }

    public String getId(){
        return this.url.contains(NAME_TAG) ? url.substring(url.indexOf(NAME_TAG)) : url;
    }

}
