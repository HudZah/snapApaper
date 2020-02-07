package com.hudzah.snapapaper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraX;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.view.TextureViewMeteringPointFactory;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int REQUEST_CODE_PERMISSIONS = 101;

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.INTERNET"};

    TextureView textureView;

    Preview preview;

    Button torchButton;

    File file;

    ImageCapture imgCap;

    String codeText;

    LoadingDialog loadingDialog;

    ImageView cameraImage;

    String[] splitText;

    String paperCode;

    static Map<String, String> examCodesMap = new HashMap<String, String>();

    String examLevel;

    String pdfUrl;

    Dialog multipleDownload;

    Dialog help;

    ParseObject papersObject;

    String pdfUrlMs;

    String paperCodeMs;

    ListAdapter adapter;

    AlertDialog.Builder choiceBuilder;

    Boolean isQp;

    DrawerLayout drawerLayout;

    ConnectionDetector connectionDetector;

    Boolean isMs;

    String LOG_TAG = "MainActivity";

    int dailyRemaining;

    int monthlyRemaining;

    int value;

    TextView limitTextView;

    int REQUEST_CODE_PROFILE = 2;

    static String todayDate;

    CharSequence ago;

    static String currentMonth;

    static long downloadTime;

    static ArrayList<String> myList;

    static Boolean isLoggedIn = false;

    ParseObject userLimits;

    public static String username;

    static String packageSelected;

    Date resetLimitDate;

    CardView cardViewDuo;

    String[] papersToDownload;

    String[] urlsToDownload;

    ImageView closeOverlay;

    TextView yearTextView;

    Button downloadDuo;

    Button downloadFullSet;

    Button downloadMultipleYears;

    Button dropdownButtonFullSet;

    Button dropdownButtonMultipleYears;

    LinearLayout linearFullSet;

    LinearLayout linearMultipleYears;

    CardView cardViewFullSet;

    CardView cardViewManyPerYear;

    String pdfUrlPart;

    String[] filenamesMultiple;

    String[] urlsToDownloadMultiple;

    Boolean singlePaper;

    String subjectName;

    String examLevelFull;


    public static final String KEY_TASK = "key_task";

    @Override
    protected void onStart() {
        super.onStart();

        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.setLimit(1);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){

                    if(objects.size() > 0){

                        for(ParseUser object : objects){

                            try {

                                Log.i("Limits", "" + object.getString("dailyRemaining"));
                                Log.i("Limits", "" + object.getString("monthlyRemaining"));

                                dailyRemaining = Integer.parseInt(object.getString("dailyRemaining"));
                                monthlyRemaining = Integer.parseInt(object.getString("monthlyRemaining"));
                            }
                            catch (Exception ex){

                                String err = (ex.getMessage()==null)?"Failed":ex.getMessage();
                                Log.e("sdcard-err2:",err);
                            }


                            Log.i("Limits", "Daily remain " + dailyRemaining + " and monthly " + monthlyRemaining);
                        }
                    }
                }
                else{

                    e.printStackTrace();
                }
            }
        });
    }

    public void torchAction(View view){


        if(preview.isTorchOn() != true){

            preview.enableTorch(true);



            //int id = getResources().getIdentifier("yourpackagename:drawable/" + StringGenerated, null, null);

            torchButton.setBackgroundResource(R.drawable.flashonicon);
        }
        else{

            preview.enableTorch(false);
            torchButton.setBackgroundResource(R.drawable.flashofficon);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ParseInstallation.getCurrentInstallation().saveInBackground();

        //ParseAnalytics.trackAppOpenedInBackground(getIntent());


        loadingDialog = new LoadingDialog(MainActivity.this);

        torchButton = (Button)findViewById(R.id.torchButton);

        torchButton.setBackgroundResource(R.drawable.flashofficon);

        cameraImage = (ImageView)findViewById(R.id.imgCapture);

        textureView = findViewById(R.id.view_finder);

        multipleDownload = new Dialog(this);

        help = new Dialog(this);

        paperCode = "";

        myList = new ArrayList<String>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        connectionDetector = new ConnectionDetector(this);


        if(connectionDetector.checkConnection()) {

            Intent loginIntentUsername = getIntent();
            username = loginIntentUsername.getStringExtra("username");

            ParseQuery<ParseUser> queryPackage = ParseUser.getQuery();

            queryPackage.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            queryPackage.setLimit(1);


            queryPackage.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {

                        if (objects.size() > 0) {

                            for (ParseUser object : objects) {

                                packageSelected = object.getString("package");
                                resetLimitDate = object.getCreatedAt();
                                setAlarmDailyLimit(resetLimitDate.getTime());
                                setAlarmMonthlyLimit(resetLimitDate.getTime());
                                Log.i("Date", String.valueOf(object.getCreatedAt().getTime()));
                            }
                        }
                    }
                    else{

                        Log.i("Alarm", e.getMessage());
                    }
                }
            });


            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.setLimit(1);

            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {

                        if (objects.size() > 0) {

                            for (ParseUser object : objects) {

                                try {

                                    Log.i("Limits", "" + object.getString("dailyRemaining"));
                                    Log.i("Limits", "" + object.getString("monthlyRemaining"));

                                    packageSelected = object.getString("package");
                                    dailyRemaining = Integer.parseInt(object.getString("dailyRemaining"));
                                    monthlyRemaining = Integer.parseInt(object.getString("monthlyRemaining"));
                                } catch (Exception ex) {

                                    String err = (ex.getMessage() == null) ? "Failed" : ex.getMessage();
                                    Log.e("sdcard-err2:", err);
                                }


                                Log.i("Limits", "Daily remain " + dailyRemaining + " and monthly " + monthlyRemaining);
                            }
                        }
                    } else {

                        e.printStackTrace();
                    }
                }
            });
            // do this every 24 hours, and 30 days

            todayDate = getDateFromFormat("dd-MM-yyyy");

            currentMonth = getDateFromFormat("MM-yyyy");

            Log.i("Limits", String.valueOf(dailyRemaining));


            drawerLayout = findViewById(R.id.drawer_layout);

            NavigationView navigationView = (NavigationView) findViewById(R.id.drawer);

            navigationView.setNavigationItemSelectedListener(this);

            ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

            drawerLayout.addDrawerListener(drawerToggle);

            drawerToggle.syncState();

            toolbar.setNavigationIcon(R.drawable.whitemenuverysmall);


            examCodesMap.put("7707", "Accounting (7707)");
            examCodesMap.put("7707", "Accounting (7707)");
            examCodesMap.put("4037", "Add-Maths (4037)");
            examCodesMap.put("5038", "Agriculture (5038)");
            examCodesMap.put("3180", "Arabic (3180)");
            examCodesMap.put("6090", "Art and Design (BD, MV, MU, PK) (6090)");
            examCodesMap.put("7094", "Bangladesh Studies (7094)");
            examCodesMap.put("3204", "Bengali (3204)");
            examCodesMap.put("5090", "Biology (5090)");
            examCodesMap.put("7115", "Business studies (7115)");
            examCodesMap.put("7048", "CDT Design and Communication (7048)");
            examCodesMap.put("5070", "Chemistry (5070)");
            examCodesMap.put("7100", "Commerce (7100)");
            examCodesMap.put("7101", "Commercial Studies (7101)");
            examCodesMap.put("2210", "Computer Science (2210)");
            examCodesMap.put("7010", "Computer Studies (7010)");
            examCodesMap.put("4024", "D-Maths (4024)");
            examCodesMap.put("6043", "Design and Technology (6043)");
            examCodesMap.put("2281", "Economics (2281)");
            examCodesMap.put("1123", "English (1123)");
            examCodesMap.put("5014", "Environmental Management (5014)");
            examCodesMap.put("6130", "Fashion and Textiles (6130)");
            examCodesMap.put("6065", "Food and Nutrition (6065)");
            examCodesMap.put("3015", "French (3015)");
            examCodesMap.put("2217", "Geography (2217)");
            examCodesMap.put("3025", "German (3025)");
            examCodesMap.put("2069", "Global Perspectives (2069)");
            examCodesMap.put("2055", "Hinduism (2055)");
            examCodesMap.put("2134", "History (Modern World Affairs) (2134)");
            examCodesMap.put("2158", "History World Affairs, 1917-1991 (2158)");
            examCodesMap.put("2056", "Islamic Religion and Culture (2056)");
            examCodesMap.put("2068", "Islamic Studies (2068)");
            examCodesMap.put("2058", "Islamiyat (2058)");
            examCodesMap.put("2010", "Literature in English (2010)");
            examCodesMap.put("5180", "Marine Science (5180)");
            examCodesMap.put("3202", "Nepali (3202)");
            examCodesMap.put("2059", "Pakistan Studies (2059)");
            examCodesMap.put("5054", "Physics (5054)");
            examCodesMap.put("7110", "Principles of Accounts (7110)");
            examCodesMap.put("2048", "Religious Studies (2048)");
            examCodesMap.put("5129", "Science - Combined (5129)");
            examCodesMap.put("3158", "Setswana (3158)");
            examCodesMap.put("3205", "Sinhala (3205)");
            examCodesMap.put("2251", "Sociology (2251)");
            examCodesMap.put("3035", "Spanish (3035)");
            examCodesMap.put("4040", "Statistics (4040)");
            examCodesMap.put("3162", "Swahili (3162)");
            examCodesMap.put("3226", "Tamil (3226)");
            examCodesMap.put("7096", "Travel and Tourism (7096)");
            examCodesMap.put("3247", "Urdu - First Language (3247)");
            examCodesMap.put("3248", "Urdu - Second Language (3248)");
            examCodesMap.put("9706", "Accounting (9706)");
            examCodesMap.put("9679", "Afrikaans (9679)");
            examCodesMap.put("8779", "Afrikaans - First Language (AS Level only) (8779)");
            examCodesMap.put("8679", "Afrikaans - Language (AS Level only) (8679)");
            examCodesMap.put("9713", "Applied Information and Communication Technology (9713)");
            examCodesMap.put("9680", "Arabic (9680)");
            examCodesMap.put("8680", "Arabic - Language (AS Level only) (8680)");
            examCodesMap.put("9479", "Art & Design (9479)");
            examCodesMap.put("9704", "Art & Design (9704)");
            examCodesMap.put("9700", "Biology (9700)");
            examCodesMap.put("9609", "Business (for first examination in 2016) (9609)");
            examCodesMap.put("9707", "Business Studies (9707)");
            examCodesMap.put("9980", "Cambridge International Project Qualification (9980)");
            examCodesMap.put("9701", "Chemistry (9701)");
            examCodesMap.put("9715", "Chinese (A Level only) (9715)");
            examCodesMap.put("8681", "Chinese - Language (AS Level only) (8681)");
            examCodesMap.put("9274", "Classical Studies (9274)");
            examCodesMap.put("9608", "Computer Science (for final examination in 2021) (9608)");
            examCodesMap.put("9618", "Computer Science (for first examination in 2021) (9618)");
            examCodesMap.put("9691", "Computing (9691)");
            examCodesMap.put("9631", "Design & Textiles (9631)");
            examCodesMap.put("9705", "Design and Technology (9705)");
            examCodesMap.put("9481", "Digital Media & Design (9481)");
            examCodesMap.put("9011", "Divinity (9011)");
            examCodesMap.put("8041", "Divinity (AS Level only) (8041)");
            examCodesMap.put("9708", "Economics (9708)");
            examCodesMap.put("9093", "English - Language AS and A Level (9093)");
            examCodesMap.put("8695", "English - Language and Literature (AS Level only) (8695)");
            examCodesMap.put("9695", "English - Literature (9695)");
            examCodesMap.put("8021", "English General Paper (AS Level only) (8021)");
            examCodesMap.put("8291", "Environmental Management (AS only) (8291)");
            examCodesMap.put("9336", "Food Studies (9336)");
            examCodesMap.put("9716", "French (A Level only) (9716)");
            examCodesMap.put("8682", "French - Language (AS Level only) (8682)");
            examCodesMap.put("8670", "French - Literature (AS Level only) (8670)");
            examCodesMap.put("8001", "General Paper 8001 (AS Level only) (8001)");
            examCodesMap.put("8004", "General Paper 8004 (AS Level only) (8004)");
            examCodesMap.put("9696", "Geography (9696)");
            examCodesMap.put("9717", "German (A Level only) (9717)");
            examCodesMap.put("8683", "German - Language (AS Level only) (8683)");
            examCodesMap.put("9239", "Global Perspectives & Research (9239)");
            examCodesMap.put("9687", "Hindi (A Level only) (9687)");
            examCodesMap.put("8687", "Hindi - Language (AS Level only) (8687)");
            examCodesMap.put("8675", "Hindi - Literature (AS Level only) (8675)");
            examCodesMap.put("9014", "Hinduism (9014)");
            examCodesMap.put("8058", "Hinduism (AS level only) (8058)");
            examCodesMap.put("9489", "History (9489)");
            examCodesMap.put("9389", "History (for final examination in 2021) (9389)");
            examCodesMap.put("9626", "Information Technology (9626)");
            examCodesMap.put("9013", "Islamic Studies (9013 & 8053)");
            examCodesMap.put("9488", "Islamic Studies (9488)");
            examCodesMap.put("8281", "Japanese Language (AS Level only) (8281)");
            examCodesMap.put("9084", "Law (9084)");
            examCodesMap.put("9693", "Marine Science (9693)");
            examCodesMap.put("9709", "Mathematics (9709)");
            examCodesMap.put("9231", "Mathematics - Further (9231)");
            examCodesMap.put("9607", "Media Studies (9607)");
            examCodesMap.put("9483", "Music (9483)");
            examCodesMap.put("9703", "Music (9703)");
            examCodesMap.put("8663", "Music (AS Level only) (8663)");
            examCodesMap.put("8024", "Nepal Studies (AS Level only) (8024)");
            examCodesMap.put("9396", "Physical Education (9396)");
            examCodesMap.put("9702", "Physics (9702)");
            examCodesMap.put("9718", "Portuguese (A Level only) (9718)");
            examCodesMap.put("8684", "Portuguese - Language (AS Level only) (8684)");
            examCodesMap.put("8672", "Portuguese - Literature (AS Level only) (8672)");
            examCodesMap.put("9698", "Psychology (9698)");
            examCodesMap.put("9990", "Psychology (9990)");
            examCodesMap.put("9699", "Sociology (9699)");
            examCodesMap.put("9719", "Spanish (A Level only) (9719)");
            examCodesMap.put("8665", "Spanish - First Language (AS Level only) (8665)");
            examCodesMap.put("8685", "Spanish - Language (AS Level only) (8685)");
            examCodesMap.put("8673", "Spanish - Literature (AS Level only) (8673)");
            examCodesMap.put("9689", "Tamil (9689)");
            examCodesMap.put("8689", "Tamil - Language (AS Level only) (8689)");
            examCodesMap.put("9694", "Thinking Skills (9694)");
            examCodesMap.put("9395", "Travel and Tourism (9395)");
            examCodesMap.put("9676", "Urdu (A Level only) (9676)");
            examCodesMap.put("8686", "Urdu - Language (AS Level only) (8686)");
            examCodesMap.put("9686", "Urdu - Pakistan only (A Level only) (9686)");
            examCodesMap.put("0452", "Accounting (0452)");
            examCodesMap.put("0508", "Arabic - First Language (0508)");
            examCodesMap.put("0400", "Art and Design (0400)");
            examCodesMap.put("0610", "Biology (0610)");
            examCodesMap.put("0450", "Business Studies (0450)");
            examCodesMap.put("0620", "Chemistry (0620)");
            examCodesMap.put("0509", "Chinese - First Language (0509)");
            examCodesMap.put("0523", "Chinese - Second Language (0523)");
            examCodesMap.put("0478", "Computer Science (0478)");
            examCodesMap.put("0420", "Computer Studies (0420)");
            examCodesMap.put("0445", "Design and Technology (0445)");
            examCodesMap.put("0453", "Development Studies (0453)");
            examCodesMap.put("0411", "Drama (0411)");
            examCodesMap.put("0455", "Economics (0455)");
            examCodesMap.put("0500", "English - First Language (0500)");
            examCodesMap.put("0627", "English - First Language (9-1) (UK only) (0627)");
            examCodesMap.put("0522", "English - First Language (UK) (0522)");
            examCodesMap.put("0486", "English - Literature (0486)");
            examCodesMap.put("0477", "English - Literature (9-1) (UK only) (0477)");
            examCodesMap.put("0510", "English - Second Language (oral endorsement) (0510)");
            examCodesMap.put("0454", "Enterprise (0454)");
            examCodesMap.put("0680", "Environmental Management (0680)");
            examCodesMap.put("0501", "French - First Language (0501)");
            examCodesMap.put("0520", "French - Foreign Language (0520)");
            examCodesMap.put("0460", "Geography (0460)");
            examCodesMap.put("0525", "German - Foreign Language (0525)");
            examCodesMap.put("0457", "Global Perspectives (0457)");
            examCodesMap.put("0549", "Hindi as a Second Language (0549)");
            examCodesMap.put("0470", "History (0470)");
            examCodesMap.put("0447", "India Studies (0447)");
            examCodesMap.put("0417", "Information and Communication Technology (0417)");
            examCodesMap.put("0580", "Mathematics (0580)");
            examCodesMap.put("0606", "Mathematics - Additional (0606)");
            examCodesMap.put("0607", "Mathematics - International (0607)");
            examCodesMap.put("0413", "Physical Education (0413)");
            examCodesMap.put("0652", "Physical Science (0652)");
            examCodesMap.put("0625", "Physics (0625)");
            examCodesMap.put("0490", "Religious Studies (0490)");
            examCodesMap.put("0653", "Science - Combined (0653)");
            examCodesMap.put("0654", "Sciences - Co-ordinated (Double) (0654)");
            examCodesMap.put("0408", "World Literature (0408)");


            if (allPermissionsGranted()) {
                startCamera(); //start camera if permission has been granted by user
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        }
        else if(!connectionDetector.checkConnection()){


            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Connection not found")
                    .setMessage("Please connect to a network")
                    .setCancelable(false)
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (connectionDetector.checkConnection()) {
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            } else {

                                Toast.makeText(MainActivity.this, "You are not connected to a network", Toast.LENGTH_SHORT).show();
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            }
                        }
                    })
                    .show();

        }
    }


    public String getDateFromFormat(String format) {
        Calendar today = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String date = formatter.format(today.getTime());

        Log.i("date", date);

        return date;
    }

    public void callProfileIntent(){

        Intent profileIntent = new Intent(getApplicationContext(), MyProfileActivity.class);
        profileIntent.putExtra("dailyRemaining", dailyRemaining);
        profileIntent.putExtra("monthlyRemaining", monthlyRemaining);
        startActivityForResult(profileIntent, REQUEST_CODE_PROFILE);
    }

    public void decreaseLimit(int amountToDecrease){

        dailyRemaining = dailyRemaining - amountToDecrease;
        monthlyRemaining = monthlyRemaining - amountToDecrease;

        ParseQuery<ParseUser> query = ParseUser.getQuery();
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
                        }
                    }
                }
                else{
                    e.printStackTrace();
                }
            }
        });

        Log.i(LOG_TAG, "Decreased limit: " + dailyRemaining + " and monthly remaining " + monthlyRemaining);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        String itemName = (String) item.getTitle();

        closeDrawer();


        switch (item.getItemId()){

            case R.id.item_a:

                callProfileIntent();
                break;
            case R.id.item_b:

                Intent listIntent = new Intent(this ,MyListActivity.class);
                startActivity(listIntent);
                break;
            case R.id.item_c:
                Intent typeIntent = new Intent(this, TypeActivity.class);
                startActivity(typeIntent);

                break;
            case R.id.item_d:
                Intent pricingIntent = new Intent(this,PricingActivity.class);
                startActivity(pricingIntent);

                break;
            case R.id.item_e:

                new AlertDialog.Builder(MainActivity.this)
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


                break;

            case R.id.item_f:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;


        }

        return true;
    }

    private void closeDrawer(){

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawer(){

        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){

            closeDrawer();
        }
        super.onBackPressed();
    }

    private void startCamera() {

        CameraX.unbindAll();


        //CameraControl cameraControl = CameraX.getCameraControl(CameraX.LensFacing.BACK);

        Rational aspectRatio = new Rational (textureView.getWidth(), textureView.getHeight());
        Size screen = new Size(textureView.getWidth(), textureView.getHeight()); //size of the screen

        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen).setTargetRotation(Surface.ROTATION_0).build();
        preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    //to update the surface texture we  have to destroy it first then re-add it
                    @Override
                    public void onUpdated(Preview.PreviewOutput output){
                        ViewGroup parent = (ViewGroup) textureView.getParent();
                        parent.removeView(textureView);
                        parent.addView(textureView, 0);

                        textureView.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();
                    }
                });

        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() != MotionEvent.ACTION_UP){

                    return false;
                }
