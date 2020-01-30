package com.hudzah.snapapaper;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MyDailyWork extends Worker {

    public static final String KEY_TASK = "key_task";


    private static final String TAG = "MyDailyWork";

    public MyDailyWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {


        String packageSelected = getInputData().getString(ParseUser.getCurrentUser().getUsername());

        Log.i("Package is", "" + packageSelected);

        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.setLimit(1);

        if(packageSelected.equals(null)) {

            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {

                        if (objects.size() > 0) {

                            for (ParseUser object : objects) {

                                if (packageSelected.matches("Free")) {

                                    object.put("dailyRemaining", "5");
                                    object.saveInBackground();
                                } else if (packageSelected.matches("Plus")) {

                                    object.put("dailyRemaining", "10");
                                    object.saveInBackground();
                                } else if (packageSelected.matches("Premium")) {

                                    object.put("dailyRemaining", "20");
                                    object.saveInBackground();
                                }
                            }
                        }
                    }
                }
            });
        }


        Log.i(TAG, "doWork: Work is done after 1 day");

        return Result.success();
    }
}
