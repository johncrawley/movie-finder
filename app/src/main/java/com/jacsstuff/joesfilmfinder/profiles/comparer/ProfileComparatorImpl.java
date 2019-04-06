package com.jacsstuff.joesfilmfinder.profiles.comparer;

import android.util.Log;

import com.jacsstuff.joesfilmfinder.profiles.ActorProfile;
import com.jacsstuff.joesfilmfinder.profiles.MovieProfile;
import com.jacsstuff.joesfilmfinder.profiles.ParsedProfile;
import com.jacsstuff.joesfilmfinder.results.ComparisonResultSet;
import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 21/03/2016.
 * This is the super class for the Actor and Movie Profile Comparators.
 * Those classes need only 2 methods and they are both here:
 *
 */
public class ProfileComparatorImpl
        implements ProfileComparator{


    public ComparisonResultSet compare(ParsedProfile p1, ParsedProfile p2){
            if(p2.getClass().equals(p1.getClass())){
            return  compareProfiles(p1,p2);
        }

        boolean isP1AnActor = p1.getClass().equals(ActorProfile.class);
        ActorProfile actorProfile =   isP1AnActor ?  (ActorProfile)p1 : (ActorProfile)p2;
        MovieProfile movieProfile =  !isP1AnActor ?  (MovieProfile)p1 : (MovieProfile)p2;
        return compareActorToMovie(actorProfile, movieProfile);
    }


    // used to either get the common people found in 2 movies, or the common movies of 2 people
    private ComparisonResultSet compareProfiles(ParsedProfile profile1, ParsedProfile profile2){

        List<ResultLink> resultsInCommon = new ArrayList<>();
        boolean firstIsNull = false, secondIsNull = false;
        if(profile1.getResultLinks() == null){
            log("first profile is null");
            firstIsNull = true;
        }
        if (profile2.getResultLinks() == null){
            log("second profile has null resultLinks");
            secondIsNull = true;
        }
        if(firstIsNull || secondIsNull){
            return new ComparisonResultSet(false);
        }

        for (ResultLink resultLink : profile1.getResultLinks()) {
            for (ResultLink otherResultLink : profile2.getResultLinks()) {
                if (resultLink.getId().equals(otherResultLink.getId())) {
                    resultLink.addSecondActorRoles(otherResultLink);
                    resultsInCommon.add(resultLink);
                    break;
                }
            }
        }
        return new ComparisonResultSet(resultsInCommon);
    }

    // returns whether the stated actor (i.e. person) participated in the stated movie.
    private ComparisonResultSet compareActorToMovie(ActorProfile actorProfile, MovieProfile movieProfile){
        log("...entering compareResult(ParsedResult parsedResult) ");
        for(ResultLink resultLink : movieProfile.getResultLinks()){
            if(resultLink.getId().equals(actorProfile.getUrl())){
                return new ComparisonResultSet(actorProfile.getName(), resultLink.getRolesAsSingleLine(), movieProfile.getName());
            }
            else log(" compare not found... actorURL:" + actorProfile.getUrl() + " resultLinkURL : "+ resultLink.getId());
        }
        return new ComparisonResultSet();
    }

    private void log(String msg){
        Log.i("ProfileComparator", msg);
    }
}



