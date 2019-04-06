package com.jacsstuff.joesfilmfinder.parsers;

/**
 * Created by John on 05/10/2016.
 * Contains strings used to parse for results
 */
public class ParseConstants {

    //public static final String START_OF_SEARCH_RESULTS = "<h1>Search results</h1>";
    public static final String START_OF_SEARCH_RESULTS = "Search: ";
    public static final String TV_ICON_TAG = "tv-40x59.png";
    public static final String MOVIE_ICON_TAG = "film-40x59.png";
    public static final String PERSON_ICON_TAG = "people-40x59.png";
    public static final String IMAGE_URL_START = "<img src=\"";
    public static final String IMAGE_URL_END = ".jpg";
    public static final String IMAGE_URL_END_PNG = ".png";
    public static final String RESULT_URL_START = "<a href=\"";
    //public static final String RESULT_URL_END = "\">";
    public static final String RESULT_URL_END = "\"";
    //public static final String END_OF_RESULTS_TAG = "<div id=\"sidebar\">";
    public static final String END_OF_RESULTS_TAG = "</body>";
    public static final String URL_NAME_IDENTIFIER = "/name/";
    public static final String URL_TITLE_IDENTIFIER = "/title/";
    public static final String IMAGES_URI = "/images/";
    public static final String WEBSITE_URL = "https://www.imdb.com";
    public static final String MOBILE_SITE_URL = "https://m.imdb.com";
    public static final String SLASH_CHARACTER = "/";


    public static final String MOVIE_TAG = "/title/";
    public static final String FULL_CREDITS_URL_PARAM = "fullcredits";



     static final String FULL_CREDITS_CONTENT_TAG = "<div id=\"fullcredits_content\"";
     static final String URL_START_TAG ="<a href=\"";
     static final String URL_END_TAG = "?ref_=";
    // static final String NAME_START_TAG = "<span class=\"itemprop\" itemprop=\"name\">";
     static final String NAME_START_TAG = "title=\"";
    // static final String NAME_END_TAG ="</span>";
    static final String NAME_END_TAG ="\"";
     static final String NON_ACTOR_NAME_START_TAG = "> ";
     static final String NON_ACTOR_NAME_END_TAG ="";
     static final String CHARACTER_TAG = "/character/";
     static final String URL_NAME_FRAGMENT = "/name/nm";
     static final String NEW_ROLE_TAG = "class=\"dataHeaderWithBorder\"";
     static final String END_OF_SECTION= "<!-- begin TOP_RHS -->";
}
