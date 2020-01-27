package com.hudzah.snapapaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Preview;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import static com.hudzah.snapapaper.MainActivity.currentMonth;
import static com.hudzah.snapapaper.MainActivity.todayDate;

public class MyProfileActivity extends AppCompatActivity {

    TextView dailyRemainingTextView;

    TextView monthlyRemainingTextView;

    RelativeLayout watchAnAd;

    RelativeLayout share;

    RelativeLayout upgradePackage;

    int value;

    String packageSelected;

    ParseQuery<ParseUser> query;

    TextView dailyPackageDetails;

    TextView monthlyPackageDetails;

    TextView packageName;

    TextView tv_examboard;

    TextView tv_name;

    TextView detailsUsername;

    TextView detailsEmail;

    TextView detailsPhone;

    int dailyRemaining;

    int monthlyRemaining;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        loadingDialog = new LoadingDialog(this);

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout);

        watchAnAd = (RelativeLayout)findViewById(R.id.watchAnAd);

        share = (RelativeLayout)findViewById(R.id.share);

        upgradePackage = (RelativeLayout)findViewById(R.id.upgradePackage);

        dailyRemainingTextView = (TextView)findViewById(R.id.dailyRemainingTextView);

        monthlyRemainingTextView = (TextView)findViewById(R.id.monthlyRemainingTextView);

        dailyPackageDetails = (TextView)findViewById(R.id.dailyPackageDetails);

        monthlyPackageDetails = (TextView)findViewById(R.id.monthlyPackageDetails);

        packageName = (TextView)findViewById(R.id.packageName);

        tv_name = (TextView)findViewById(R.id.tv_name);

        tv_examboard = (TextView)findViewById(R.id.tv_examboard);

        detailsUsername = (TextView)findViewById(R.id.detailsUsername);

        detailsEmail = (TextView)findViewById(R.id.detailsEmail);

        detailsPhone = (TextView)findViewById(R.id.detailsPhone);


        ConnectionDetector connectionDetector = new ConnectionDetector(this);


        if (connectionDetector.checkConnection() == false) {

            final Snackbar snackBar = Snackbar.make(layout, "You are not connected to a network", Snackbar.LENGTH_INDEFINITE);

            snackBar.setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            snackBar.dismiss();
                        }
                    }).show();


            Log.i("Internet", "Not connected");
        }
        else {

            loadingDialog.startLoadingDialog();

            query = ParseUser.getQuery();

            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {

                    if (e == null) {

                        if (objects.size() > 0) {

                            for (ParseUser object : objects) {

                                dailyRemaining = Integer.parseInt(object.getString("dailyRemaining"));
                                monthlyRemaining = Integer.parseInt(object.getString("monthlyRemaining"));

                                Log.i("Limits", "Daily remain " + dailyRemaining + " and monthly " + monthlyRemaining);

                                String packageSelected = object.getString("package");
                                String examBoard = object.getString("examBoard");
                                String phoneNumber = object.getString("phoneNumber");

                                tv_name.setText(ParseUser.getCurrentUser().getUsername());

                                tv_examboard.setText(examBoard);

                                detailsUsername.setText(ParseUser.getCurrentUser().getUsername());

                                detailsEmail.setText(ParseUser.getCurrentUser().getEmail());

                                updateRemaining(dailyRemaining, monthlyRemaining);

                                if (!phoneNumber.matches("")) {

                                    detailsPhone.setText(phoneNumber);
                                } else {

                                    detailsPhone.setText("No phone number");
                                }

                                Log.i("Package ", packageSelected);

                                Log.i("Package ", String.valueOf(dailyRemaining));

                                if (packageSelected.equals("Free")) {

                                    dailyPackageDetails.setText("5");
                                    monthlyPackageDetails.setText("30");
                                    packageName.setText("Free");
                                } else if (packageSelected.equals("Plus")) {

                                    dailyPackageDetails.setText("10");
                                    monthlyPackageDetails.setText("60");
                                    packageName.setText("Plus");
                                } else if (packageSelected.equals("Premium")) {

                                    dailyPackageDetails.setText("20");
                                    monthlyPackageDetails.setText("120");
                                    packageName.setText("Premium");
                                }
                            }

                            loadingDialog.dismissDialog();
                        }
                    } else {

                        e.printStackTrace();
                    }
                }
            });


        }



        watchAnAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                value = 3;

                dailyRemaining += value;
                monthlyRemaining += value;

                //watchAd();

                query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {

                        if(e == null){

                            if(objects.size() > 0){

                                for(ParseUser object : objects){

                                    object.put("dailyRemaining", String.valueOf(dailyRemaining));
                                    object.put("monthlyRemaining", String.valueOf(monthlyRemaining));
                                    object.saveInBackground();
                                    updateRemaining(dailyRemaining, monthlyRemaining);

                                }
                            }
                        }
                        else{
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Nothing for now
            }

        });

        upgradePackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), PricingActivity.class);

                startActivity(intent);
            }
        });

    }

    public void updateRemaining(int dailRemaining, int monthlyRemaining){

        dailyRemainingTextView.setText(dailyRemaining + " more");

        monthlyRemainingTextView.setText(monthlyRemaining + " more");
    }

}
