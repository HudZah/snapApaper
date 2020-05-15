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
import android.content.res.ColorStateList;
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

import com.google.android.gms.ads.AdView;
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

import static com.hudzah.snapapaper.MainActivity.examCodesMap;
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

    static RelativeLayout layout;

    static EditText paperNumberEditText;

    static Spinner spinnerExamLevel;

    static Spinner spinnerSubject;

    static Spinner spinnerSession;

    static Spinner spinnerYear;

    ArrayList<String> arrayListAL;

    ArrayList<String> arrayListOL;

    ArrayList<String> arrayListIG;

    String selectedExamLevel;

    String entryKey;

    String entryValue;

    ArrayAdapter<String> arrayAdapterSubject;

    static String selectedExamCode;

    String paperNumber;

    TextView multipleYearsText;

    String newPaperNumber;

    List<String> papersToDownload;
    List<String> urlsToDownload;

    CodeSplitter codeSplitter;
    DownloadPdf downloadPdf;

    String DOWNLOADED_BY = "";

    AdView adView;

    private static final String TAG = "TypeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        adView = (AdView) findViewById(R.id.adView);

        AdsManager adsManager = new AdsManager(TypeActivity.this);

        if(!MainActivity.adsRemoved) {

            adsManager.initBannerAd();
            adsManager.loadBannerAd(adView);
        } else Log.d(TAG, "onCreate: Ads Removed");

        ImageView helpText = (ImageView) findViewById(R.id.helpText);

        TextView textCode = (TextView) findViewById(R.id.textCode);

        multipleDownload = new Dialog(this);

        userCode = (EditText) findViewById(R.id.paperCode);

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

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        cardViewType.setOnClickListener(new View.OnClickListener() {
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

        cardViewSearch.setOnClickListener(new View.OnClickListener() {
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            this.finish(); // close this activity and return to preview activity (if there is any)


        }

        return super.onOptionsItemSelected(item);
    }

    public void typeToFind(View view) {

        String paperCodeBefore = userCode.getText().toString();
        Log.i(LOG_TAG, paperCodeBefore);


//        if(dailyRemaining > 0){
        if(1 ==1){

            if(1==1){



//            if(monthlyRemaining > 0) {

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

                            // Under signle PAPEr OMLY!
                            String type = "type";

                            if(checkIfCodeExists()) {

                                showChoices(type);
                            }
                            else{

                                Snackbar.make(layout, "Please enter a valid exam code", Snackbar.LENGTH_LONG).show();
                            }

                        }
                    }
                    else {

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

    private Boolean checkIfCodeExists() {

        String[] checkForCode = codeText.split("/");

        if(examCodesMap.containsKey(checkForCode[0])){

            return true;
        }
        else{

            return false;
        }
    }

    public void showChoices(String type){

        choiceBuilder = new AlertDialog.Builder(this);

        String[] items = getResources().getStringArray(R.array.choice_names_for_type);

        codeSplitter = new CodeSplitter(TypeActivity.this, codeText);
        downloadPdf = new DownloadPdf(TypeActivity.this, layout);


        if(type.equals("type")) {
            codeSplitter.createCodeForType();
            DOWNLOADED_BY = "Type";

        }
        else if(type.equals("search")){

            codeSplitter.createCodeForSearch();
            DOWNLOADED_BY = "Search";

        }

        papersToDownload = codeSplitter.getCodes();
        urlsToDownload = codeSplitter.getUrls();

        choiceBuilder.setCancelable(true);
        choiceBuilder.setTitle("Select an option for \n" + codeSplitter.getPaperCode());

        choiceBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
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
                                        //decreaseLimit(value);

                                        Log.i("Papers", String.valueOf(papersToDownload));

                                        downloadPdf.downloadSinglePaper(urlsToDownload, papersToDownload, isQp, isMs, value, singlePaper, DOWNLOADED_BY);

                                    } else if (which == 1) {

                                        isQp = false;
                                        isMs = true;
                                        value = 1;
                                        //decreaseLimit(value);

                                        Log.i("Papers", String.valueOf(papersToDownload));

                                        downloadPdf.downloadSinglePaper(urlsToDownload, papersToDownload, isQp, isMs, value, singlePaper, DOWNLOADED_BY);


                                    }
                                }
                            }).show();

                    dialog.dismiss();

                } else if (which == 1) {

                    singlePaper = false;

                    if(1 == 1){

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

    public void searchToFind(View view){

        if(paperNumberEditText.getText().toString().isEmpty() || paperNumberEditText.getText().length() != 2){

            Snackbar.make(view, "Please enter a valid paper number that is 2 digits", Snackbar.LENGTH_LONG).show();
        }
        else{


            if(1 == 1){

                if(1 == 1){

                    String type = "search";

                    showChoices(type);


                }else{

                    Snackbar.make(view, "You have reached your monthly limit", Snackbar.LENGTH_LONG).show();
                }
            }
            else{


            }

            Log.i("PaperCodeForSearch", paperCode + paperCodeMs);
        }
    }




    public void showMultipleDownloads(int which, List<String> urlsToDownload, List<String> papersToDownload, Boolean isQp, Boolean isMs, String examCode){


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
        multipleYearsText = (TextView) multipleDownload.findViewById(R.id.multipleYearsText);


        yearTextView.setText("For " + examCode);

        closeOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleDownload.dismiss();
            }
        });

        multipleDownload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        multipleDownload.show();

