package com.pathogenstudios.fitbark;

import android.util.Log;

public class FitBarkApiException extends RuntimeException {
    private static final String TAG = "FitBarkApiException";

    public FitBarkApiException() {
        super();
    }

    public FitBarkApiException(String detailMessage) {
        super(detailMessage);
    }

    public FitBarkApiException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        Log.e(TAG, throwable.getStackTrace().toString());
    }

    public FitBarkApiException(Throwable throwable) {
        super(throwable);
        Log.e(TAG, throwable.getStackTrace().toString());
    }
}
