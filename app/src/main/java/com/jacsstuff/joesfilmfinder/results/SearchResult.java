package com.jacsstuff.joesfilmfinder.results;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.URL_TITLE_IDENTIFIER;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.URL_NAME_IDENTIFIER;

/**
 * Created by John on 25/08/2015.
 *  Contains the data for a single parsed result retrieved from a search response from the server.
 *  This object stores data for movie results as well as people results.
 *
 *  It implements Parcelable  and includes the methods readFromParcel() and writeToParcel, because
 *  originally these search results would be passed from the first activity to the second via an Intent.
 *  Leaving that old code in here for instructional purposes.
 */
public class SearchResult implements Parcelable, Comparable<SearchResult> {

    private String name;
    private String url;
    private Bitmap photo;
    private String type;
    private boolean usesPlaceholderImage; // if a placeholder image is found in the initial search results, we don't need to download a profile pic for the profile.

    public SearchResult(Parcel in){
        readFromParcel(in);
    }

    public SearchResult(){

    }
    public SearchResult(String name, String url, byte[] imageByteArray){
        this.name = name;
        this.url = url;
        photo = BitmapFactory.decodeByteArray(imageByteArray,0,imageByteArray.length);
        setType(url);
    }


    public SearchResult(String name, String url, Bitmap imageBitmap, boolean usesPlaceholderImage){
        this.name = name;
        this.url = url;
        photo = imageBitmap;
        this.usesPlaceholderImage = usesPlaceholderImage;
        setType(url);
    }

    public void setPhotoBytes(byte[] photoBytes){
        this.photo = BitmapFactory.decodeByteArray(photoBytes, 0 , photoBytes.length);

    }

    public void setUrl(String url){
        this.url = url;
    }

    private void setType(String url){
        if(url.contains(URL_NAME_IDENTIFIER)){
            type = URL_NAME_IDENTIFIER;
        }
        else if(url.contains(URL_TITLE_IDENTIFIER)){
            type = URL_TITLE_IDENTIFIER;
        }
    }

    private void readFromParcel(Parcel in){
        name = in.readString();
        url = in.readString();
        type = in.readString();
        photo = in.readParcelable(Bitmap.class.getClassLoader());
    }

    // this sort makes the people search reuslts appear first in a list
    // and the titles come after
    public int compareTo(SearchResult otherResult){
        if(this.type.equals(URL_NAME_IDENTIFIER)) {
            if (!otherResult.type.equals(URL_NAME_IDENTIFIER)) {
                return -1;
            }
        }
        else{
            if(otherResult.type.equals(URL_NAME_IDENTIFIER)){
                return 1;
            }
        }
        return 0;
    }

    public int describeContents(){
        return 0;
    }

    public boolean usesPlaceHolderImage(){
        return this.usesPlaceholderImage;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public SearchResult createFromParcel(Parcel in){
            return new SearchResult(in);
        }

        public SearchResult[] newArray(int size){
            return new SearchResult[size];
        }
    };

    public void writeToParcel(Parcel out, int flags){
        out.writeString(name);
        out.writeString(url);
        out.writeString(type);
        out.writeParcelable(photo,0);
    }

    public String getName(){
        return name;
    }

    public String getUrl(){
        return this.url;
    }

    public byte[] getPhotoBytes(){

        return getBitmapBytes(photo);
    }

    private byte[] getBitmapBytes(Bitmap bitmap){

        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(byteBuffer);
        return byteBuffer.array();
    }

    private byte[] compressBitmap(Bitmap bmp){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(bmp != null) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        }

        return stream.toByteArray();
    }

    public Bitmap getPhoto(){return photo;}
    public void setName(String newName){ name = newName;}
}