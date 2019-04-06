package com.jacsstuff.joesfilmfinder.parsers;

import com.jacsstuff.joesfilmfinder.profiles.AbstractProfile;
import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.util.List;

/**
 * Created by John on 27/09/2016.
 * will parse lines as they're being downloaded
 */
public interface InlineParser {

    public boolean parseLine(String line);
    //public <T extends AbstractProfile> T getProfile();
    public boolean hasNoResults();
}
