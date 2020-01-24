package com.hudzah.snapapaper;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.app.Application;
import android.util.Log;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.app_id))
                // if defined
                .clientKey(getString(R.string.client_key))
                .server(getString(R.string.server_url))
                .build()
        );


    }
}