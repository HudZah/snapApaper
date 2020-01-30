package com.hudzah.snapapaper;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MyMonthlyWork extends Worker {


    public static final String KEY_TASK = "key_task";


    public MyMonthlyWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {


        String packageSelected = getInputData().getString(ParseUser.getCurrentUser().getUsername());

        Log.i("Package is inside month", "" + packageSelected);

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

                                if (packageSelected.equals("Free")) {

                                    object.put("monthlyRemaining", "30");
                                    object.saveInBackground();
                                } else if (packageSelected.equals("Plus")) {

                                    object.put("monthlyRemaining", "60");
                                    object.saveInBackground();
                                } else if (packageSelected.equals("Premium")) {

                                    object.put("monthlyRemaining", "120");
                                    object.saveInBackground();
                                }
                            }
                        }
                    }
                }
            });
        }


        Log.i("Monthly", "doWork: Work is done after 30 days");

        return Result.success();

    }
}
