package com.jacsstuff.joesfilmfinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.jacsstuff.joesfilmfinder.parsers.NetUtils;


import java.net.URI;
import java.net.URISyntaxException;

/**
 * Some  methods that are used in different places
 */
public class Utils {


    // if the network isn't available this will send a call to create a dialog to
    // display a message to the user
    public static boolean displayDialogueIfNetworkUnavailable(Context context){
        if (!NetUtils.isNetworkUp(context.getApplicationContext())) {
            Utils.createDialog(context, R.string.network_unavailable);
            return true;
        }
        return false;
    }


    // This creates a dialog with the given message.
    public static void createDialog(Context context, int stringID){

        String message = context.getResources().getString(stringID);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage(message);

        alertBuilder.setNeutralButton(context.getResources().getString(R.string.cannot_connect_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // alertBuilder.

            }
        });
        alertBuilder.create();
        alertBuilder.show();
    }



    // removes all characters that might cause a problem when including a search term in a url.
    public static String sanitizeUrlPart(String str){

            if(str == null){
                return null;
            }
            str = str.trim();
            if(str.isEmpty()){
                return null;
            }
            str = str.replaceAll("[^a-zA-Z0-9\\s]", "");
            return str.replaceAll(" ", "+");
        }

    // returns a key that represents a profile.
    // i.e. strips away the non-unique parts of a url
    public static String getKey(String inputUrl){

        String retValue = inputUrl;
        int startIndex = retValue.indexOf("nm");
        int startIndexTitle = retValue.indexOf("title/");
        if((startIndex != -1)){
            retValue = retValue.substring(startIndex).replaceAll("/","");
        }
        if(startIndexTitle != -1){
            retValue = retValue.substring(startIndexTitle).replaceAll("/","");
        }
        return retValue;
    }


    public static String getSubstring(String line, String startTag, String endTag){
        String outputString = "";
        if(line.contains(startTag)){
            int startIndex = line.indexOf(startTag) + startTag.length();
            outputString= line.substring(startIndex);

            if((!endTag.isEmpty()) && (outputString.contains(endTag))){
                outputString = outputString.substring(0, outputString.indexOf(endTag));
            }

        }
        return outputString.trim();
    }

    public static String  parseString(String inputString, String startTag){
        return parseString(inputString, startTag, "");
    }

    // finds the start and end tags in the input string and returns the substring
    // if the tags can't be found, returns an empty string.
    public static String parseString(String inputString, String startTag, String endTag){
        return parseString(inputString, startTag, endTag, false);
    }

    public static String parseString(String inputString, String startTag, String endTag, boolean isEndTagIncluded){

       String tempString;
       String outputString = "";
       int endTagOffset = 0;
       // if stated, we want to include the end tag in the output string.
       if(isEndTagIncluded){
           endTagOffset = endTag.length();
       }

       if ( inputString.contains(startTag)) {
           int startIndex = inputString.indexOf(startTag) + startTag.length();
           tempString = inputString.substring(startIndex);
           if(!endTag.equals("")) {
               if (tempString.contains(endTag)) {
                   outputString = tempString.substring(0, tempString.indexOf(endTag) + endTagOffset);
               }
           }
           else{
               outputString = tempString;
           }
       }
       return outputString;
   }


}