package com.jacsstuff.joesfilmfinder.parsers;

import android.util.Log;

import com.jacsstuff.joesfilmfinder.Utils;
import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.END_OF_SECTION;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.FULL_CREDITS_CONTENT_TAG;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.URL_START_TAG;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.URL_END_TAG;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.NAME_START_TAG;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.NAME_END_TAG;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.NON_ACTOR_NAME_START_TAG;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.NON_ACTOR_NAME_END_TAG;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.CHARACTER_TAG;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.URL_NAME_FRAGMENT;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.NEW_ROLE_TAG;

/**
 * Created by John on 27/09/2016.
 *
 * This is a parser that is passed into the NetUtils.downloadPage() method and the parseLine
 * method is called on every line downloaded from the webpage.
 *
 */
public class InlineMovieParser implements InlineProfileParser {


    private boolean isFinishedParsing;

    private final Map<String, String> roles;
    private String role = "";
    private mode currentMode = mode.LOOKING_FOR_CREDITS;
    private String actorUrl = "";
    private String actorName;
    private Map<String, ResultLink> parsedResults;
    private final String ACTOR_TAG = "Actor";
    private String siteUrl;

    public InlineMovieParser(String siteUrl) {
        parsedResults = new HashMap<>();
        roles = new HashMap<>();
        roles.put("Directed by", "Director");
        roles.put("Cast", ACTOR_TAG);
        roles.put("Produced by", "Producer");
        roles.put("Cinematography", "Cinematographer");
        roles.put("Writing Credits", "Writer");

        this.isFinishedParsing = false;
        this.actorName = "";
        this.siteUrl = siteUrl;
    }

    private enum mode { LOOKING_FOR_CREDITS, CHANGE_ROLE, GET_ACTOR_URL, GET_ACTOR_NAME, NULL}

    public boolean parseLine(String line){

        finishParsingIfEndOfSection(line);
        if(skipIfCreditTagNotFound(line) || line.trim().isEmpty()){
            return isFinishedParsing;
        }
        parseForRole(line);
        parseForUrl(line);
        parseForName(line);
        addResultLinkIfInfoPresent();

        return isFinishedParsing;
    }


    private void addResultLinkIfInfoPresent(){
        if(!currentMode.equals(mode.GET_ACTOR_NAME) || actorName.isEmpty()) {
            return;
        }

        if (parsedResults.containsKey(actorUrl)) {
            parsedResults.get(actorUrl).addRole(role);
        }
        else {
           parsedResults.put(actorUrl, new ResultLink(actorName, this.siteUrl + actorUrl, role));
        }
        currentMode = mode.GET_ACTOR_URL;
    }

    private void finishParsingIfEndOfSection(String line){
        if(line.contains(END_OF_SECTION)){
            isFinishedParsing = true;
        }
    }

    private boolean skipIfCreditTagNotFound(String line){
        if(currentMode.equals(mode.LOOKING_FOR_CREDITS)){
            if(line.contains(FULL_CREDITS_CONTENT_TAG)){
                currentMode = mode.NULL;
            }
            else return true;
        }
        return false;
    }

    private void parseForRole(String line){
        if(line.contains(NEW_ROLE_TAG)){
            currentMode = mode.CHANGE_ROLE;
        }

        for(String key : roles.keySet()) {
            if ((line.contains(key) && (currentMode.equals(mode.CHANGE_ROLE)))) {
                role = roles.get(key);
                currentMode = mode.GET_ACTOR_URL;
            }
        }
    }

    private void parseForUrl(String line){
        if((currentMode.equals(mode.GET_ACTOR_URL)) && ( line.contains(URL_NAME_FRAGMENT))) {
            actorUrl = Utils.getSubstring(line, URL_START_TAG, URL_END_TAG);
            actorUrl = removePathQueryIfPresent(actorUrl);
            if ((!actorUrl.isEmpty()) && (!actorUrl.contains(CHARACTER_TAG))){
                currentMode = mode.GET_ACTOR_NAME;
                log("found url : " + actorUrl);
            }
        }

    }

    private void parseForName(String line){
        if(currentMode.equals(mode.GET_ACTOR_NAME)) {
            actorName = Utils.getSubstring(line, NAME_START_TAG, NAME_END_TAG);

            if (actorName.isEmpty() && !role.equals(ACTOR_TAG)) {
                actorName = Utils.getSubstring(line, NON_ACTOR_NAME_START_TAG, NON_ACTOR_NAME_END_TAG);
            }
            if(!actorName.isEmpty()){
                log("Found actor name: "+  actorName);
            }
        }
    }


    private String removePathQueryIfPresent(String url){
        final String QUERY_SYMBOL = "?";
        if(url.contains(QUERY_SYMBOL)){
            url = url.substring(0, url.indexOf(QUERY_SYMBOL));
        }
        return url;
    }

    private void log(String msg){
        Log.i("InlineMovieParser", msg);
    }
    public List<ResultLink> getResults(){
        return new ArrayList<>(parsedResults.values());
    }

    public String getImageUrl(){
        return "";
    }

    public boolean hasNoResults(){
        return this.parsedResults.size() == 0;
    }
}

