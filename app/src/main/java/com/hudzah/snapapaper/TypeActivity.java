package com.hudzah.snapapaper;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hudzah.snapapaper.MainActivity.packageSelected;

public class TypeActivity extends AppCompatActivity {

    EditText userCode;

    String LOG_TAG = "TypeActivity";

    String codeText;

    String text;

    String[] splitText;

    String paperCode;

    String examLevel;

    String pdfUrl;

    String pdfUrlMs;

    AlertDialog.Builder choiceBuilder;

    String paperCodeMs;

    Boolean isQp;

    ParseObject object;

    ConnectionDetector connectionDetector;

    Boolean isMs;

    int dailyRemaining;

    int monthlyRemaining;

    int value;

    SharedPreferences sharedPreferences;

    String todayDate;

    String currentMonth;

    ParseQuery<ParseUser> query;

    Boolean singlePaper;

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

    Dialog multipleDownload;

    String pdfUrlPart;

    ParseObject papersObject;

    String subjectName;

    String examLevelFull;

    Button typeCodeButtonDropDown;

    Button searchCodeButtonDropDown;

    LinearLayout typeCodeLayout;

    LinearLayout searchCodeLayout;

    CardView cardViewType;

    CardView cardViewSearch;

    RelativeLayout layout;

    EditText paperNumberEditText;

    Spinner spinnerExamLevel;

    Spinner spinnerSubject;

    Spinner spinnerSession;

    Spinner spinnerYear;

    ArrayList<String> arrayListAL;

    ArrayList<String> arrayListOL;

    ArrayList<String> arrayListIG;

    String selectedExamLevel;

    String entryKey;

    String entryValue;

    ArrayAdapter<String> arrayAdapterSubject;

    String selectedExamCode;

    String paperNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ImageView helpText = (ImageView) findViewById(R.id.helpText);

        TextView textCode = (TextView) findViewById(R.id.textCode);

        multipleDownload = new Dialog(this);

        userCode = (EditText) findViewById(R.id.paperCode);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.examboard_spinner, android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        object = new ParseObject("Papers");

        connectionDetector = new ConnectionDetector(this);

        layout = (RelativeLayout)findViewById(R.id.layout);

        Intent intent = getIntent();

        typeCodeButtonDropDown = (Button) findViewById(R.id.typeCodeButton);

        searchCodeButtonDropDown = (Button)findViewById(R.id.searchCodeButton);

        typeCodeLayout = (LinearLayout)findViewById(R.id.typeCodeLayout);

        searchCodeLayout = (LinearLayout)findViewById(R.id.searchCodeLayout);

        cardViewType = (CardView) findViewById(R.id.cardViewType);

        cardViewSearch = (CardView)findViewById(R.id.cardViewSearch);

        paperNumberEditText = (EditText)findViewById(R.id.paperNumberEditText);

        spinnerExamLevel = (Spinner)findViewById(R.id.spinnerExamLevel);

        spinnerSubject = (Spinner)findViewById(R.id.spinnerSubject);

        spinnerSession = (Spinner)findViewById(R.id.spinnerSession);

        spinnerYear = (Spinner)findViewById(R.id.spinnerYear);

        ArrayAdapter<CharSequence> arrayAdapterSpinner = ArrayAdapter.createFromResource(this, R.array.exam_level_spinner, android.R.layout.simple_spinner_dropdown_item);

        spinnerExamLevel.setAdapter(arrayAdapterSpinner);

        ArrayAdapter<CharSequence> arrayAdapterSession = ArrayAdapter.createFromResource(this, R.array.session_spinner_cambridge, android.R.layout.simple_spinner_dropdown_item);

        spinnerSession.setAdapter(arrayAdapterSession);

        ArrayAdapter<CharSequence> arrayAdapterYear = ArrayAdapter.createFromResource(this, R.array.years_spinner, android.R.layout.simple_spinner_dropdown_item);

        spinnerYear.setAdapter(arrayAdapterYear);

        HashMap<String, String> examCodesMap = (HashMap<String, String>) MainActivity.examCodesMap;

        ArrayList<String> arrayList = new ArrayList<>();

        ArrayList<String> examCodesArrayList = new ArrayList<String>();

