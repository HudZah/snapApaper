package com.hudzah.snapapaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MyAlarmMonthly extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        try{

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if(e == null){

                        if(objects.size() > 0){

                            for(ParseUser object : objects){

                                String packageSelected = object.getString("package");


                                if(packageSelected.equals("Free")) {
                                    object.put("monthlyRemaining", "30");
                                    object.saveInBackground();
                                }

                                else if(packageSelected.equals("Plus")) {
                                    object.put("monthlyRemaining", "60");
                                    object.saveInBackground();
                                }

                                else if(packageSelected.equals("Premium")) {
                                    object.put("monthlyRemaining", "120");
                                    object.saveInBackground();
                                }
                            }
                        }
                    }
                    else{
                        e.printStackTrace();
                    }
                }
            });

        }
        catch (Exception e){

            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
