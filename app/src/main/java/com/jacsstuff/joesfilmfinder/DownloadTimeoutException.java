package com.jacsstuff.joesfilmfinder;

/**
 * An exception that is thrown when the number of attempts to download a page has exceed the specified limit
 */
public class DownloadTimeoutException extends Exception {

    public DownloadTimeoutException(String message){
        super(message);
    }

    public DownloadTimeoutException(String message, Throwable e){
        super(message, e);
    }
}
