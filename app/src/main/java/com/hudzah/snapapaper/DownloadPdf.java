package com.hudzah.snapapaper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

public class DownloadPdf {

    Context context;

    ConnectionDetector connectionDetector;

    ParseObject papersObject;

    String subjectName;

    String examLevelFull;

    View layout;

    Boolean isQp;

    Boolean isMs;

    String fileName;

    List<String> finalUrlsToDownload;

    String downloadedBy;

    private static final String TAG = "DownloadPdf";



    public DownloadPdf(Context context, View layout){

        this.context = context;
        this.layout = layout;
    }

    public void downloadSinglePaper(List<String> urlsToDownload, List<String> fileNames, Boolean isQp, Boolean isMs, int value, Boolean singlePaper, String downloadedBy){

        connectionDetector = new ConnectionDetector(context);

        this.isMs = isMs;
        this.isQp = isQp;

        if (!connectionDetector.checkConnection()) {


            final Snackbar snackBar = Snackbar.make(layout, "You are not connected to a network", Snackbar.LENGTH_INDEFINITE);

            snackBar.setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            snackBar.dismiss();
                        }
                    }).show();


            Log.i(TAG, "Not connected");


        } else {

            if(isQp && isMs){

                urlsToDownload = urlsToDownload;
                fileNames = fileNames;

                Log.i("ArrayFiles", String.valueOf(fileNames));

            }

            else if(isQp && !isMs){

                urlsToDownload.remove(1);
               fileNames.remove(1);

                Log.i("ArrayFiles", String.valueOf(fileNames));

            }
            else if(isMs && !isQp){

                Log.i("ArrayFiles", String.valueOf(fileNames));


                urlsToDownload.remove(0);
                fileNames.remove(0);

                Log.i("ArrayFiles", String.valueOf(fileNames));

            }



            for(int i = 0; i < urlsToDownload.size(); i++ ) {

                fileName = fileNames.get(i);
                String url = urlsToDownload.get(i);

                File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File dir = new File(root.getAbsolutePath());

                if (dir.exists() == false) {
                    dir.mkdirs();
                }

                String examCodeForFile = fileName + ".pdf";

                File downloadFile = new File(dir, examCodeForFile);


                if (downloadFile.exists()) {

                    Snackbar.make(layout, "File already exists", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(context, fileName + " already exists", Toast.LENGTH_SHORT).show();
                } else {

                    try {

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

                        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        request.setMimeType("application/pdf");


                        request.setTitle(fileName);
                        request.setDescription("snapApaper");

                        request.allowScanningByMediaScanner();

                        request.setVisibleInDownloadsUi(true);

                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + ".pdf");

                        // get download service and enqueue file
                        downloadManager.enqueue(request);

                        Toast.makeText(context, "Downloading : " + fileName + ".pdf", Toast.LENGTH_LONG).show();

                        papersObject = new ParseObject("Papers");


                        finalUrlsToDownload = urlsToDownload;

                        if(!downloadedBy.equals("List")) {

                            for (int c = 0; c < urlsToDownload.size(); c++) {

                                papersObject.put("username", ParseUser.getCurrentUser().getUsername());
                                papersObject.put("paper", fileName);
                                papersObject.put("subject", CodeSplitter.subjectName); // issue
                                papersObject.put("examLevel", CodeSplitter.examLevelFull);
                                papersObject.put("downloadedBy", downloadedBy);
                                papersObject.saveInBackground();

                            }
                        }


                        if(finalUrlsToDownload.size() <= 2 && singlePaper) {

                            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                            Log.i(TAG, "Complete");
                        }



                    } catch (Exception e) {

                        Log.d("DOWNLOAD INFO", e.getMessage());
                        e.printStackTrace();
                    }


                }

            }

            if(!singlePaper){

                if(finalUrlsToDownload.size() > 8){

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent listIntent = new Intent(context, MyListActivity.class);
                            context.startActivity(listIntent);
                        }
                    }, 4500);
                }

                else if(finalUrlsToDownload.size() < 8){

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent listIntent = new Intent(context, MyListActivity.class);
                            context.startActivity(listIntent);
                        }
                    }, 2500);
                }



            }


        }
    }



    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {


            if(isQp && isMs) {

                openPdf(fileName);

            }
            else if(!isQp && isMs){

                openPdf(fileName);
            }

            else if(isQp && !isMs){

                openPdf(fileName);

            }

        }

    };

    public void onDestroy() {
        onDestroy();

        try {

            context.unregisterReceiver(onComplete);
        }catch (Exception e){

            e.getMessage();
        }
    }

    public void openPdf(String fileName){

        try {

            Log.i(TAG, "Open");

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), fileName + ".pdf");

            Log.i("pdf file name", fileName + ".pdf");

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file), "application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent intent = Intent.createChooser(target, "Open File");

            try {
                context.startActivity(intent);
            }
            catch (Exception e){

                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){

            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
