package com.hudzah.snapapaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Preview;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.hudzah.snapapaper.MainActivity.currentMonth;
import static com.hudzah.snapapaper.MainActivity.todayDate;

public class MyProfileActivity extends AppCompatActivity {

    TextView dailyRemainingTextView;

    TextView monthlyRemainingTextView;

    SharedPreferences sharedPreferences;

    int dailyRemaining;

    int monthlyRemaining;

    RelativeLayout watchAnAd;

    RelativeLayout share;

    RelativeLayout upgradePackage;

    int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        watchAnAd = (RelativeLayout)findViewById(R.id.watchAnAd);

        share = (RelativeLayout)findViewById(R.id.share);

        upgradePackage = (RelativeLayout)findViewById(R.id.upgradePackage);


        dailyRemainingTextView = (TextView)findViewById(R.id.dailyRemainingTextView);
        monthlyRemainingTextView = (TextView)findViewById(R.id.monthlyRemainingTextView);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        dailyRemaining = sharedPreferences.getInt(todayDate, 0);
        monthlyRemaining = sharedPreferences.getInt(currentMonth, 0);

        dailyRemainingTextView.setText(dailyRemaining + " more");

        monthlyRemainingTextView.setText(monthlyRemaining + " more");

        watchAnAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                value = 3;

                dailyRemaining += value;
                monthlyRemaining += value;

                sharedPreferences.edit().putInt(todayDate, dailyRemaining).apply(); //5
//
                sharedPreferences.edit().putInt(currentMonth, monthlyRemaining).apply(); //30
                //watchAd();
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

}
