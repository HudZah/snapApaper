package com.hudzah.snapapaper;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeActivity extends AppCompatActivity {

    EditText userCode;

    String LOG_TAG = "TypeActivity";

    String codeText;

    String text;

    RelativeLayout layout;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ImageView helpText = (ImageView) findViewById(R.id.helpText);

        TextView textCode = (TextView) findViewById(R.id.textCode);

        userCode = (EditText) findViewById(R.id.paperCode);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.examboard_spinner, android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        object = new ParseObject("Papers");

        connectionDetector = new ConnectionDetector(this);

        layout = (RelativeLayout)findViewById(R.id.view);

        Intent intent = getIntent();

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

        TextView redirect = (TextView) findViewById(R.id.redirect);

        userCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){

                    search(v);
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

        redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(TypeActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();

//                Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
//                startActivity(searchIntent);
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

    public void search(View view) {

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

                            String pdfUrlPart = MainActivity.examCodesMap.get(splitText[0]);

                            if (pdfUrlPart != null) {

                                pdfUrlPart = pdfUrlPart.replaceAll("\\s+", "%20");

                                Log.i(LOG_TAG, "Exam subject name: " + pdfUrlPart);


                                if (Integer.valueOf(splitText[0]) > 8000) {

                                    examLevel = "A%20Levels";
                                } else if (Integer.valueOf(splitText[0]) < 1000) {

                                    examLevel = "IGCSE";
                                } else {

                                    examLevel = "O%20Levels";
                                }

                                pdfUrl = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + paperCode + ".pdf";

                                pdfUrlMs = pdfUrl.replace("_qp_", "_ms_");

                                Log.i(LOG_TAG, pdfUrl + "MS IS " + pdfUrlMs);

                                choiceBuilder = new AlertDialog.Builder(this);

                                // add a list

                                String[] items = getResources().getStringArray(R.array.choice_names_for_type);

                                choiceBuilder.setCancelable(true);
                                choiceBuilder.setTitle("Select an option for \n" + codeText);
                                //choiceBuilder.setMessage("Choose an option");

                                String[] papersToDownload = {paperCode, paperCodeMs};
                                String[] urlsToDownload = {pdfUrl, pdfUrlMs};
                                choiceBuilder.setItems(items, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

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
                                        } else if (which == 2) {
                                            isQp = true;
                                            isMs = true;
                                            value = 2;

                                            if(value <= dailyRemaining){
                                                decreaseLimit(value);
                                                downloadPdf(which, urlsToDownload, papersToDownload, isQp, isMs, value);
                                            }
                                            else{

                                                Snackbar.make(view, "Required limit to download this is " + value, Snackbar.LENGTH_LONG).show();
                                            }
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

                File downloadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName + ".pdf");

                if (downloadFile.exists()) {

                    Snackbar.make(layout, "File already exists", Snackbar.LENGTH_LONG).show();
                } else {

                    try {

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

                        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        request.setMimeType("application/pdf");

                        if (fileName == paperCode) {

                            request.setTitle(paperCode);
                            request.setDescription("SnapAPaper");
                        } else if (fileName == paperCodeMs) {

                            request.setTitle(paperCodeMs);
                            request.setDescription("SnapAPaper");
                        }
                        request.allowScanningByMediaScanner();

                        request.setVisibleInDownloadsUi(true);

                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + ".pdf");

                        // get download service and enqueue file
                        downloadManager.enqueue(request);

                        Toast.makeText(this, "Downloading : " + fileName + ".pdf", Toast.LENGTH_LONG).show();

                        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


                    } catch (Exception e) {

                        Log.d("DOWNLOAD INFO", e.getMessage());
                        e.printStackTrace();
                    }

                }

            }

        }

    }


    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {


            if(isQp && isMs) {

                openPdf(paperCode);
                openPdf(paperCodeMs);
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("paper", paperCode);

                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("paper", paperCodeMs);

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){

                            openPdf(paperCode);
                            openPdf(paperCodeMs);
                        }
                        else{

                            Toast.makeText(ctxt, "Error in saving file", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                object.saveInBackground();
            }else if(!isQp && isMs){

                openPdf(paperCodeMs);
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("paper", paperCodeMs);

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){

                            openPdf(paperCodeMs);
                        }
                        else{

                            Toast.makeText(ctxt, "Error in saving file", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            else if(isQp && !isMs){

                openPdf(paperCode);
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("paper", paperCode);

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){

                            openPdf(paperCode);
                        }
                        else{

                            Toast.makeText(ctxt, "Error in saving file", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

        Log.i(LOG_TAG, "Open");

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName + ".pdf");

        Log.i("pdf file name", fileName + ".pdf");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }



}






