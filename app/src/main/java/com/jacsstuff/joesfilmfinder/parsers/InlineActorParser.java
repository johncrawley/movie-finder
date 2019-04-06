package com.jacsstuff.joesfilmfinder.parsers;

import android.util.Log;

import com.jacsstuff.joesfilmfinder.Utils;
import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by John on 27/09/2016.
 * Gets passed into the downloadWebpage method in NetUtils as a functor. The parseLine() method gets called
 * once for every line downloaded from the webpage, and in doing so builds up a profiles of the actors, variable by variable.
 */
public class InlineActorParser implements InlineProfileParser {

    private final String FILMOGRAPHY_HEADING = "<h2>Filmography</h2>";
    private final String MOVIE_LIST_START_TAG = "<div class=\"filmo-category-section\"";
    private final String MOVIE_URL_START_TAG = "<b><a href=\"";
    private final String MOVIE_URL_END_TAG = "/?ref";
    private final String MOVIE_NAME_START_TAG = ">";
    private final String MOVIE_NAME_END_TAG = "</a></b>";
    private final String MOVIE_YEAR_START_TAG = "&nbsp;";
    private final String BREAK_TAG = "<br/>";
    private final String CHARACTER_URL_START_TAG = "<a href=\"/character/";
    private final String CHARACTER_URL_END_TAG = "\"";
    private final String CHARACTER_NAME_START_TAG = ">";
    private final String CHARACTER_NAME_END_TAG = "</a>";
    private final String ACTOR_TAG = "a name=\"actor\"";
    private final String ACTRESS_TAG = "a name=\"actress\"";
    private final String DIRECTOR_TAG = "a name=\"director\"";
    private final String PRODUCER_TAG = "a name=\"producer\"";
    private final String SOUNDTRACK_TAG = "a name=\"soundtrack\"";
    private final String WRITER_TAG = "a name=\"writer\"";
    private final String CINEMATOGRAPHER_TAG="a name=\"cinematographer\"";
    private final String ROLE_TAG="<a name=\"";
    private final String PROFILE_PIC_START_TAG = "<img id=\"name-poster\"";

    private Map<String,ResultLink> resultLinksMap;
    private SearchMode searchMode = SearchMode.PROFILE_PIC_START;
    private String year = "";
    private String movieName = "";
    private String movieUrl =  "";
    private String characterName = "";
    private String characterURL = "";
    private String role = "";

    private String imageUrl = null;
    private String siteUrl;
    private final String IMAGE_URL_START = "src=\"";
    private final String IMAGE_URL_END = ".jpg";
    private final String IMAGE_URL_PREFIX = "";


    private boolean hasfinishedParsing = false;
    private HashMap<String, String> roles;

    public InlineActorParser(String siteUrl) {
        this.siteUrl = siteUrl;
        resultLinksMap = new HashMap<>();
        roles = new HashMap<>();
        roles.put(ACTOR_TAG, "Actor");
        roles.put(DIRECTOR_TAG, "Director");
        roles.put(WRITER_TAG, "Writer");
        roles.put(CINEMATOGRAPHER_TAG, "Cinematographer");
        roles.put(ACTRESS_TAG, "Actor");
        roles.put(PRODUCER_TAG, "Producer");
        roles.put(SOUNDTRACK_TAG, "Soundtrack");

    }

    private void log(String msg){

        Log.i("InlineActorP", msg);
    }

    private void parseImageUrl(String line){
        int startIndex = line.indexOf(IMAGE_URL_START);
        int endIndex = line.indexOf(IMAGE_URL_END);

        boolean isImageUrlFound = (startIndex != -1) && (endIndex != -1);
        if (isImageUrlFound) {
            startIndex = startIndex + IMAGE_URL_START.length();
            endIndex = endIndex + IMAGE_URL_END.length();
            imageUrl = line.substring(startIndex, endIndex);
            log("image has been found, url = " + imageUrl);
        }

        if (imageUrl != null) {
            imageUrl = IMAGE_URL_PREFIX + imageUrl;
        }
    }

