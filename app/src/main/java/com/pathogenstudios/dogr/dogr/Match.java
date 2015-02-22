package com.pathogenstudios.dogr.dogr;

import com.parse.ParseFile;

/**
 * Created by HaydenKinney on 2/22/15.
 */
public class Match {
    private String name;
    private String number;
    private ParseFile thumbnailFile;

    public Match(String name, String num, ParseFile tF) {
        this.name = name;
        this.number = num;
        this.thumbnailFile = tF;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public ParseFile getThumbnailFile() {
        return thumbnailFile;
    }
}
