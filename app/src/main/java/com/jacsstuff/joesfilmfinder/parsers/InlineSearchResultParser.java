package com.jacsstuff.joesfilmfinder.parsers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.jacsstuff.joesfilmfinder.R;
import com.jacsstuff.joesfilmfinder.results.SearchResult;
import com.jacsstuff.joesfilmfinder.results.SearchResultsSingleton;
import java.util.ArrayList;
import java.util.List;
import static com.jacsstuff.joesfilmfinder.parsers.ParseConstants.*;

/**
 * Created by John on 28/09/2016.
 * passed into the NetUtils method for downloading pages
 * to parse each line downloaded from a search query
 */
public class InlineSearchResultParser implements InlineParser {

    private boolean hasFinishedParsing = false;
    private List<SearchResult> searchResults;
    Context context;
    boolean hasParsedStartOfSearchResults = false;
    private String imageUrl;
    private String resultUrl;
    private String resultName;
    private String previousLine = "";
    int lineCount = 0;

    public InlineSearchResultParser(Context context) {
        searchResults = new ArrayList<>();
        this.context = context;
        this.imageUrl = "";
        this.resultUrl = "";
        this.resultName = "";
    }


    private String parseForUrl(String line){
        final String NON_RESULT_TOKEN = "/offsite/";
        String result = getSubstring(line, RESULT_URL_START, RESULT_URL_END);
        result = removePathQueryIfPresent(result);
        if(result.contains(NON_RESULT_TOKEN)){
            result = "";
        }
        if(!result.isEmpty()){
            result = WEBSITE_URL + result;
            log("InlineSearchResultParser: Result URL Found: "+ resultUrl);
        }
        return result;
    }


    public boolean parseLine(String line) {
        printLineParseCount();
        if (line.contains(START_OF_SEARCH_RESULTS)) {
            hasParsedStartOfSearchResults = true;
        }
        if (!hasParsedStartOfSearchResults) {
            return hasFinishedParsing;
        }
        if(line.contains(IMAGE_URL_START)) {
            imageUrl = getSubstring(line, IMAGE_URL_START, IMAGE_URL_END, IMAGE_URL_END_PNG, true);

            if (imageUrl.startsWith(IMAGES_URI)){
                imageUrl = MOBILE_SITE_URL + imageUrl;
            }
        }
        if(resultUrl.isEmpty()){
            resultUrl = parseForUrl(line);
        }
        if(previousLine.equals("<span class=\"h3\">")){
            resultName = line.trim();
            log("name found: "+ resultName);
        }

        if (areNotEmpty(imageUrl, resultUrl, resultName)) {
            Bitmap thumbnailImage = deriveThumbnailImage(imageUrl, resultUrl);
            searchResults.add(new SearchResult(resultName, resultUrl, thumbnailImage, usesPlaceholderImage));
            resetTempVariables();
        }

        if (line.contains(END_OF_RESULTS_TAG)) {
            hasFinishedParsing = true;
            Log.i("InlineSearchResultPars", "end of results, count: " + searchResults.size());
        }
        previousLine = line;
        return hasFinishedParsing;
    }


    private String removePathQueryIfPresent(String url){
        final String QUERY_SYMBOL = "?";
        if(url.contains(QUERY_SYMBOL)){
            url = url.substring(0, url.indexOf(QUERY_SYMBOL));
        }
        return url;
    }



    private String getSubstring(String line, String start, String end) {
        return getSubstring(line, start, end, null, false);
    }

    private String getSubstring(String line, String startToken, String endToken, String alternativeEndToken, boolean includeEndTag) {

        if(tokensNotFound(line, startToken, endToken, alternativeEndToken)){
            return "";
        }
        String currentEndToken = line.contains(endToken)? endToken : alternativeEndToken;
        if(currentEndToken == null){
            return "";
        }
        String remainingString = getRemaining(line, startToken);
        int endIndex = getEndIndex(remainingString, currentEndToken, includeEndTag);
        if(endIndex == -1){
            return "";
        }
        return remainingString.substring(0, endIndex);
    }

    private String getRemaining(String line, String startToken){
       return line.substring(line.indexOf(startToken) + startToken.length());
    }


    private int getEndIndex(String line, String endToken, boolean includeEndTag){
        if(!line.contains(endToken)){
            return -1;
        }
        int endIndex = line.indexOf(endToken);
        if(includeEndTag){
            endIndex += endToken.length();
        }
        if(endIndex >= line.length()){
            return -1;
        }
        return endIndex;
    }

    private boolean tokensNotFound(String line, String startToken, String endToken, String altEndToken){
        return !line.contains(endToken) && (altEndToken == null || !line.contains(altEndToken)) || !line.contains(startToken);
    }


    private boolean areNotEmpty(String ... strings){
        for(String str: strings){
            if(str.isEmpty())return false;
        }
        return true;
    }

    private void printLineParseCount(){
        lineCount++;
        if(lineCount % 100 == 0){
            log("Lines parsed: " + lineCount);
        }
    }

    private void resetTempVariables(){
        this.resultName = "";
        this.imageUrl = "";
        this.resultUrl = "";
    }

    //public <T extends AbstractProfile> T getProfile();
    public boolean hasNoResults() {
        return searchResults.isEmpty();
    }


    public List<SearchResult> getResults() {
        return this.searchResults;
    }


    public void log(String msg) {

        //Log.i("ResponseParser", msg);
    }

    private boolean usesPlaceholderImage = false;

    // checks if the url for the image has already been downloaded, and if so
    // returns the temprarily stored image.
    // the boolean returns whether or not the photo is a placeholder icon when there's no actual profile pic on the site.
    private Bitmap deriveThumbnailImage(String imageUrl, String profileUrl) {

        Bitmap photoBitmap = determineDefaultThumbnail(imageUrl);
        if(photoBitmap != null){
            usesPlaceholderImage = true;
            return photoBitmap;
        }

        SearchResultsSingleton results = SearchResultsSingleton.getInstance();

        if (results.containsImage(imageUrl)) {
            return  results.getImage(imageUrl);
        }
        long starttime = System.currentTimeMillis();
        Bitmap bitmap = NetUtils.downloadBitmapFromUrl(imageUrl);
        long timeElapsed = System.currentTimeMillis() - starttime;
        log("thumbnail downloaded in " + timeElapsed + "ms");
        if (bitmap == null){
            bitmap = determineThumbnailOnLoadFail(profileUrl);
        }
        results.putImage(imageUrl, bitmap);
        return bitmap;
    }


    private Bitmap determineDefaultThumbnail(String url){

        String key = url;
        int drawableId = R.drawable.person_icon_default;
        if (url.contains(SLASH_CHARACTER)) {
            key = url.substring(url.lastIndexOf(SLASH_CHARACTER));
        }
        if (key.contains(MOVIE_ICON_TAG) || key.contains(TV_ICON_TAG)) {
            drawableId = R.drawable.movie_icon_default;
            return getStoredImage(drawableId);
        }
        if (key.contains(PERSON_ICON_TAG)) {
            return getStoredImage(drawableId);
        }
        return null;
    }

    private Bitmap determineThumbnailOnLoadFail(String profileUrl){
        int drawableId = R.drawable.movie_thumbnail_placeholder1;
        if (profileUrl.contains(URL_NAME_IDENTIFIER)){
            drawableId = R.drawable.person_thumbnail_placeholder1;
        }
        usesPlaceholderImage = true;
        return getStoredImage(drawableId);
    }


    private Bitmap getStoredImage(int drawableId){
        return BitmapFactory.decodeResource(context.getResources(), drawableId);
    }


}