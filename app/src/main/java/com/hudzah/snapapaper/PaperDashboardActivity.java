package com.hudzah.snapapaper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.health.TimerStat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.Locale;
import java.util.Timer;

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

    RelativeLayout relativeLayout;

    FloatingActionButton fabStop;

    FloatingActionButton fabPause;

    FloatingActionButton fabStart;

    private static final long START_TIME_IN_MILLIS = 60000;

    private CountDownTimer countDownTimer;

    private boolean timerRunning;

    private long timeLeftInMillis = START_TIME_IN_MILLIS;

    TextView countdownText;

    MaterialProgressBar progressCountdown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_dashboard);

        Intent dashboardIntent = getIntent();

        myListActivity = new MyListActivity();

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

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        paperCodeHeading.setText(examPaperCode);

        fabStop = (FloatingActionButton) findViewById(R.id.fabStop);

        fabPause = (FloatingActionButton) findViewById(R.id.fabPause);

        fabStart = (FloatingActionButton) findViewById(R.id.fabStart);

        dateAddedTextView.setText("\t\t" + dateCreated);

        subjectNameTextView.setText("\t\t" + subjectName);

        examLevelTextView.setText("\t\t" + examLevel);

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), examPaperCode + ".pdf");

        if (isDownloaded) {

            isDownloadedTextView.setText("Open");
        } else {

            isDownloadedTextView.setText("Download");
        }

        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timerRunning) {

                } else {
                    startTimer();
                    progressCountdown.setMax((Integer.parseInt(String.valueOf(START_TIME_IN_MILLIS / 1000))));

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

            }
        }.start();

        timerRunning = true;

    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }

    private void resetTimer() {
        countDownTimer.cancel();
        timeLeftInMillis = START_TIME_IN_MILLIS;
        timerRunning = false;
        updateCountDownText();
    }

    private void updateCountDownText() {
        int minutes = (int) timeLeftInMillis / 1000 / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        progressCountdown.setProgress((int) ((START_TIME_IN_MILLIS - timeLeftInMillis) / 1000));
        countdownText.setText(timeLeftFormatted);
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


}
