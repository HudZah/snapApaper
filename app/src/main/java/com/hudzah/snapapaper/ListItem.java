package com.hudzah.snapapaper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ListItem {
    private String mSubjectName;
    private String mExamPaperCode;
    private String mExamLevel;
    private String mDateCreated;
    MyListActivity myListActivity;
    Boolean isDownloaded = false;


    public ListItem(String subjectName, String examPaperCode, String examLevel, Date createdAt){

        mSubjectName = subjectName;
        mExamPaperCode = examPaperCode;
        mExamLevel = examLevel;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        mDateCreated = sdf.format(createdAt);


    }

    public void showDashboard(int position, Context context, RecyclerView recyclerView){

        // Open pdf
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), mExamPaperCode + ".pdf");
        Log.i("pdf file name", mExamPaperCode + ".pdf");

        if(file.exists()) isDownloaded = true;

        else isDownloaded = false;

        Log.i("Downloaded", isDownloaded.toString());


        if(isDownloaded && file.length() <  21000) {

            Snackbar snackbar = Snackbar.make(recyclerView, "Paper might be corrupted", Snackbar.LENGTH_LONG);

            snackbar.setAction("Delete", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    myListActivity.deleteItem(position);
                }
            }).show();
        }

        else{

            Intent dashboardIntent = new Intent(context, PaperDashboardActivity.class);
            dashboardIntent.putExtra("examPaperCode", mExamPaperCode);
            dashboardIntent.putExtra("examLevel", mExamLevel);
            dashboardIntent.putExtra("dateCreated", mDateCreated);
            dashboardIntent.putExtra("isDownloaded", isDownloaded);
            dashboardIntent.putExtra("examSubjectName", mSubjectName);
            dashboardIntent.putExtra("position", position);
            context.startActivity(dashboardIntent);
        }




    }



    public void fileOptions(int position, Context context){

        AlertDialog.Builder choiceBuilder = new AlertDialog.Builder(context);

        String[] items = context.getResources().getStringArray(R.array.paper_options);

        choiceBuilder.setCancelable(true);
        choiceBuilder.setTitle("Select an option for this file");
        choiceBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), mExamPaperCode + ".pdf");

                if(which == 0) {

                    sharePdf(position, context);
                }

                else if(which == 1){

                    new AlertDialog.Builder(context)
                            .setTitle("Date created for " + mExamPaperCode)
                            .setMessage("Created on " + mDateCreated)
                            .setPositiveButton("OK", null)
                            .show();
                }

            }
        }).show();
    }

    public void sharePdf(int position, Context context){

        final Handler handler = new Handler();

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), mExamPaperCode + ".pdf");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Share
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                    final String AUTHORITY = context.getPackageName() + ".fileprovider";

                    Uri contentUri = FileProvider.getUriForFile(context, AUTHORITY, file);

                    final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    shareIntent.setType("application/pdf");
                    context.startActivity(Intent.createChooser(shareIntent, "Share file via"));

                } else {

                    final Intent shareIntent = new Intent(Intent.ACTION_SEND);

                    Uri fileUri = Uri.parse(file.getAbsolutePath());


                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    shareIntent.setType("application/pdf");
                    context.startActivity(Intent.createChooser(shareIntent, "Share file via"));

                }

            }

        }, 200);
    }

    public void deleteFile(int position, Context context){


        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),  mExamPaperCode + ".pdf");

        Log.i("FileName", String.valueOf(file));


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Papers");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("paper", mExamPaperCode);
        query.setLimit(1);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){

                    if(objects.size() > 0){

                        for (ParseObject object : objects){

                            object.deleteInBackground();
                            savedDeletedItem(mExamPaperCode, context);

                            if(file.exists()){

                                file.delete();
                            }
                        }
                    }
                }
                else{

                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getSubjectName(){

        return mSubjectName;
    }
    public String getExamCode(){

        return mExamPaperCode;
    }
    public String getExamLevel(){

        return mExamLevel;
    }

    public void savedDeletedItem(String mExamPaperCode, Context context){

        ParseObject object = new ParseObject("DeletedPapers");

        object.put("username", ParseUser.getCurrentUser().getUsername());
        object.put("session", ParseUser.getCurrentUser().getSessionToken());
        object.put("paper", mExamPaperCode);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if(e == null){

                    Log.i("SavedItem", "Saved item to Parse");

                }
                else{

                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
