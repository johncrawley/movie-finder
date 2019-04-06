package com.jacsstuff.joesfilmfinder.lists;

/**
 * Created by John on 02/04/2018.
 * Represents a list item in the results comparision activity
 */
public class ComparisonResultsListItem {

    private String roles1, roles2;
    private String title;

    public ComparisonResultsListItem(String roles1, String title, String roles2){
        this.roles1 = roles1;
        this.title = title;
        this.roles2 = roles2;
    }

    public String getTitle(){
        return this.title;
    }

    public String getRoles1(){
        return this.roles1;
    }

    public String getRoles2(){
        return this.roles2;
    }
}