//        if(packageSelected.equals("Plus")){

        if(1 == 0){


            cardViewManyPerYear.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            downloadMultipleYears.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#c2c2c2")));
            multipleYearsText.setTextColor(Color.parseColor("#ababab"));
            downloadMultipleYears.setTextColor(Color.parseColor("#666666"));

        }

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

//                if(val <= dailyRemaining){

                if(1 == 1){
                    //decreaseLimit(val);
                    singlePaper = false;
                    Boolean isQp = true;
                    Boolean isMs = true;
                    Log.i("ArrayPapers", String.valueOf(papersToDownload));
                    downloadPdf.downloadSinglePaper(urlsToDownload, papersToDownload, isQp, isMs, value, singlePaper, DOWNLOADED_BY);

                }
                else{

                    Snackbar.make(layout, "Required limit to download this is " + val, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        downloadFullSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(packageSelected.equals("Premium") || packageSelected.equals("Plus")){



                if( 1== 1){
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

//                        if (val <= dailyRemaining) {

                        if(1 == 1){

                            //decreaseLimit(val);

                            String paperType = "_qp_";

                            if (spinnerTypeOfPaperFullSet.getSelectedItemPosition() == 0) {

                                paperType = "_qp_";
                                Log.i("Worked", "Question");
                                Boolean isQp = true;
                                Boolean isMs = false;


                            } else{

                                paperType = "_ms_";
                                Log.i("Worked", "Mark");
                                Boolean isQp = false;
                                Boolean isMs = true;

                            }

                            singlePaper = false;

                            Log.i("Tester", val + paperType);

                            codeSplitter.createCodeForFullSet(val, paperType);
                            downloadPdf.downloadSinglePaper(codeSplitter.getUrls(), codeSplitter.getCodes(), isQp, isMs, val, singlePaper, DOWNLOADED_BY);

                            Log.i("Papers", String.valueOf(codeSplitter.getCodes()));


                            multipleDownload.dismiss();
                            //decreaseLimit(val);
                            //downloadPdf(0, urlsToDownloadFullSet, filenamesFullSet, isQp, isMs, val);

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

//                if(packageSelected.equals("Premium")) {

                if(1 == 1){


//                    if (packageSelected.equals("Premium")) {

                    if(1 == 1){

                        int fromYear = Integer.parseInt(spinnerFromYear.getSelectedItem().toString());

                        int toYear = Integer.parseInt(spinnerToYear.getSelectedItem().toString());

                        int val = (toYear - fromYear);

                        if (val <= 0) {

                            Snackbar.make(linearFullSet, "Please select a valid number of papers", Snackbar.LENGTH_LONG).show();

                            if (linearMultipleYears.getVisibility() == View.GONE) {

                                TransitionManager.beginDelayedTransition(cardViewManyPerYear, new AutoTransition());
                                linearMultipleYears.setVisibility(View.VISIBLE);
                                dropdownButtonMultipleYears.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);

                            }

                        } else {

//                            if (val <= dailyRemaining) {

                            if( 1 == 1){

                                String paperType = "_qp_";

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


                                codeSplitter.createCodeForMultipleYears(val, paperType, fromYear, toYear);

                                Log.i("PapersTest", String.valueOf(codeSplitter.getCodes()));

                                downloadPdf.downloadSinglePaper(codeSplitter.getUrls(), codeSplitter.getCodes(), isQp, isMs, val, singlePaper, DOWNLOADED_BY);

                                multipleDownload.dismiss();
                                //decreaseLimit(val);
                                //downloadPdf(0, urlsToDownloadMultiple, filenamesMultiple, isQp, isMs, val);

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






