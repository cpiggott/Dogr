package com.pathogenstudios.fitbark;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class ApiUtils {
    public static Date StringToDate(String dateString) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
    }

    public static String UrlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new FitBarkApiException(ex);
        }
    }
}