    public boolean parseLine(String line){
        if(searchMode.equals(SearchMode.PROFILE_PIC_START)){
            if(line.contains(PROFILE_PIC_START_TAG)){
                searchMode = SearchMode.PROFILE_PIC;
                log("parseLine: start tag found, searching for profile pic");
            }
            return false;
        }
        if(searchMode.equals(SearchMode.PROFILE_PIC)) {
            parseImageUrl(line);
            if (imageUrl != null) {
                searchMode = SearchMode.FILMOGRAPHY_HEADING;
            }
        }
        if(searchMode.equals(SearchMode.FILMOGRAPHY_HEADING)){
            if(!line.contains(FILMOGRAPHY_HEADING)){
                return hasfinishedParsing;
            }
            searchMode = SearchMode.MOVIE_LIST;
        }
        if ((searchMode.equals(SearchMode.MOVIE_LIST)) && (line.contains(MOVIE_LIST_START_TAG))) {
            searchMode = SearchMode.MOVIE_YEAR;
        }
        // only try to assign a role if the current line contains the role tag
        // we won't add a ResultLink unless a role has been found
        if(line.contains(ROLE_TAG)) {
            role = null;
            for (String key : roles.keySet()) {
                if (line.contains(key)) {
                    role = roles.get(key);
                    break;
                }
            }
        }
        if ((searchMode.equals(SearchMode.MOVIE_YEAR)) && (line.contains(MOVIE_YEAR_START_TAG)) && (role != null)) {
            year = Utils.parseString(line, MOVIE_YEAR_START_TAG);

            // only proceeding if we have a year to work with
            if(!year.isEmpty()){
                // sometimes the year has extra characters on the end of it, we want to get rid of these characters here.
                if(year.length() > 4) {
                    year = year.substring(0, 4);
                }
                // if the year string exists and begins with a 1 or a 2 i.e. 1900's to 2999
                // unfortunately this app does not support movies made outside of those dates.
                if ((year.startsWith("1") || year.startsWith("2"))) {
                    searchMode = SearchMode.MOVIE_URL;
                }
            }
        }
        else if (searchMode.equals(SearchMode.MOVIE_URL)) {
            movieUrl = Utils.parseString(line, MOVIE_URL_START_TAG, MOVIE_URL_END_TAG);
            if (!movieUrl.isEmpty()) {
                searchMode = SearchMode.MOVIE_NAME;
            }
        }
        else if (searchMode.equals(SearchMode.MOVIE_NAME)) {
            movieName = Utils.parseString(line, MOVIE_NAME_START_TAG, MOVIE_NAME_END_TAG);
            if (!movieName.isEmpty()) {
                searchMode = SearchMode.CHARACTER_URL;
            }
        }
        else if (searchMode.equals(SearchMode.CHARACTER_URL)) {
            characterURL = Utils.parseString(line, CHARACTER_URL_START_TAG, CHARACTER_URL_END_TAG);

            if (line.contains(CHARACTER_URL_START_TAG)) {
                characterURL = Utils.parseString(line, CHARACTER_URL_START_TAG, CHARACTER_URL_END_TAG);
                searchMode = SearchMode.CHARACTER_NAME;
            } else if (line.contains(BREAK_TAG)) {
                searchMode = SearchMode.CHARACTER_NAME_NO_URL;
            }
        }
        //Log.i("adding movie", "Role: " +role);
        else if (searchMode.equals(SearchMode.CHARACTER_NAME)) {
            characterName = Utils.parseString(line, CHARACTER_NAME_START_TAG, CHARACTER_NAME_END_TAG);
            if(role != null){
                ResultLink tempResultLink = new ResultLink(movieName, siteUrl + movieUrl, year, role, characterName, characterURL);
                addResultLink(tempResultLink, resultLinksMap, movieUrl);
            }
            searchMode = SearchMode.MOVIE_YEAR;
        }
        else if (searchMode.equals(SearchMode.CHARACTER_NAME_NO_URL)) {
            characterName = line.trim();
            if(role != null) {
                ResultLink tempResultLink  = new ResultLink(movieName, siteUrl + movieUrl, year, role, characterName);
                addResultLink(tempResultLink, resultLinksMap, movieUrl);
            }
            searchMode = SearchMode.MOVIE_YEAR;
        }
        String END_OF_RESULTS_TAG = "<h2>Related Videos</h2>";
        if(line.contains(END_OF_RESULTS_TAG)){
            hasfinishedParsing = true;
        }

        return hasfinishedParsing;
    }


    //
    // given a ResultLink, t, a map, and a key
    // if the key already exists, add the roles from 't' to the existing ResultLink in the map
    // otherwise, add 't' itself to the map
    private void addResultLink(ResultLink resultLink, Map <String,ResultLink> resultLinksMap, String key){
        if(resultLinksMap.containsKey(key)){
            ResultLink tempResultLink = resultLinksMap.get(key);
            tempResultLink.addRoles(resultLink);
        }
        else resultLinksMap.put(key, resultLink);
    }



    public String getImageUrl() {
        return this.imageUrl;
    }


    public List<ResultLink> getResults(){
        return new ArrayList<>(resultLinksMap.values());
    }

    public boolean hasNoResults(){
        return false;
    }

    private enum SearchMode {MOVIE_YEAR, MOVIE_NAME, PROFILE_PIC_START, PROFILE_PIC, MOVIE_URL,CHARACTER_NAME_NO_URL, CHARACTER_NAME, CHARACTER_URL, FILMOGRAPHY_HEADING, MOVIE_LIST}

}