//
                Log.i("Touched", "View touched");
//                TextureViewMeteringPointFactory factory = new TextureViewMeteringPointFactory(textureView);
//                MeteringPoint point = factory.createPoint(event.getX(), event.getY());
//                FocusMeteringAction action = FocusMeteringAction.Builder.from(point).build();
//
//                return true;


                final float x = (event != null) ? event.getX() : v.getX() + v.getWidth() / 2f;
                final float y = (event != null) ? event.getY() : v.getY() + v.getHeight() / 2f;

                TextureViewMeteringPointFactory pointFactory = new TextureViewMeteringPointFactory(textureView);
                float afPointWidth = 1.0f / 6.0f;  // 1/6 total area
                float aePointWidth = afPointWidth * 1.5f;
                MeteringPoint afPoint = pointFactory.createPoint(x, y, afPointWidth, 1.0f);
                MeteringPoint aePoint = pointFactory.createPoint(x, y, aePointWidth, 1.0f);

                try {
                    CameraX.getCameraControl(CameraX.LensFacing.BACK).startFocusAndMetering(
                            FocusMeteringAction.Builder.from(afPoint, FocusMeteringAction.MeteringMode.AF_ONLY)
                                    .addPoint(aePoint, FocusMeteringAction.MeteringMode.AE_ONLY)
                                    .build());
                } catch (CameraInfoUnavailableException e) {
                    Log.d("Error", "cannot access camera", e);
                }

                return true;
            }
        });

        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        imgCap = new ImageCapture(imageCaptureConfig);

        findViewById(R.id.imgCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (connectionDetector.checkConnection() == false) {

                    final Snackbar snackBar = Snackbar.make(textureView, "You are not connected to a network", Snackbar.LENGTH_INDEFINITE);

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

                    cameraImage.setImageResource(R.drawable.cameraon);

                    if (dailyRemaining > 0) {

                        if (monthlyRemaining > 0) {

                            loadingDialog.startLoadingDialog();

                            file = new File(Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".png");

                            imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                                @Override
                                public void onImageSaved(@NonNull File file) {

                                    String filePath = file.getPath();

                                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                                    rotateImage(bitmap);

                                }

                                @Override
                                public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                                    String msg = "Image Capture Failed : " + message;
                                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                                    if (cause != null) {
                                        cause.printStackTrace();
                                    }
                                }
                            });
                        } else {

                            Snackbar.make(textureView, "You have reached your monthly limit", Snackbar.LENGTH_LONG).show();
                            cameraImage.setImageResource(R.drawable.cameraoff);
                        }
                    } else {

                        final Snackbar snackbar = Snackbar.make(textureView, "You have reached your daily limit", Snackbar.LENGTH_LONG);

                        snackbar.setAction("ADD MORE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Log.i(LOG_TAG, "Extend limit");
                                Intent profileIntent = new Intent(getApplicationContext(), MyProfileActivity.class);
                                startActivity(profileIntent);
                            }
                        }).show();
                        cameraImage.setImageResource(R.drawable.cameraoff);
                    }
                }
            }

        });

        //bind to lifecycle:
        CameraX.bindToLifecycle((LifecycleOwner)this, preview, imgCap);

        
    }

    public void processImage(Bitmap bitmap){


        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {

                                Log.i("Text Recognized" , firebaseVisionText.getText());

                                splitCode(firebaseVisionText);

                            }
                        }
                )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        }
                );

    }

    private void splitCode(FirebaseVisionText firebaseVisionText) {

        loadingDialog.dismissDialog();

        cameraImage.setImageResource(R.drawable.cameraoff);

        boolean deleted = file.delete();

        if(deleted){

            Log.i("Deleted File", "Deleted");
        }

        String text = firebaseVisionText.getText();

        text = text.replaceAll("\\s+", "").replaceAll("\n", "").replaceAll("\r", "");

        Log.i("Whitespaces removed", text);

        boolean isMatching = Pattern.compile("\\d{4}\\/\\d{2}\\/\\w\\/\\w\\/\\d{2}").matcher(text).find();

        Log.i("Matching", String.valueOf(isMatching));

        if(isMatching){

            Log.i("in here", "In here");

            Pattern pattern = Pattern.compile("\\d{4}\\/\\d{2}\\/\\w\\/\\w\\/\\d{2}");
            Matcher matcher = pattern.matcher(text);

            while(matcher.find()){

                codeText = matcher.group(0);
                Log.i("Matched text", codeText);

                preview.enableTorch(false);
                torchButton.setBackgroundResource(R.drawable.flashofficon);

                splitText = codeText.split("/");


                // Splits into 9709, 42, F, M, 19
                // Splits into 9709, 42, M, J, 19
                // Splits into 9709, 42, O, N, 19


                paperCode = splitText[0] + "_";

                Log.i("Test value", splitText[0] + splitText[1]  + splitText[2] + splitText[3] + splitText[4]);

                if(Integer.parseInt(splitText[1]) < 10){
                    splitText[1] = splitText[1].substring(1);
                    Log.i("YearBelow", "Year below 2009 is " + splitText[1]);
                }


                if(splitText[2].equals("F")){

                    paperCode = paperCode + "m" + splitText[4] + "_qp_" + splitText[1];

                }
                else if(splitText[2].equals("M")){

                    paperCode = paperCode + "s" + splitText[4] + "_qp_" + splitText[1];
                }
                else if(splitText[2].equals("O")) {

                    paperCode = paperCode + "w" + splitText[4] + "_qp_" + splitText[1];
                }
                else if(Integer.valueOf(splitText[2]) == 0){

                    paperCode = paperCode + "w" + splitText[4] + "_qp_" + splitText[1];
                }
                else{

                    Log.i("paperCode", "Code is none");
                }

                Log.i("Paper", paperCode);

                Boolean paperCodeValid = Pattern.compile("\\d{4}\\/\\d{2}\\/\\w\\/\\w\\/\\d{2}").matcher(text).find();

                if (paperCodeValid != true){


                    Snackbar.make(textureView, "Could not find the past paper, please try again", Snackbar.LENGTH_LONG).show();
                }

                else {

                    CameraX.unbind(preview);

                    paperCodeMs = paperCode.replace("_qp_", "_ms_");

                    pdfUrlPart = examCodesMap.get(splitText[0]);

                    subjectName = examCodesMap.get(splitText[0]);

                    if (pdfUrlPart != null) {

                        Log.i("pdfUrlPart", pdfUrlPart);

                        pdfUrlPart = pdfUrlPart.replaceAll("\\s+", "%20");

                        Log.i("pdfUrlPart", pdfUrlPart);

                        if (Integer.valueOf(splitText[0]) > 8000) {

                            examLevel = "A%20Levels";
                            examLevelFull = "A Level";
                        } else if (Integer.valueOf(splitText[0]) < 1000) {

                            examLevel = "IGCSE";
                            examLevelFull = "IGCSE";
                        } else {

                            examLevel = "O%20Levels";
                            examLevelFull = "O Level";
                        }

                        pdfUrl = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + paperCode + ".pdf";

                        pdfUrlMs = pdfUrl.replace("_qp_", "_ms_");

                        Log.i("pdfURl", pdfUrl + "MS IS " + pdfUrlMs);

                        choiceBuilder = new AlertDialog.Builder(this);

                        // add a list

                        String[] items = getResources().getStringArray(R.array.choice_names);

                        choiceBuilder.setCancelable(true);
                        choiceBuilder.setTitle("Select an option for \n" + codeText);
                        //choiceBuilder.setMessage("Choose an option");

                        String[] papersToDownload = {paperCode, paperCodeMs};
                        String[] urlsToDownload = {pdfUrl, pdfUrlMs};
                        choiceBuilder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if(which == 0){


                                    String[] array = {"Download Question Paper", "Download Mark Scheme"};

                                    new AlertDialog.Builder(MainActivity.this)
                                            .setItems(array, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    singlePaper = true;

                                                    if(which == 0){

                                                        isQp = true;
                                                        isMs = false;
                                                        value = 1;
                                                        decreaseLimit(value);
                                                        downloadPdf(which, urlsToDownload, papersToDownload, isQp, isMs, value);
                                                    }

                                                    else if(which == 1){

                                                        isQp = false;
                                                        isMs = true;
                                                        value = 1;
                                                        decreaseLimit(value);
                                                        downloadPdf(which, urlsToDownload, papersToDownload, isQp, isMs, value);
                                                    }
                                                }
                                            }).show();

                                    dialog.dismiss();

                                }

                                else if(which == 1){

                                    singlePaper = false;

                                    if(packageSelected.equals("Plus") || packageSelected.equals("Premium")) {

                                        isQp = true;
                                        isMs  = true;
                                        value = 2;

                                        showMultipleDownloads(which, urlsToDownload, papersToDownload, isQp, isMs, codeText);
                                        dialog.dismiss();

                                    }

                                    else{

                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("Sorry you need a better package to use this")
                                                .setMessage("Would you like to upgrade to Plus or Premium to unlock this feature?")
                                                .setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        Intent pricingIntent = new Intent(getApplicationContext(), PricingActivity.class);
                                                        startActivity(pricingIntent);
                                                    }
                                                })
                                                .setNegativeButton("Not now", null)
                                                .create()
                                                .show();

                                        dialog.dismiss();
                                    }

                                    // FIXME: 1/31/2020
                                }
                                else if(which == 2){

                                    Intent listIntent = new Intent(getApplicationContext(), MyListActivity.class);
                                    startActivity(listIntent);
                                }

                                else if(which == 3){

                                    startCamera();
                                }

                            }


                        }).show();

                    }

                }

                startCamera();
            }

        }

        else{
            loadingDialog.dismissDialog();


            Snackbar.make(textureView, "Text is not a valid Cambridge exam format", Snackbar.LENGTH_LONG).show();


            loadingDialog.dismissDialog();

        }

    }

    public void downloadPdf(int which, String[] urlsToDownload, String[] fileNames, Boolean isQp, Boolean isMs, int value) {

        Log.i("Remaining", String.valueOf(dailyRemaining));

        if (connectionDetector.checkConnection() == false) {

            decreaseLimit(-value);

            final Snackbar snackBar = Snackbar.make(textureView, "You are not connected to a network", Snackbar.LENGTH_INDEFINITE);

            snackBar.setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            snackBar.dismiss();
                        }
                    }).show();


            Log.i("Internet", "Not connected");


        } else {


            try {
                loadingDialog.dismissDialog();



                Log.i("Which", String.valueOf(which));

                if(isQp && isMs){

                    urlsToDownload = urlsToDownload;
                    fileNames = fileNames;
                }

                else if(isQp && !isMs){

                    urlsToDownload = ArrayUtils.removeAll(urlsToDownload, pdfUrlMs);
                    fileNames = ArrayUtils.removeAll(fileNames, paperCodeMs);

                }
                else if(isMs && !isQp){

                    urlsToDownload = ArrayUtils.removeAll(urlsToDownload, pdfUrl);
                    fileNames = ArrayUtils.removeAll(fileNames, paperCode);
                }
            }

            catch (Exception e){
                e.printStackTrace();
            }


            Log.i("Array", Arrays.toString(urlsToDownload) + " " + Arrays.toString(fileNames));


            for(int i = 0; i < urlsToDownload.length; i++ ) {

                String fileName = fileNames[i];
                String url = urlsToDownload[i];

                Log.i("Downloader", "Download pdf " + url);

                File downloadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), fileName + ".pdf");

                if (downloadFile.exists()) {

                    Snackbar.make(textureView, "File already exists", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(this, fileName + " already exists", Toast.LENGTH_SHORT).show();

                } else {

                    try {

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

                        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        request.setMimeType("application/pdf");


                        request.setTitle(fileName);
                        request.setDescription("snapApaper");

                        request.allowScanningByMediaScanner();

                        request.setVisibleInDownloadsUi(true);

                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + ".pdf");

                        // get download service and enqueue file
                        downloadManager.enqueue(request);

                        Toast.makeText(this, "Downloading : " + fileName + ".pdf", Toast.LENGTH_LONG).show();

                        papersObject = new ParseObject("Papers");


                        String[] finalUrlsToDownload = urlsToDownload;

                        for (int c = 0; c < urlsToDownload.length; c++) {

                            papersObject.put("username", ParseUser.getCurrentUser().getUsername());
                            papersObject.put("paper", fileName);
                            papersObject.put("subject", subjectName);
                            papersObject.put("examLevel", examLevelFull);
                            papersObject.saveInBackground();

                        }


                        if (finalUrlsToDownload.length <= 2 && singlePaper) {

                            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        }


                    } catch (Exception e) {

                        Log.d("DOWNLOAD INFO", e.getMessage());
                        e.printStackTrace();
                    }

                }

                }

            }
            if(!singlePaper){

                Intent listIntent = new Intent(MainActivity.this, MyListActivity.class);
                startActivity(listIntent);

        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        try {

            unregisterReceiver(onComplete);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {


            //downloadTime = Long.parseLong(getDateFromFormat("yyyy-MM-dd"));



            if(isQp && isMs) {

                openPdf(paperCode);
                openPdf(paperCodeMs);

            }
            else if(!isQp && isMs){

                openPdf(paperCodeMs);
            }

            else if(isQp && !isMs){

                openPdf(paperCode);

            }



        }

    };



    public void openPdf(String fileName){

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), fileName + ".pdf");
        // FIXME: 1/30/2020
        Log.i("pdf file name", fileName + ".pdf");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }


    private void updateTransform(){
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int)textureView.getRotation();

        switch(rotation){
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float)rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }

    private void rotateImage(Bitmap bitmap){

        ExifInterface exifInterface = null;
        try{
            exifInterface = new ExifInterface(file.getAbsolutePath());
        }
        catch (IOException e){

            e.printStackTrace();
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();

        switch(orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            default:

        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        processImage(rotatedBitmap);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{

                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    public void typeCode(View view){

        Log.i(LOG_TAG, "Type");
        Intent typeIntent = new Intent(this, TypeActivity.class);
        typeIntent.putExtra("dailyRemaining", dailyRemaining);
        typeIntent.putExtra("monthlyRemaining", monthlyRemaining);

        startActivityForResult(typeIntent, 1);
    }

    public void help(View view){

        showHelpDialog();

    }

    public void showHelpDialog(){


        help.setContentView(R.layout.overlay_help);

        ImageView closeOverlay = (ImageView) help.findViewById(R.id.closeOverlay);

        Log.i(LOG_TAG, "Help");

        closeOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                help.dismiss();
            }
        });

        help.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                dailyRemaining = data.getIntExtra("dailyRemaining", 0);
                monthlyRemaining = data.getIntExtra("monthlyRemaining", 0);
            }

        }



        Log.i("This is very sad", "The fact that I am typing this message" +
                " to get that 1500 line mark is extremely sad." +
                "Although it exactly isn't 1500 lines for this whole project, rather close to 10,000" +
                " I have made it a life priority" +
                "of mine to get to this incredilbe mark" +
                "I would like to thank my laptop for this remarkable journey, going through all those rough patches," +
                "all the app crashes, all the random, dumb and weird glitches." +
                "Carrying me through this process wasn't easy, it required all of my dedication. Even if" +
                "that meant sacrificing my sleep time and getting up to 4 hours a sleep everyday." +
                "Randomly collapsing during the day due to my lack of sleep, really sucked." +
                "But oh well, it certainly will be worth it" +
                "Scrap that, most probably will" +
                "Actually no, maybeeee worth it" +
                "Still I put a lot of effort into this" +
                "I dedicated all my studying time towards this" +
                "Failing my A level because of this app, would be very likely" +
                "But depending on the outcome of it, might be worth the pain" +
                "God damn it 12 more lines?" +
                "Well as I am typing this I realize what a waste of time this" +
                "I could spend it on actually being productive" +
                "Rather than using this as an excuse to post an instagram story" +
                "But we are in the 21st century, people have foot fetishes so move on" +
                "Right now our world is on the verge of collapsing" +
                "Everyday a new TikTok meme is born, straying us further away from God" +
                "Plauge inc has become a reality, and our country might fall for it" +
                "And I don't have a mac so I can't make this app for IOS" +
                "God damn it apple why do you have to be overpriced" +
                "Oh I passed the milestone ffs");
    }

    public void setAlarmDailyLimit(long currentDay){

        if(currentDay != 0) {

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, MyAlarmDaily.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, currentDay, AlarmManager.INTERVAL_DAY, pendingIntent);

            Log.i("Alarm", "Alarm is running");

        }
    }

    public void setAlarmMonthlyLimit(long currentMonth){

        // FIXME: 2/2/2020

        if(currentMonth != 0) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, MyAlarmMonthly.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currentMonth, AlarmManager.INTERVAL_DAY * 30, pendingIntent);

            Log.i("Alarm", "Alarm is running (monthly)");


            // test with 2 days for now
        }
    }

    public void showMultipleDownloads(int which, String[] urlsToDownload, String[] papersToDownload, Boolean isQp, Boolean isMs, String examCode){



        multipleDownload.setContentView(R.layout.overlay_multiple_papers);

        // Download Buttons
        downloadDuo = (Button) multipleDownload.findViewById(R.id.downloadDuo);
        downloadFullSet = (Button)multipleDownload.findViewById(R.id.downloadFullSet);
        downloadMultipleYears = (Button)multipleDownload.findViewById(R.id.downloadMultipleYears);

        // Dropdown Buttons

        dropdownButtonFullSet = (Button)multipleDownload.findViewById(R.id.dropdownButtonFullSet);
        dropdownButtonMultipleYears = (Button)multipleDownload.findViewById(R.id.dropdownButtonMultipleYears);

        // Linear layouts

        linearFullSet = (LinearLayout)multipleDownload.findViewById(R.id.linearFullSet);
        linearMultipleYears = (LinearLayout)multipleDownload.findViewById(R.id.linearMultipleYears);

        // Card Views

        cardViewFullSet = (CardView)multipleDownload.findViewById(R.id.cardViewFullSet);
        cardViewManyPerYear = (CardView)multipleDownload.findViewById(R.id.cardViewManyPerYear);

        // Spinners

        Spinner spinnerNumberOfPapers= (Spinner) multipleDownload.findViewById(R.id.spinnerNumberOfPapers);
        Spinner spinnerTypeOfPaperFullSet = (Spinner) multipleDownload.findViewById(R.id.spinnerTypeOfPaperFullSet);

        Spinner spinnerFromYear = (Spinner)multipleDownload.findViewById(R.id.spinnerFromYear);
        Spinner spinnerToYear = (Spinner)multipleDownload.findViewById(R.id.spinnerToYear);
        Spinner spinnerTypeOfPaperMultipleYears = (Spinner) multipleDownload.findViewById(R.id.spinnerTypeOfPaperMultipleYears);

        closeOverlay = (ImageView) multipleDownload.findViewById(R.id.closeOverlay);
        yearTextView = (TextView)multipleDownload.findViewById(R.id.yearTextView);

        yearTextView.setText("For " + examCode);

        closeOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleDownload.dismiss();
            }
        });

        multipleDownload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        multipleDownload.show();

        dropdownButtonFullSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(linearFullSet.getVisibility() == View.GONE){

                    TransitionManager.beginDelayedTransition(cardViewFullSet, new AutoTransition());
                    linearFullSet.setVisibility(View.VISIBLE);
                    dropdownButtonFullSet.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
                else{

                    TransitionManager.beginDelayedTransition(cardViewFullSet);
                    linearFullSet.setVisibility(View.GONE);
                    dropdownButtonFullSet.setBackgroundResource(R.drawable.arrow_bitmap);
                }
            }
        });

        dropdownButtonMultipleYears.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(linearMultipleYears.getVisibility() == View.GONE){

                    TransitionManager.beginDelayedTransition(cardViewManyPerYear, new AutoTransition());
                    linearMultipleYears.setVisibility(View.VISIBLE);
                    dropdownButtonMultipleYears.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
                else{

                    TransitionManager.beginDelayedTransition(cardViewManyPerYear);
                    linearMultipleYears.setVisibility(View.GONE);
                    dropdownButtonMultipleYears.setBackgroundResource(R.drawable.arrow_bitmap);
                }
            }
        });


        downloadDuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int val = 2;

                if(val <= dailyRemaining){
                    decreaseLimit(val);
                    downloadPdf(which, urlsToDownload, papersToDownload, isQp, isMs, val);
                }
                else{

                    Snackbar.make(textureView, "Required limit to download this is " + val, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        downloadFullSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(packageSelected.equals("Premium") || packageSelected.equals("Plus")){

                    String vale = String.valueOf(spinnerNumberOfPapers.getSelectedItem());

                    int val = Integer.parseInt(vale);

                    if(val == 0) {

                        Snackbar.make(linearFullSet, "Please select a valid number of papers", Snackbar.LENGTH_LONG).show();

                        if(linearFullSet.getVisibility() == View.GONE){

                            TransitionManager.beginDelayedTransition(cardViewFullSet, new AutoTransition());
                            linearFullSet.setVisibility(View.VISIBLE);
                            dropdownButtonFullSet.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                        }


                    }
                    else if(val > 0){

                        if (val <= dailyRemaining) {
                            //decreaseLimit(val);
                            Log.i("Worked", paperCode);

                            // 9701_w16_qp_42

                            String[] splitCode = paperCode.split("_");
                            String splitVariant = splitCode[3].substring(1, 2);
                            Log.i("Worked", splitVariant);
                            String[] filenamesMultiple = new String[val];
                            String[] urlsToDownloadMultiple = new String[val];
                            String paperType = splitCode[2];


                            if(spinnerTypeOfPaperFullSet.getSelectedItemPosition() == 0){

                                paperType = "_qp_";
                                Log.i("Worked", "Question");
                                Boolean isQp = true;
                                Boolean isMs = false;


                            }
                            else{

                                paperType = "_ms_";
                                Log.i("Worked", "Mark");
                                Boolean isQp = false;
                                Boolean isMs = true;

                            }



                            for(int i = 1; i < val + 1; i++){

                                String paperNumber = i + splitVariant;
                                Log.i("Worked", paperNumber);

                                String newPaperCode = splitCode[0] + "_" + splitCode[1] + paperType + paperNumber;

                                filenamesMultiple[i -1] = newPaperCode;

                                String newPdfUrl = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + newPaperCode + ".pdf";

                                urlsToDownloadMultiple[i -1] = newPdfUrl;

                                Log.i("Worked", Arrays.toString(filenamesMultiple) + "Paper codes are " + Arrays.toString(urlsToDownloadMultiple));
                            }

                            multipleDownload.dismiss();
                            decreaseLimit(val);
                            downloadPdf(0, urlsToDownloadMultiple, filenamesMultiple, isQp, isMs, val);

                        } else {

                            Snackbar.make(linearFullSet, "Required limit to download this is " + val, Snackbar.LENGTH_LONG).show();
                        }
                    }

                }
                else{

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Sorry you need a better package to use this")
                            .setMessage("Would you like to upgrade to Plus or Premium to unlock this feature?")
                            .setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent pricingIntent = new Intent(getApplicationContext(), PricingActivity.class);
                                    startActivity(pricingIntent);
                                }
                            })
                            .setNegativeButton("Not now", null)
                            .create()
                            .show();
                }
            }
        });

        downloadMultipleYears.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(packageSelected.equals("Premium")) {

                    if (packageSelected.equals("Premium")) {

                        int fromYear = Integer.parseInt(spinnerFromYear.getSelectedItem().toString());

                        int toYear = Integer.parseInt(spinnerToYear.getSelectedItem().toString());

                        int val = (toYear - fromYear);


                        if (val <= 0) {

                            Snackbar.make(linearFullSet, "Please select a valid number of papers", Snackbar.LENGTH_LONG).show();

                            if (linearMultipleYears.getVisibility() == View.GONE) {

                                TransitionManager.beginDelayedTransition(cardViewManyPerYear, new AutoTransition());
                                linearMultipleYears.setVisibility(View.VISIBLE);
                                dropdownButtonMultipleYears.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);

                                // 2018, 2017, 2016, 2015, 2014

                            }

                        } else {

                            if (val <= dailyRemaining) {

                                Log.i("ArraySize", String.valueOf(val));

                                String[] filenamesMultiple = new String[val + 1];

                                String[] splitCode = paperCode.split("_");

                                String paperSeason = splitCode[1].substring(0, 1); //w

                                String paperYear = splitCode[1].substring(1); //16

                                String paperType = splitCode[2];

                                String[] urlsToDownloadMultiple = new String[val + 1];
                                // 9701_w16_qp_42

                                if (spinnerTypeOfPaperMultipleYears.getSelectedItemPosition() == 0) {

                                    paperType = "_qp_";
                                    Log.i("Worked", "Question");
                                    Boolean isQp = true;
                                    Boolean isMs = false;


                                } else {

                                    paperType = "_ms_";
                                    Log.i("Worked", "Mark");
                                    Boolean isQp = false;
                                    Boolean isMs = true;

                                }

                                int nextYear = fromYear;

                                for (int i = 0; i < val + 1; i++) {

                                    //paperYears[i] = String.valueOf(nextYear).substring(2);
                                    String newPaperYear = paperSeason + nextYear;

                                    newPaperYear = paperSeason + newPaperYear.substring(3);

                                    Log.i("PaperYear", newPaperYear);

                                    String newPaperCode = splitCode[0] + "_" + newPaperYear + paperType + splitCode[3];

                                    filenamesMultiple[i] = newPaperCode;

                                    String newPdfUrl = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + newPaperCode + ".pdf";

                                    urlsToDownloadMultiple[i] = newPdfUrl;

                                    nextYear = nextYear + 1;

                                }

                                Log.i("Years", Arrays.toString(filenamesMultiple) + " and urls are " + Arrays.toString(urlsToDownloadMultiple));

                                multipleDownload.dismiss();
                                decreaseLimit(val);
                                downloadPdf(0, urlsToDownloadMultiple, filenamesMultiple, isQp, isMs, val);

                            }
                            else{

                                Snackbar.make(linearFullSet, "Required limit to download this is " + val, Snackbar.LENGTH_LONG).show();
                            }
                        }

                    }


                }
                else{

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Sorry you need a better package to use this")
                            .setMessage("Would you like to upgrade to Premium to unlock this feature?")
                            .setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent pricingIntent = new Intent(getApplicationContext(), PricingActivity.class);
                                    startActivity(pricingIntent);
                                }
                            })
                            .setNegativeButton("Not now", null)
                            .create()
                            .show();

                }


            }
        });






    }


}
