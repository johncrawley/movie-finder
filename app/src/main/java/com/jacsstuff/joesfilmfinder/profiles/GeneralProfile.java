package com.jacsstuff.joesfilmfinder.profiles;

/**
 * Created by John on 31/03/2018.
 *
 * Used for storing the profile name, url and whether it uses a placeholder profile pic or not
 * we don't care if it's an actor or movie profile.
 *
 * Used by the compareResultsActivity to clean up the instance variables there
 */
public class GeneralProfile {

    private String name, url;
    private boolean usesDefaultProfilePic;

    public GeneralProfile(String name, String url, boolean usesDefaultProfilePic){
        this.name = name;
        this.url = url;
        this.usesDefaultProfilePic = usesDefaultProfilePic;
    }

    public String getName(){return this.name;}
    public String getUrl(){return this.url;}
    public boolean usesDefaultProfilePic(){return this.usesDefaultProfilePic;}
}
