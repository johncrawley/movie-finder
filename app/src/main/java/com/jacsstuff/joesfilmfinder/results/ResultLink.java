package com.jacsstuff.joesfilmfinder.results;

import android.support.annotation.NonNull;
import android.util.Log;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by John on 09/02/2016.
 * When two Search Results have been selected, their corresponding profiles are downloaded and parsed.
 * The main result of that parsing is a list of these Result Links that correspond to a movie that the
 * profile's person has participated in, or alternatively, a person who featured in the movie represented by
 * the profile.
 *
 * These are featured in the Compare Results Activity, where the Result Links of a profile is compared to those of the
 * other profile(s) and the common Result Links are stored in the Comparison Result Set instance.
 *
 * The Result Links are stored as part of the Actor or Movie Profiles in the Profile Cache (in permanent storage) and
 * therefore need to be Serializable.
 *
 */
public class ResultLink implements Comparable<ResultLink> {

    private String name;
    private String url;
    private String year;
    private String characterName = "";
    private String characterUrl = "";
    private Set<String> roles;
    private Set<String> secondActorRoles;   // when two compared result links contain persons and are found to be common, this variable stores the
                                            //  roles of the second person

    private final String ROLES_DELIMITER = ", ";
    private final String COMMA_NEWLINE = ", \n";

    public ResultLink(String name, String url, String roles){
        this.name = name;
        this.url = url;
        this.roles = new HashSet<>();
        this.roles.add(roles);

        this.secondActorRoles = new HashSet<>();
    }
    public ResultLink(String name, String url, String year, String roles, String characterName, String characterUrl){
        this.name = name;
        this.url = url;
        this.year = year;
        this.roles = new HashSet<>();
        this.roles.add(roles);
        this.characterName = characterName;
        this.characterUrl = characterUrl;
        this.secondActorRoles = new HashSet<>();
    }

    public ResultLink(String name, String url, String year, String roles, String characterName){

        this(name,url,year,roles,characterName,"");
    }

    //currently only used when creating a resultLink that is the result of a comparison between
    // an actor and a movie - this result link will only contain a message, saying that the
    // actor was in the movie, or not.
    public ResultLink(){
        this.secondActorRoles = new HashSet<>();
        this.roles = new HashSet<>();
    }


    public int compareTo(@NonNull ResultLink otherResultLink){

        if(yearDataNotFound(otherResultLink)){ return 0; }
        if(hasSameYearAs(otherResultLink)) { return 0;  }
        if(hasMoreRecentYearThan(otherResultLink)){ return -1; }
        return 1;
    }

    private boolean yearDataNotFound(ResultLink resultLink){
        return this.getYear() == null || resultLink.getYear() == null;
    }

    private boolean hasSameYearAs(ResultLink resultLink){
        return this.getYear().equals(resultLink.getYear());
    }

    private boolean hasMoreRecentYearThan(ResultLink resultLink){
        return getDigits(this.getYear()) > getDigits(resultLink.getYear());
    }

    private int getDigits(String year){
        return Integer.valueOf(year.replaceAll("\\D+", ""));
    }


    public String getYear() {
        return year;
    }

    private String getBracketedYear(){

        if((year!=null) && (!year.equals(""))){
            return "(" + this.year + ")";
        }
        return "";
    }


    public String getId() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getNameAndYear(){
        return getName() + " " + getBracketedYear();
    }

    public void setName(String name) {
        this.name = name;
    }

    private Set<String> getRoles() {
        return roles;
    }

    public String getRolesAsString(){
        return getRolesWithDelimiter(roles, COMMA_NEWLINE);
    }

    public String getRolesAsSingleLine(){
        return getRolesWithDelimiter(roles, ROLES_DELIMITER);
    }


    private String getRolesWithDelimiter(Set <String>rolesSet, String delimiter){

        String outputRoles = buildRolesStr(rolesSet, delimiter);
        return cutOffFinalDelimiter(outputRoles, delimiter);
    }

    private String buildRolesStr(Set <String> rolesSet, String delimiter){
        StringBuilder str = new StringBuilder();
        for (String role : rolesSet) {
            str.append(role);
            str.append(delimiter);
        }
        return str.toString();
    }

    private String cutOffFinalDelimiter(String str, String delimiter){
        if(!str.isEmpty()) {
            int endIndex = str.length() - (delimiter.length());
            if (endIndex > 0) {
                str = str.substring(0, endIndex);
            }
        }
        return str;
    }

    public void addRole(String newRole){
        if(roles.contains(newRole)){
            return;
        }
        roles.add(newRole);
    }


    public void addRoles(String rolesStr){
        this.roles.addAll(Arrays.asList(rolesStr.split(ROLES_DELIMITER)));
    }

    public void addRoles(ResultLink otherResultLink){
        this.roles.addAll(otherResultLink.getRoles());
    }

    public void addSecondActorRoles(ResultLink otherResultLink){
        secondActorRoles.addAll(otherResultLink.getRoles());
    }


    public String getSecondActorRolesAsString() {
        return getRolesWithDelimiter(secondActorRoles, COMMA_NEWLINE);
    }


    public String getUrl(){
        if(!this.url.equals("")){
            return url;
        }
            return null;
    }

    public String getCharacterName(){
        return this.characterName;
    }

    public String getCharacterUrl(){
        return this.characterUrl;
    }

    public void setYear(String year) {
        this.year = year;
    }




}
