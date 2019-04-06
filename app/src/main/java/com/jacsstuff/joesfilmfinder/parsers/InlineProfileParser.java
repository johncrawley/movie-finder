package com.jacsstuff.joesfilmfinder.parsers;

import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.util.List;

/**
 * Created by John on 28/09/2016.
 */
public interface InlineProfileParser extends InlineParser{


    public List<ResultLink> getResults();
    public String getImageUrl();

}
