package com.jacsstuff.joesfilmfinder.profiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.jacsstuff.joesfilmfinder.DownloadTimeoutException;
import com.jacsstuff.joesfilmfinder.R;
import com.jacsstuff.joesfilmfinder.db.ProfileCache;
import com.jacsstuff.joesfilmfinder.parsers.InlineActorParser;
import com.jacsstuff.joesfilmfinder.parsers.InlineMovieParser;
import com.jacsstuff.joesfilmfinder.parsers.InlineMoviePosterParser;
import com.jacsstuff.joesfilmfinder.parsers.InlineProfileParser;
import com.jacsstuff.joesfilmfinder.parsers.NetUtils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.MOVIE_TAG;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.WEBSITE_URL;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.FULL_CREDITS_URL_PARAM;

/**
 * Created by John on 18/02/2016.
 *
 * Creates a Person or a Movie profile based on the url.
 * Used in the Compare Results Activity.
 * Checks for the profile in the cache, and downloads the page if it isn't there.
 * The parser associated with the profile does the work of extracting the data from the web response.
 */
public class ParsedProfileFactory {


    //private ProfilesCache cache;
    private Context context;
    private ProfileCache profileCache;

    public ParsedProfileFactory(Context context) {
        profileCache = new ProfileCache(context);
        this.context = context;
    }


    private void log(String msg){
        Log.i("ProfileFactory", msg);
    }

    public ParsedProfile generateResult(String name, String url, boolean usesPlaceholderProfilePic) throws DownloadTimeoutException, IOException {

        if (profileCache.contains(url)) {
            Class type = url.contains(MOVIE_TAG)? MovieProfile.class : ActorProfile.class;
            Log.i("generateResult", "cache contains url: " + url);
            return profileCache.getProfile(url, type);
        }

        InlineProfileParser parser;
        ParsedProfile profile;
        byte[] profilePic;

        if (url.contains(MOVIE_TAG)) {
            parser = new InlineMovieParser(WEBSITE_URL);
            log("ParsedProfileFactory - about to download Url " + url + "  with inlineMovieParser.");
            NetUtils.downloadAndParseWebpage(createFullCreditsUrl(url), parser);
            profile = new MovieProfile(name, url, parser.getResults(), getImagePoster(url, usesPlaceholderProfilePic));
        }
        else{
            parser = new InlineActorParser(WEBSITE_URL);
            log("ParsedProfileFactory - about to download Url " + url + "  with inlineActorParser.");
            NetUtils.downloadAndParseWebpage(url, parser);
            if(usesPlaceholderProfilePic || !NetUtils.isValidUrl(parser.getImageUrl())){
                profilePic = getPlaceholderProfilePic(R.drawable.default_profile_pic);
            }
            else profilePic = NetUtils.downloadImageByteArray(parser.getImageUrl());
            profile = new ActorProfile(name, url, parser.getResults(), profilePic);
        }

        if (parser.hasNoResults()) {
            log("parser has no results");
            return null;
        }

        profileCache.add(profile);
        return profile;
    }

    private String createFullCreditsUrl(String address){
        if(address == null){
            return null;
        }
        int lastSlashIndex = address.lastIndexOf("/");
        if(lastSlashIndex == -1){
            return null;
        }
        return address.substring(0, lastSlashIndex + 1) + FULL_CREDITS_URL_PARAM;
    }

    private byte[] getImagePoster(String profileUrl, boolean usesPlaceholderProfilePic) throws DownloadTimeoutException, IOException {
        if(usesPlaceholderProfilePic || !isValidUrl(profileUrl)){
            return getPlaceholderProfilePic(R.drawable.default_profile_pic); //R.drawable.movie_thumbnail_placeholder1
        }
        InlineMoviePosterParser inlineMoviePosterParser = new InlineMoviePosterParser();
        log("Parsed Profile Factory -  about to download and parse movie poster URL ");
        NetUtils.downloadAndParseWebpage(profileUrl, inlineMoviePosterParser);
        String posterImageUrl = inlineMoviePosterParser.getImageUrl();
        if(!isValidUrl(posterImageUrl)) {
            return getPlaceholderProfilePic(R.drawable.default_profile_pic);
        }
        return NetUtils.downloadImageByteArray(posterImageUrl);
    }

    private boolean isValidUrl(String address){
        try{
            new URL(address);
            return true;
        }
        catch(MalformedURLException e){
            log("url not valid: "+ address);
        }
        return false;
    }

    private byte[] getPlaceholderProfilePic(int resourceId){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

}

