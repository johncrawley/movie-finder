package com.jacsstuff.joesfilmfinder.parsers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.jacsstuff.joesfilmfinder.DownloadTimeoutException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 05/02/2016.
 * Contains methods that are used to download webpages and images
 */
public class NetUtils {


    private final static int PAGE_DOWNLOAD_TIMEOUT = 4500;
    private final static int IMAGE_DOWNLOAD_TIMEOUT = 2300;

    public static byte[] downloadImageByteArray(String src) throws IOException {
        log("Downloading image byte array from : "+  src);
        final int BYTE_COUNT_LIMIT = 4194304;
        // try {
        java.net.URL url = new java.net.URL(src);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        List<Byte> byteList = new ArrayList<>(10000);
        int byteValue = inputStream.read();


        while (byteValue != -1) {
            byteList.add((byte) byteValue);
            byteValue = inputStream.read();
        }
        if (byteList.size() >= BYTE_COUNT_LIMIT) {
            log("byte download limit reached. Returning null byte array");
            return null;
        }
        Byte[] byteObjArray = new Byte[byteList.size()];
        byteObjArray = byteList.toArray(byteObjArray);
        byte[] byteArray = new byte[byteObjArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = byteObjArray[i];
        }
        log("byteArray length: " + byteArray.length);       return byteArray;
    }

    public static boolean isValidUrl(String address){
        try{
            new URL(address);
            return true;
        }catch(MalformedURLException e){
            log("malformed url: " + address);
            e.printStackTrace();
        }
        return false;
    }


    // will return whether or not the device running the app can actually connect to a network or not
    public static boolean isNetworkUp(Context context){
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public static Bitmap downloadBitmapFromUrl(String src) {

      return downloadBitmapFromUrl(src,0);
    }

    private static Bitmap downloadBitmapFromUrl(String urlAddress, int tries) {
        int retryLimit = 3;
        if(urlAddress == null) urlAddress= "null";
        //Log.i("downloadBitmapFromUrl", "address:" + urlAddress);
        if(tries == retryLimit){
            return null;
        }
        try {
            java.net.URL url = new java.net.URL(urlAddress);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setReadTimeout(IMAGE_DOWNLOAD_TIMEOUT);
            connection.connect();
            InputStream input = connection.getInputStream();
            //log("Success downloading image from url: "+ urlAddress);
            return BitmapFactory.decodeStream(input);

        }catch(SocketTimeoutException e){
            //log("downloadBitmapFromUrl() - attempt #: "+  tries + " url: " + urlAddress);
            downloadBitmapFromUrl(urlAddress, tries + 1);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    private static void log(String msg){

        //Log.i("NetUtils", msg);
    }

    public static List<String> downloadWebpagetoList(String urlAddress) throws DownloadTimeoutException{
        return downloadWebpageToList(urlAddress, 0, 3);
    }
    public static void downloadAndParseWebpage(String urlAddress, InlineParser inlineParser) throws DownloadTimeoutException, IOException{
        downloadAndParseWebpage(urlAddress, inlineParser, 0, 3);
    }


    private static void downloadAndParseWebpage(String urlAddress, InlineParser parser, int previousTries, int maxTries)
             throws DownloadTimeoutException, IOException{

         if(previousTries == maxTries){
             log("Number of maximum tries for " + urlAddress + " exceeded. Aborting connection attempt");
             throw new DownloadTimeoutException("The maximum number of attempts to download the page has been reached : "+ urlAddress);
         }


         try {
             URL url = new URL(urlAddress);
          //   Log.i("NetUtils", "downloading url: " + urlAddress);
             URLConnection urlConnection = url.openConnection();
             urlConnection.setDoInput(true);
             urlConnection.setReadTimeout(PAGE_DOWNLOAD_TIMEOUT);
             urlConnection.getInputStream();
             boolean isFinishedParsing;
             BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
             String line;
             int count =0;
             while((line = reader.readLine()) != null){
                 count++;
                 //log("line: "+  line);
                 isFinishedParsing = parser.parseLine(line);
                 if(isFinishedParsing){
                    reader.close();
                    return;
                }
             }
             log("Lines read: "+ count);
             reader.close();

         }catch(SocketTimeoutException e){
             log("Socket Connection Timeout - download has timed out for: " + urlAddress);
             downloadWebpageToList(urlAddress, previousTries + 1, maxTries);
         }
         try {
             Thread.sleep(300);
         }catch(InterruptedException e){
             log("sleep after download was interrupted.");
         }
     }

    private static void throwExceptionIfOutOfTries(String urlAddress, int previousTries, int maxTries)throws DownloadTimeoutException{

        if(previousTries == maxTries){
            log("Number of maximum tries for " + urlAddress + " exceeded. Aborting connection attempt");
            //Utils.createDialog(context, R.string.server_unavailable);
            throw new DownloadTimeoutException("The maximum number of attempts to download the page has been reached : "+ urlAddress);
        }

    }

    
    private static List<String> downloadWebpageToList(String urlAddress, int previousTries, int maxTries) throws DownloadTimeoutException{

        throwExceptionIfOutOfTries(urlAddress, previousTries, maxTries);
        List<String> htmlList = new ArrayList<>(20000);
        try {
            //log("Entered downloadWebpageToList() - " + urlAddress + "  previousTries = " + previousTries);
            URL url = new URL(urlAddress);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(PAGE_DOWNLOAD_TIMEOUT);
            urlConnection.getInputStream();
            log("downloadWebpageToList() - connection opened");

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            //int lineCounter = 0;
            log("downloadWebpageToList() - downloading page... attempt number:" + previousTries );

            while((line = reader.readLine()) != null){
                htmlList.add(line);
            }
            log("downloadWebpageToList() - page download completed: " + urlAddress);
            reader.close();

        }catch(SocketTimeoutException e){
            log("Connection has timed out for download of : " + urlAddress);
            downloadWebpageToList(urlAddress, previousTries + 1, maxTries);
        }catch(java.io.IOException e){
            Log.e("parseUtils", e.toString());
            //throw e;
        }
        try {
            Thread.sleep(300);
        }catch(InterruptedException e){
            log("sleep after download was interrupted.");
        }
        return htmlList;
    }


/*
    public static boolean isWebpageReachable(String address){

        boolean canConnect = false;
        try {
            URL url = new URL(address);

            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            //urlc.setRequestProperty("User-Agent", "Android Application:"+Z.APP_VERSION);
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1000 * 30); // mTimeout is in seconds
            urlc.connect();

            if (urlc.getResponseCode() == 200) {
                log("pingWebsite() - getResponseCode == 200");
                 canConnect = true;
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            log("");
        }
        return canConnect;
    }
    */


}
