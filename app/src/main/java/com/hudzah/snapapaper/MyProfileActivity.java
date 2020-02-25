package com.hudzah.snapapaper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Preview;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import static com.hudzah.snapapaper.MainActivity.currentMonth;
import static com.hudzah.snapapaper.MainActivity.todayDate;

public class MyProfileActivity extends AppCompatActivity implements RewardedVideoAdListener {

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

    LoadingDialog loadingDialog;

    ImageView settingsButton;

    AlertDialog.Builder choiceBuilder;

    //public static final String app_id = "ca-app-pub-9334007634623344~4718773124";

    //public static final String ad_id = "ca-app-pub-9334007634623344/5447554958";


    private RewardedVideoAd videoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadingDialog = new LoadingDialog(this);

        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));

        videoAd = MobileAds.getRewardedVideoAdInstance(this);

        videoAd.setRewardedVideoAdListener(MyProfileActivity.this);

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout);

        watchAnAd = (RelativeLayout)findViewById(R.id.watchAnAd);

        share = (RelativeLayout)findViewById(R.id.share);

        upgradePackage = (RelativeLayout)findViewById(R.id.upgradePackage);

//        dailyRemainingTextView = (TextView)findViewById(R.id.dailyRemainingTextView);
//
//        monthlyRemainingTextView = (TextView)findViewById(R.id.monthlyRemainingTextView);

        dailyPackageDetails = (TextView)findViewById(R.id.dailyPackageDetails);

        monthlyPackageDetails = (TextView)findViewById(R.id.monthlyPackageDetails);

        packageName = (TextView)findViewById(R.id.packageName);

        tv_name = (TextView)findViewById(R.id.tv_name);

        tv_examboard = (TextView)findViewById(R.id.tv_examboard);

        detailsUsername = (TextView)findViewById(R.id.detailsUsername);

        detailsEmail = (TextView)findViewById(R.id.detailsEmail);

        detailsPhone = (TextView)findViewById(R.id.detailsPhone);

        settingsButton = (ImageView)findViewById(R.id.settingsButton);

        ConnectionDetector connectionDetector = new ConnectionDetector(this);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(MyProfileActivity.this, v);

                MenuInflater inflater = popupMenu.getMenuInflater();

                inflater.inflate(R.menu.settings_menu, popupMenu.getMenu());

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getItemId() == R.id.settingsProfile){

                            // Open settings
                            Intent settingsIntent = new Intent(MyProfileActivity.this ,SettingsActivity.class);
                            startActivity(settingsIntent);

                            return true;
                        }
                        else if(item.getItemId() == R.id.logoutProfile){

                            new AlertDialog.Builder(MyProfileActivity.this)
                                    .setTitle("Log out?")
                                    .setMessage("Are you sure you want to log out")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            ParseUser.logOut();
                                            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                            startActivity(loginIntent);
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                            return true;
                        }

                        return false;
                    }
                });
            }
        });


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





                                String packageSelected = object.getString("package");
                                String examBoard = object.getString("examBoard");
                                String phoneNumber = object.getString("phoneNumber");

                                tv_name.setText(ParseUser.getCurrentUser().getUsername());

                                tv_examboard.setText(examBoard);

                                detailsUsername.setText(ParseUser.getCurrentUser().getUsername());

                                detailsEmail.setText(ParseUser.getCurrentUser().getEmail());

                                if (!phoneNumber.matches("")) {

                                    detailsPhone.setText(phoneNumber);
                                } else {

                                    detailsPhone.setText("No phone number");
                                }

                                Log.i("Package ", packageSelected);

                                if (packageSelected.equals("Free")) {

                                    dailyPackageDetails.setText("Unlimited");
                                    monthlyPackageDetails.setText("Unlimited");
                                    packageName.setText("Free");
                                } else if (packageSelected.equals("Plus")) {

                                    dailyPackageDetails.setText("Unlimited");
                                    monthlyPackageDetails.setText("Unlimited");
                                    packageName.setText("Plus");
                                } else if (packageSelected.equals("Premium")) {

                                    dailyPackageDetails.setText("Unlimited");
                                    monthlyPackageDetails.setText("Unlimited");
                                    packageName.setText("Premium");
                                }
                            }

                            loadRewardedAd();

                            loadingDialog.dismissDialog();
                        }
                    } else {

                        e.printStackTrace();
                    }
                }
            });


        }



        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoadingDialog loadingDialog = new LoadingDialog(MyProfileActivity.this);

                loadingDialog.startLoadingDialog();

                final Handler handler = new Handler();

                String text = "Tired of using ad-filled websites to get your past papers? Switch to snapApaper \n\nhttps://play.google.com/store/apps/details?id=com.hudzah.snapapaper";

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        loadingDialog.dismissDialog();

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){


                            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "snapApaper");
                            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                            shareIntent.setType("text/plain");
                            MyProfileActivity.this.startActivity(Intent.createChooser(shareIntent, "Share link via"));

                        }
                        else{


                            final Intent shareIntent = new Intent(Intent.ACTION_SEND);

                            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "snapApaper");
                            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                            shareIntent.setType("text/plain");


                            MyProfileActivity.this.startActivity(Intent.createChooser(shareIntent, "Share file via"));

                        }

                    }
                }, 1500);


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

//        dailyRemainingTextView.setText(dailyRemaining + " more");
//
//        monthlyRemainingTextView.setText(monthlyRemaining + " more");
    }


    public void startVideoAd(View view) {


        if (videoAd.isLoaded()) {
            Log.i("MyProfile", "Load ad");
            videoAd.show();
        }
        else{



            Toast.makeText(this, "Your ad is loading", Toast.LENGTH_SHORT).show();
        }

    }

    public void loadRewardedAd(){


        if(!videoAd.isLoaded()) {
            videoAd.loadAd(getString(R.string.ad_unit_id), new AdRequest.Builder().build());
        }
    }

        //watchAd();

    @Override
    public void onRewardedVideoAdLoaded() {
//        Toast.makeText(getBaseContext(),
//                "Ad loaded.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
//        Toast.makeText(getBaseContext(),
//                "Ad opened.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
//        Toast.makeText(getBaseContext(),
//                "Ad closed.", Toast.LENGTH_SHORT).show();
        loadRewardedAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

        value = 2;

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
//        Toast.makeText(getBaseContext(),
//                "Ad left application.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {
//        Toast.makeText(this, "Ad complete", Toast.LENGTH_SHORT).show();
    }

    public void showRewardDialog(int value){

        new AlertDialog.Builder(MyProfileActivity.this)
            .setTitle("Congratulations!")
            .setMessage("You have earned an additional " + value + " " + "limits")
            .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            this.finish(); // close this activity and return to preview activity (if there is any)


        }

        return super.onOptionsItemSelected(item);
    }

}



