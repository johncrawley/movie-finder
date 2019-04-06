package com.jacsstuff.joesfilmfinder.profiles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.jacsstuff.joesfilmfinder.Utils;
import com.jacsstuff.joesfilmfinder.profiles.comparer.ProfileComparator;
import com.jacsstuff.joesfilmfinder.profiles.comparer.ProfileComparatorImpl;
import com.jacsstuff.joesfilmfinder.results.ComparisonResultSet;
import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 17/02/2016.
 * The super class profile from which the Actor Profile and the Movie Profile are derived.
 */
public abstract class AbstractProfile implements ParsedProfile {

    protected String url;
    private byte[] profilePicByteArray;
    protected String name;
    private List <ResultLink> resultLinks;
    private String resultText;
    private transient ProfileComparator profileComparator;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<ResultLink> getResultLinks() {
        return resultLinks;
    }
    public void setResultLinks(List<ResultLink> resultLinks) {
        this.resultLinks = resultLinks;
    }
    public void setResultText(String msg){ this.resultText = msg; }
    public String getResultText(){
        return this.resultText;
    }

    public void setImageBytes(byte[] imageBytes){
        this.profilePicByteArray = imageBytes.clone();
    }

    AbstractProfile(String name,String url, List<ResultLink> resultLinks, byte[] profilePic){
        this.profileComparator = new ProfileComparatorImpl();
        this.name = name;
        this.url = url;
        this.resultLinks = new ArrayList<>(resultLinks);
        this.profilePicByteArray = profilePic;
    }

    AbstractProfile(){
        this.profileComparator = new ProfileComparatorImpl();
        this.name = "";
        this.url = "";
        this.resultLinks = new ArrayList<>(0);
    }


    public Bitmap getProfileBitmap() {
        if(profilePicByteArray != null) {
            return BitmapFactory.decodeByteArray(profilePicByteArray, 0, profilePicByteArray.length);
        }
        else return null;
    }

    public byte[] getProfilePicBytes(){
        return this.profilePicByteArray;
    }


    public String getKey(){ return Utils.getKey(this.url);}

    public String getKey(String inputUrl){
        return Utils.getKey(inputUrl);
    }

    public void log(String msg){
        Log.i("AbstractProfile", msg);}


    public ComparisonResultSet compareTo(ParsedProfile otherProfile){
        return otherProfile != null ? profileComparator.compare(this,otherProfile) : new ComparisonResultSet(false);
    }

    public String toString(){
            StringBuilder str = new StringBuilder("Profile name: " + getName()+"\n");

            Log.i("ParsedProfile", "**********************************");
            for (ResultLink link : getResultLinks()) {

                str.append("Name: ");
                str.append(link.getName());
                str.append("\n ID: ");
                str.append(link.getId());
                if(link.getYear() != null){
                    str.append( "\n Year: ");
                    str.append(link.getYear());
                }
                if(link.getRolesAsString() != null){
                    str.append(" Roles: ");
                    str.append(link.getRolesAsString());
                    str.append("\n");
                }
            }
            str.append("\n**********************************");
            return str.toString();
    }
}
