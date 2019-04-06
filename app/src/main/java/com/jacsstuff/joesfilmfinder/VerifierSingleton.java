package com.jacsstuff.joesfilmfinder;

import android.content.Context;
import android.util.Log;

import com.jacsstuff.joesfilmfinder.parsers.NetUtils;
import java.util.List;

/**
 * Connects to a website with a verification string.
 * If the string on the webpage corresponds to the one
 * listed here, the app can proceed as normal.
 * This feature is to be used with beta versions or versions
 * given to various parties in the form of an apk file.
 * The idea being that when the beta is over, the site will be updated,
 * and the outdated version of the app will no longer work.
 */
public class VerifierSingleton {

    private static VerifierSingleton instance;
    private static boolean verified = false;

    public static VerifierSingleton getInstance(){
        if(instance == null){
            instance = new VerifierSingleton();
        }
        return instance;
    }


    private VerifierSingleton(){

    }


    public static boolean isVerified(){
      //  return true;
        return verified;
    }


    public static void setVerified(boolean b){
        verified = b;
    }


    public static boolean check(Context context){
        final String VERIFICATION_URL = "https://mightythoughtful.wordpress.com/verify/";
        //final String VERIFICATION_URL = "https://mightythoughtful.word12312312343.com/verify/";
        final String VERIFCATION_TEXT = "<h1 class=\"entry-title\">verify</h1>";
        log("in doInBackground()... downloading website for verification...");
        List<String> verifyWebpage;
        try {
           verifyWebpage = NetUtils.downloadWebpagetoList(VERIFICATION_URL);
        }catch(DownloadTimeoutException e){
            log(e.getMessage());
            return false;
        }
            if(verifyWebpage != null){
            for(String line: verifyWebpage) {
                if (line.contains(VERIFCATION_TEXT)) {
                    verified = true;
                    setVerified(verified);
                    log("verification found...");
                    return true;
                }
            }
        }
        return false;
    }

    private static void log(String msg){
        Log.i("VerifierSingleton", msg);
    }

}