        for(Map.Entry<String, String> entry : examCodesMap.entrySet()) {

            entryKey = entry.getKey();
            entryValue = entry.getValue();

            arrayList.add(entryValue);
            examCodesArrayList.add(entryKey);


        }

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedExamCode = examCodesArrayList.get(position);
                Log.i("ExamCodeSelected", selectedExamCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> arrayAdapterSubject = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        spinnerSubject.setAdapter(arrayAdapterSubject);


        todayDate = getDateFromFormat("dd-MM-yyyy");
        currentMonth = getDateFromFormat("MM-yyyy");

        query = ParseUser.getQuery();
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){

                    if(objects.size() > 0){

                        for(ParseUser object : objects){

                            Log.i("Limits", object.getString("dailyRemaining"));
                            Log.i("Limits", object.getString("monthlyRemaining"));

                            dailyRemaining = Integer.parseInt(object.getString("dailyRemaining"));
                            monthlyRemaining = Integer.parseInt(object.getString("monthlyRemaining"));

                            Log.i("Limits", "Daily remain " + dailyRemaining + " and monthly " + monthlyRemaining);
                        }
                    }
                }
            }
        });

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        typeCodeButtonDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(typeCodeLayout.getVisibility() == View.GONE){

                    TransitionManager.beginDelayedTransition(cardViewType, new AutoTransition());
                    typeCodeLayout.setVisibility(View.VISIBLE);
                    typeCodeButtonDropDown.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
                else{

                    TransitionManager.beginDelayedTransition(cardViewType);
                    typeCodeLayout.setVisibility(View.GONE);
                    typeCodeButtonDropDown.setBackgroundResource(R.drawable.arrow_bitmap);
                }
            }
        });

        searchCodeButtonDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(searchCodeLayout.getVisibility() == View.GONE){

                    TransitionManager.beginDelayedTransition(cardViewSearch, new AutoTransition());
                    searchCodeLayout.setVisibility(View.VISIBLE);
                    searchCodeButtonDropDown.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
                else{

                    TransitionManager.beginDelayedTransition(cardViewSearch);
                    searchCodeLayout.setVisibility(View.GONE);
                    searchCodeButtonDropDown.setBackgroundResource(R.drawable.arrow_bitmap);
                }
            }
        });

        userCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){

                    typeToFind(v);
                }

                return false;
            }
        });

        paperNumberEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){

                    searchToFind(v);
                }

                return false;
            }
        });

        helpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textCode.setVisibility(View.VISIBLE);
            }
        });


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getId() == R.id.view){

                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

    }

    public String getDateFromFormat(String format) {
        Calendar today = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String date = formatter.format(today.getTime());

        Log.i("date", date);

        return date;
    }

    public void decreaseLimit(int amountToDecrease){

        dailyRemaining -= amountToDecrease;
        monthlyRemaining -= amountToDecrease;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            Intent resultIntent = new Intent();
            resultIntent.putExtra("dailyRemaining", dailyRemaining);
            resultIntent.putExtra("monthlyRemaining", monthlyRemaining);

            setResult(RESULT_OK, resultIntent);
            this.finish(); // close this activity and return to preview activity (if there is any)

            IntentFilter intentFilter = new IntentFilter();

            registerReceiver(onComplete, intentFilter);


        }

        return super.onOptionsItemSelected(item);
    }

    public void typeToFind(View view) {

        String paperCodeBefore = userCode.getText().toString();
        Log.i(LOG_TAG, paperCodeBefore);


        if(dailyRemaining > 0){

            if(monthlyRemaining > 0) {

                if (paperCodeBefore.isEmpty()) {

                    Snackbar.make(view, "Please enter an exam code to continue", Snackbar.LENGTH_LONG).show();
                } else {

                    paperCodeBefore = paperCodeBefore.replaceAll("\\s+", "").replaceAll("\n", "").replaceAll("\r", "");

                    boolean isMatching = Pattern.compile("\\d{4}\\/\\d{2}\\/\\w\\/\\w\\/\\d{2}").matcher(paperCodeBefore).find();

                    if (isMatching) {

                        Log.i("in here", "In here");

                        Pattern pattern = Pattern.compile("\\d{4}\\/\\d{2}\\/\\w\\/\\w\\/\\d{2}");
                        Matcher matcher = pattern.matcher(paperCodeBefore);

                        while (matcher.find()) {

                            codeText = matcher.group(0);
                            codeText = codeText.toUpperCase();
                            Log.i(LOG_TAG, "Matched text: " + codeText);

                            splitText = codeText.split("/");

                            // Splits into 9709, 42, F, M, 19
                            // Splits into 9709, 42, M, J, 19
                            // Splits into 9709, 42, O, N, 19

                            if(Integer.parseInt(splitText[1]) < 10){
                                splitText[1] = splitText[1].substring(1);
                                Log.i("YearBelow", "Year below 2009 is " + splitText[1]);
                            }


                            paperCode = splitText[0] + "_";

                            if (splitText[2].equals("F")) {

                                paperCode = paperCode + "m" + splitText[4] + "_qp_" + splitText[1];

                            } else if (splitText[2].equals("M")) {

                                paperCode = paperCode + "s" + splitText[4] + "_qp_" + splitText[1];
                            } else if (splitText[2].equals("O")) {

                                paperCode = paperCode + "w" + splitText[4] + "_qp_" + splitText[1];
                            } else {

                                Log.i("paperCode", "Code is none");
                            }


                            Log.i(LOG_TAG, "Paper code is: " + paperCode);

                            paperCodeMs = paperCode.replace("_qp_", "_ms_");

                            pdfUrlPart = MainActivity.examCodesMap.get(splitText[0]);
                            subjectName = MainActivity.examCodesMap.get(splitText[0]);

                            if (pdfUrlPart != null) {

                                pdfUrlPart = pdfUrlPart.replaceAll("\\s+", "%20");

                                Log.i(LOG_TAG, "Exam subject name: " + pdfUrlPart);


                                if (Integer.valueOf(splitText[0]) > 8000) {

                                    examLevel = "A%20Levels";
                                    examLevelFull = "A Level";
                                } else if (Integer.valueOf(splitText[0]) < 1000) {

                                    examLevel = "IGCSE";
                                    examLevelFull = "IGCSE";
                                } else {

                                    examLevel = "O%20Levels";
                                    examLevelFull = "O level";
                                }

                                pdfUrl = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + paperCode + ".pdf";

                                pdfUrlMs = pdfUrl.replace("_qp_", "_ms_");

                                Log.i(LOG_TAG, pdfUrl + "MS IS " + pdfUrlMs);

                                choiceBuilder = new AlertDialog.Builder(this);

                                String[] items = getResources().getStringArray(R.array.choice_names_for_type);

                                choiceBuilder.setCancelable(true);
                                choiceBuilder.setTitle("Select an option for \n" + codeText);
                                //choiceBuilder.setMessage("Choose an option");

                                String[] papersToDownload = {paperCode, paperCodeMs};
                                String[] urlsToDownload = {pdfUrl, pdfUrlMs};

                                choiceBuilder.setItems(items, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0) {


                                            String[] array = {"Download Question Paper", "Download Mark Scheme"};

                                            new AlertDialog.Builder(TypeActivity.this)
                                                    .setItems(array, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            singlePaper = true;

                                                            if (which == 0) {

                                                                isQp = true;
                                                                isMs = false;
                                                                value = 1;
                                                                decreaseLimit(value);
                                                                downloadPdf(which, urlsToDownload, papersToDownload, isQp, isMs, value);
                                                            } else if (which == 1) {

                                                                isQp = false;
                                                                isMs = true;
                                                                value = 1;
                                                                decreaseLimit(value);
                                                                downloadPdf(which, urlsToDownload, papersToDownload, isQp, isMs, value);
                                                            }
                                                        }
                                                    }).show();

                                            dialog.dismiss();

                                        } else if (which == 1) {

                                            singlePaper = false;

                                            if (packageSelected.equals("Plus") || packageSelected.equals("Premium")) {

                                                isQp = true;
                                                isMs = true;
                                                value = 2;

                                                showMultipleDownloads(which, urlsToDownload, papersToDownload, isQp, isMs, codeText);
                                                dialog.dismiss();

                                            } else {

                                                new AlertDialog.Builder(TypeActivity.this)
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
                                        } else if (which == 2) {

                                            Intent listIntent = new Intent(getApplicationContext(), MyListActivity.class);
                                            startActivity(listIntent);
                                        }

                                    }

                                }).show();

                            }
                        }

                    } else {

                        Snackbar.make(view, "Code is not valid, please enter a valid code", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
            else{

                Snackbar.make(view, "You have reached your monthly limit", Snackbar.LENGTH_LONG).show();
            }
        }
        else{

            Snackbar.make(view, "You have reached your daily limit", Snackbar.LENGTH_LONG).show();
        }
    }

    public void searchToFind(View view){

        if(paperNumberEditText.getText().toString().isEmpty() || paperNumberEditText.getText().length() != 2){

            Snackbar.make(view, "Please enter a valid paper number that is 2 digits", Snackbar.LENGTH_LONG).show();
        }
        else{

            if(dailyRemaining > 0) {

                if (monthlyRemaining > 0) {

                    String examLevel = spinnerExamLevel.getSelectedItem().toString();

                    paperCode = selectedExamCode + "_";

                    String examYear = spinnerYear.getSelectedItem().toString().substring(2);

                    Log.i("Session", spinnerSession.getSelectedItem().toString());




                    if (spinnerSession.getSelectedItem().toString().equals("Feb/Mar")) {

                        paperCode = paperCode + "m" + examYear;

                    } else if (spinnerSession.getSelectedItem().toString().equals("May/Jun")) {

                        paperCode = paperCode + "s" + examYear;

                    } else if (spinnerSession.getSelectedItem().toString().equals("Oct/Nov")) {


                        paperCode = paperCode + "w" + examYear;
                    }

                    if(Integer.parseInt(paperNumberEditText.getText().toString()) < 10){

                        paperNumber = paperNumberEditText.getText().toString().substring(1);
                        Log.i("YearBelow", "Year below 2009 is " + splitText[1]);
                    }

                    else{

                        paperNumber = paperNumberEditText.getText().toString();
                    }

                    paperCodeMs = paperCode + "_ms_" + paperNumber;

                    paperCode = paperCode + "_qp_" + paperNumber;

                    pdfUrlPart = MainActivity.examCodesMap.get(selectedExamCode);
                    subjectName = MainActivity.examCodesMap.get(selectedExamCode);

                    if (pdfUrlPart != null) {

                        pdfUrlPart = pdfUrlPart.replaceAll("\\s+", "%20");

                        Log.i(LOG_TAG, "Exam subject name: " + pdfUrlPart);


                        if (Integer.valueOf(selectedExamCode) > 8000) {

                            examLevel = "A%20Levels";
                            examLevelFull = "A Level";
                        } else if (Integer.valueOf(selectedExamCode) < 1000) {

                            examLevel = "IGCSE";
                            examLevelFull = "IGCSE";
                        } else {

                            examLevel = "O%20Levels";
                            examLevelFull = "O level";
                        }

                        pdfUrl = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + paperCode + ".pdf";

                        pdfUrlMs = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + paperCodeMs + ".pdf";

                        Log.i(LOG_TAG, pdfUrl + "MS IS " + pdfUrlMs);

                        choiceBuilder = new AlertDialog.Builder(this);

                        String[] items = getResources().getStringArray(R.array.choice_names_for_type);

                        choiceBuilder.setCancelable(true);
                        choiceBuilder.setTitle("Select an option for \n" + paperCode);
                        //choiceBuilder.setMessage("Choose an option");

                        String[] papersToDownload = {paperCode, paperCodeMs};
                        String[] urlsToDownload = {pdfUrl, pdfUrlMs};

                        choiceBuilder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (which == 0) {


                                    String[] array = {"Download Question Paper", "Download Mark Scheme"};

                                    new AlertDialog.Builder(TypeActivity.this)
                                            .setItems(array, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    singlePaper = true;

                                                    if (which == 0) {

                                                        isQp = true;
                                                        isMs = false;
                                                        value = 1;
                                                        decreaseLimit(value);
                                                        downloadPdf(which, urlsToDownload, papersToDownload, isQp, isMs, value);
                                                    } else if (which == 1) {

                                                        isQp = false;
                                                        isMs = true;
                                                        value = 1;
                                                        decreaseLimit(value);
                                                        downloadPdf(which, urlsToDownload, papersToDownload, isQp, isMs, value);
                                                    }
                                                }
                                            }).show();

                                    dialog.dismiss();

                                } else if (which == 1) {

                                    singlePaper = false;

                                    if (packageSelected.equals("Plus") || packageSelected.equals("Premium")) {

                                        isQp = true;
                                        isMs = true;
                                        value = 2;

                                        showMultipleDownloads(which, urlsToDownload, papersToDownload, isQp, isMs, paperCode);
                                        dialog.dismiss();

                                    } else {

                                        new AlertDialog.Builder(TypeActivity.this)
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
                                } else if (which == 2) {

                                    Intent listIntent = new Intent(getApplicationContext(), MyListActivity.class);
                                    startActivity(listIntent);
                                }

                            }

                        }).show();

                    }
                }else{

                    Snackbar.make(view, "You have reached your monthly limit", Snackbar.LENGTH_LONG).show();
                }
            }else{

                Snackbar.make(view, "You have reached your daily     limit", Snackbar.LENGTH_LONG).show();
            }


            Log.i("PaperCodeForSearch", paperCode + paperCodeMs);
        }
    }

    public void downloadPdf(int which, String[] urlsToDownload, String[] fileNames, Boolean isQp, Boolean isMs, int value) {

        Log.i("Remaining", String.valueOf(dailyRemaining));


        if (!connectionDetector.checkConnection()) {

            // If no connection, refund limits
            decreaseLimit(-value);

            final Snackbar snackBar = Snackbar.make(layout, "You are not connected to a network", Snackbar.LENGTH_INDEFINITE);

            snackBar.setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            snackBar.dismiss();
                        }
                    }).show();


            Log.i(LOG_TAG, "Not connected");


        } else {

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



            for(int i = 0; i < urlsToDownload.length; i++ ) {

                String fileName = fileNames[i];
                String url = urlsToDownload[i];

                File downloadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), fileName + ".pdf");

                if (downloadFile.exists()) {

                    Snackbar.make(layout, "File already exists", Snackbar.LENGTH_LONG).show();
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

                        for(int c = 0; c < urlsToDownload.length; c++ ) {

                            papersObject.put("username", ParseUser.getCurrentUser().getUsername());
                            papersObject.put("paper", fileName);
                            papersObject.put("subject", subjectName);
                            papersObject.put("examLevel", examLevelFull);
                            papersObject.saveInBackground();

                        }


                        if(finalUrlsToDownload.length <= 2 && singlePaper) {

                            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        }



                    } catch (Exception e) {

                        Log.d("DOWNLOAD INFO", e.getMessage());
                        e.printStackTrace();
                    }


                    }

                }
            if(!singlePaper){

                Intent listIntent = new Intent(TypeActivity.this, MyListActivity.class);
                startActivity(listIntent);

            }


        }

    }


    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {




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

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {

            unregisterReceiver(onComplete);
        }catch (Exception e){

            e.getMessage();
        }
    }




    public void openPdf(String fileName){

        try {

            Log.i(LOG_TAG, "Open");

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), fileName + ".pdf");

            Log.i("pdf file name", fileName + ".pdf");

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
        catch (Exception e){

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

                    Snackbar.make(layout, "Required limit to download this is " + val, Snackbar.LENGTH_LONG).show();
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
                            String[] filenamesFullSet = new String[val];
                            String[] urlsToDownloadFullSet = new String[val];
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

                                filenamesFullSet[i -1] = newPaperCode;

                                String newPdfUrl = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + newPaperCode + ".pdf";

                                urlsToDownloadFullSet[i -1] = newPdfUrl;

                                Log.i("Worked", Arrays.toString(filenamesFullSet) + "Paper codes are " + Arrays.toString(urlsToDownloadFullSet));
                            }

                            multipleDownload.dismiss();
                            decreaseLimit(val);
                            downloadPdf(0, urlsToDownloadFullSet, filenamesFullSet, isQp, isMs, val);

                        } else {

                            Snackbar.make(linearFullSet, "Required limit to download this is " + val, Snackbar.LENGTH_LONG).show();
                        }
                    }

                }
                else{

                    new AlertDialog.Builder(TypeActivity.this)
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

                    new AlertDialog.Builder(TypeActivity.this)
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






