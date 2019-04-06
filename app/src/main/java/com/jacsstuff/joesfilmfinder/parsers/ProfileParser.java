package com.jacsstuff.joesfilmfinder.parsers;

import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.util.List;

/**
 * Created by John on 22/02/2016.
 */
public interface ProfileParser {

    byte[] parseProfilePic(List<String> webpage);

    List<ResultLink> parseResultLinks(List<String> webpage);


}
