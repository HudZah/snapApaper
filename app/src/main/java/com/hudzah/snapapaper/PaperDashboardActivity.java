package com.hudzah.snapapaper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.os.health.TimerStat;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.PatternPathMotion;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class PaperDashboardActivity extends AppCompatActivity {

    TextView paperCodeHeading;

    TextView dateAddedTextView;

    TextView subjectNameTextView;

    TextView examLevelTextView;

    String examPaperCode;

    String examLevel;

    String dateCreated;

    String subjectName;

    Boolean isDownloaded;

    TextView isDownloadedTextView;

    int position;

    File file;

    MyListActivity myListActivity;

    double bestTime;

    RelativeLayout relativeLayout;

    FloatingActionButton fabStop;

    FloatingActionButton fabPause;

    FloatingActionButton fabStart;

    private static final String TAG = "PaperDashboardActivity";

    private static long START_TIME_IN_MILLIS;

    private CountDownTimer countDownTimer;

    private boolean timerRunning;

    private long timeLeftInMillis;

    EditText countdownText;

    MaterialProgressBar progressCountdown;
    
    AdView adView;

    boolean timeSaved = false;

    Button timeSavedButton;

    ScrollView scrollView;

    private static MediaPlayer mediaPlayer;

    Dialog timerCompleteDialog;

    Button markCompleteButton;

    TextView incompleteButton;

    ImageView closeOverlayButton;

    RelativeLayout layout;

    TextView bestTimeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_dashboard);

        Intent dashboardIntent = getIntent();

        myListActivity = new MyListActivity();

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        adView = (AdView) findViewById(R.id.adView);

        timerCompleteDialog = new Dialog(this);

        AdsManager adsManager = new AdsManager(PaperDashboardActivity.this);

        timeSavedButton = (Button) findViewById(R.id.timeSavedButton);

        bestTimeTextView = (TextView) findViewById(R.id.bestTimeTextView);


        if(!MainActivity.adsRemoved) {

            adsManager.initBannerAd();
            adsManager.loadBannerAd(adView);
        } else Log.d(TAG, "onCreate: Ads Removed");


        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        examPaperCode = dashboardIntent.getStringExtra("examPaperCode");
        examLevel = dashboardIntent.getStringExtra("examLevel");
        dateCreated = dashboardIntent.getStringExtra("dateCreated");
        subjectName = dashboardIntent.getStringExtra("examSubjectName");
        isDownloaded = dashboardIntent.getBooleanExtra("isDownloaded", false);
        position = dashboardIntent.getIntExtra("position", 0);
        Log.i("ExamPaperCode", examPaperCode + examLevel + dateCreated + isDownloaded);

        paperCodeHeading = (TextView) findViewById(R.id.paperCodeHeading);

        dateAddedTextView = (TextView) findViewById(R.id.dateAddedTextView);

        countdownText = findViewById(R.id.countdownText);

        progressCountdown = (MaterialProgressBar) findViewById(R.id.progressCountdown);

        subjectNameTextView = (TextView) findViewById(R.id.subjectNameTextView);

        examLevelTextView = (TextView) findViewById(R.id.examLevelTextView);

        isDownloadedTextView = (TextView) findViewById(R.id.isDownloadedTextView);

        findBestTime();

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        paperCodeHeading.setText(examPaperCode);

        fabStop = (FloatingActionButton) findViewById(R.id.fabStop);

        fabPause = (FloatingActionButton) findViewById(R.id.fabPause);

        fabStart = (FloatingActionButton) findViewById(R.id.fabStart);

        dateAddedTextView.setText("\t\t" + dateCreated);

        subjectNameTextView.setText("\t\t" + subjectName);

        examLevelTextView.setText("\t\t" + examLevel);

        fabStop.setEnabled(false);

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), examPaperCode + ".pdf");

        if (isDownloaded) {

            isDownloadedTextView.setText("Open");
        } else {

            isDownloadedTextView.setText("Download");
        }

        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!timerRunning && timeSaved) {

                    Log.d(TAG, "onClick: " + START_TIME_IN_MILLIS);
                    startTimer();

                    progressCountdown.setMax((Integer.parseInt(String.valueOf(START_TIME_IN_MILLIS / 1000))));
                }
                else{
                    Snackbar.make(scrollView, "Please save the time before starting the timer.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        fabPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timerRunning) {

                    pauseTimer();
                }
            }
        });

        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetTimer();
            }
        });

        updateCountDownText();

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void startTimer() {
        countdownText.setCursorVisible(false);
        timeSavedButton.setEnabled(false);
        fabStop.setEnabled(true);

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                timeLeftInMillis = START_TIME_IN_MILLIS;
                progressCountdown.setProgress(0);
                countdownText.setText("00:00");
                timerComplete();
            }
        }.start();

        timerRunning = true;

    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }

    private void resetTimer() {


        if(timerRunning) {
            pauseTimer();
            cancelTimerCheck();
        }
        else{
            resetTimerDetails();
        }

    }
    public void resetTimerDetails(){
        try{
            countDownTimer.cancel();
            countdownText.setCursorVisible(true);
            timeSavedButton.setEnabled(true);
            timeLeftInMillis = START_TIME_IN_MILLIS;
            timerRunning = false;
            updateCountDownText();
        } catch (Exception e){
            Toast.makeText(myListActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelTimerCheck(){

        new AlertDialog.Builder(PaperDashboardActivity.this)
                .setTitle("Complete Timer")
                .setMessage("Have you finished completing this paper?")
                .setPositiveButton("Yes, complete and save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        timerComplete();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void updateCountDownText() {
        int minutes = (int) timeLeftInMillis / 1000 / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        progressCountdown.setProgress((int) ((START_TIME_IN_MILLIS - timeLeftInMillis) / 1000));
        countdownText.setText(timeLeftFormatted);
    }

    public void saveTime(View view){
        String[] split = countdownText.getText().toString().split(":");
        START_TIME_IN_MILLIS = TimeUnit.MINUTES.toMillis(Long.parseLong(split[0])) + TimeUnit.SECONDS.toMillis(Long.parseLong(split[1]));
        if(START_TIME_IN_MILLIS > 0) {
            timeLeftInMillis = START_TIME_IN_MILLIS;
            timeSaved = true;
            Snackbar.make(scrollView, "Time has been saved! You can start the timer.", Snackbar.LENGTH_SHORT).show();
        }
        else{
            Snackbar.make(scrollView, "Please enter a time greater than 00:00 before saving.", Snackbar.LENGTH_SHORT).show();
        }

    }

    public void timerComplete(){
        playTimerCompleteAudio();
        showTimerCompleteOverlay();
//        showTimerCompleteNotification();

    }

    public void showTimerCompleteOverlay(){
        layout = timerCompleteDialog.findViewById(R.id.layout);
        timerCompleteDialog.setContentView(R.layout.overlay_timer_complete);
        markCompleteButton = (Button) timerCompleteDialog.findViewById(R.id.markCompleteButton);
        incompleteButton = (TextView) timerCompleteDialog.findViewById(R.id.incompleteButton);
        //closeOverlayButton = (ImageView) timerCompleteDialog.findViewById(R.id.closeOverlay);

//        closeOverlayButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //closeOverlay();
//            }
//        });

        timerCompleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timerCompleteDialog.show();
    }

    public void markAsCompleteTimer(View view){
        Log.d(TAG, "markAsCompleteTimer: Marked As Complete");
        savePaperTimer(true);
        closeOverlay();
    }

    public void markAsIncompleteTimer(View view){
        Log.d(TAG, "markAsCompleteTimer: Marked As Incomplete");
        savePaperTimer(false);
        closeOverlay();
    }

    public void savePaperTimer(boolean isCompleted){
        double initTime = (START_TIME_IN_MILLIS)/1000;
        ParseObject timedPapers = new ParseObject("TimedPapers");
        timedPapers.put("username", ParseUser.getCurrentUser().getUsername());
        timedPapers.put("paperCode", examPaperCode);
        timedPapers.put("completed", isCompleted);
        timedPapers.put("duration", initTime);

        timedPapers.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Snackbar.make(scrollView, "Timed paper saved successfully!.", Snackbar.LENGTH_LONG).show();
                    findBestTime();
                    resetTimerDetails();
                }
                else{
                    Toast.makeText(myListActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    public void findBestTime(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("TimedPapers");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("paperCode", examPaperCode);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(e == null){
                    if(objects.size() > 0){
                        bestTime = (int) objects.get(0).getNumber("duration");

                        for(ParseObject object : objects){
                            Log.d(TAG, "done: " + object.getNumber("duration"));
                            int temp = (int) object.getNumber("duration");
                            if(bestTime > temp){
                                bestTime = (int) temp;
                            }

                        }
                        int hours = (int) (bestTime / 3600);
                        int mins = (int) ((bestTime % 3600) / 60);
                        int secs = (int) bestTime % 60;

                        bestTimeTextView.setText("  " + String.format("%02d:%02d:%02d", hours, mins, secs));
                    }
                    else{
                        bestTimeTextView.setText("  No best time");

                        // No timed paper for this
                    }
                }

            }
        });
    }

    public void closeOverlay(){
        timerCompleteDialog.dismiss();

    }

    public void playTimerCompleteAudio(){
        mediaPlayer = MediaPlayer.create(this, R.raw.timercomplete);
        if(mediaPlayer != null){
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(mediaPlayer != null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
            });
            mediaPlayer.start();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    public void fileAction(View view) {


        if (isDownloaded) {
            openPdf();
        } else {

            DownloadPdf downloadPdf = new DownloadPdf(PaperDashboardActivity.this, relativeLayout);

            CodeSplitter codeSplitter = new CodeSplitter(PaperDashboardActivity.this, examPaperCode);

            codeSplitter.reverseCode();

            downloadPdf.downloadSinglePaper(codeSplitter.getUrls(), codeSplitter.getCodes(), true, true, 1, true, "List");
        }
    }

    public void openPdf() {

        // Open pdf
        Log.i("pdf file name", examPaperCode + ".pdf");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, "Open File");

        try {
            startActivity(intent);
        } catch (Exception e) {

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    public void share(View view) {

        if (!isDownloaded) {

            Snackbar.make(view, "Download file before sharing", Snackbar.LENGTH_LONG).show();
            return;
        }

        myListActivity.shareItem(position, PaperDashboardActivity.this);

    }

    public void delete(View view) {

        new AlertDialog.Builder(PaperDashboardActivity.this)
                .setTitle("Delete " + examPaperCode)
                .setMessage("Are you sure you want to delete this paper?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        myListActivity.deleteItem(position);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();

    }

    @Override
    public void onBackPressed() {
        if(timerRunning){
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure?")
                    .setMessage("Going back will cancel your current timer")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PaperDashboardActivity.super.onBackPressed();
                        }
                    }).show();
        }
        else{
            PaperDashboardActivity.super.onBackPressed();
        }


    }
}
