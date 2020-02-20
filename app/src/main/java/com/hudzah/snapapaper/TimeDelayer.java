package com.hudzah.snapapaper;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class TimeDelayer {

    Context context;
    int TIME_TO_DELAY_BY;
    private static final String TAG = "TimeDelayer";

    public TimeDelayer(Context context){

        this.context = context;
    }

    public Boolean startDelay(int TIME_TO_DELAY_BY){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.i(TAG, "Delay by " + TIME_TO_DELAY_BY + " ms");
            }
        }, TIME_TO_DELAY_BY);

        return false;
    }
}
