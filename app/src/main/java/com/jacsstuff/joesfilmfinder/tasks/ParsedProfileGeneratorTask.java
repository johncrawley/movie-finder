package com.jacsstuff.joesfilmfinder.tasks;

import android.content.Context;
import android.util.Log;

import com.jacsstuff.joesfilmfinder.DownloadTimeoutException;
import com.jacsstuff.joesfilmfinder.controller.CompareResultsController;
import com.jacsstuff.joesfilmfinder.profiles.GeneralProfile;
import com.jacsstuff.joesfilmfinder.profiles.ParsedProfile;
import com.jacsstuff.joesfilmfinder.profiles.ParsedProfileFactory;

import java.io.IOException;

/**
 * Created by John on 01/04/2018.
 * Executed for every profile that is downloaded or loaded from cache.
 */
public class ParsedProfileGeneratorTask implements Runnable{
    private Context context;
    private CompareResultsController controller;
    private GeneralProfile profile;
    private int profileIndex; // we want to add profiles to the results in the correct order, not just whichever one is processed first

    public ParsedProfileGeneratorTask(Context context, CompareResultsController controller, GeneralProfile profile, int index){
        this.context = context;
        this.controller = controller;
        this.profile = profile;
        this.profileIndex = index;
    }

    public void run(){
        controller.addParsedProfile(generateResult(profile), profileIndex);
    }



    private ParsedProfile generateResult(GeneralProfile profile){
        ParsedProfile parsedProfile;
        try{
            ParsedProfileFactory ppf = new ParsedProfileFactory(context);
            parsedProfile = ppf.generateResult(profile.getName(), profile.getUrl(), profile.usesDefaultProfilePic());
        }
        catch(DownloadTimeoutException | IOException e) {
            log(e.getMessage());
            log("Exception caught downloading profile for: " + profile.getName());
            controller.notifyException();
            return null;
        }
        return  parsedProfile;
    }

    private void log(String msg){
        Log.i("ProfileGenTask", msg);
    }
}
