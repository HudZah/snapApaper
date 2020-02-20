package com.hudzah.snapapaper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ListItem {
    private String mSubjectName;
    private String mExamPaperCode;
    private String mExamLevel;

    public ListItem(String subjectName, String examPaperCode, String examLevel){

        mSubjectName = subjectName;
        mExamPaperCode = examPaperCode;
        mExamLevel = examLevel;

    }

    public void openPdf(int position, Context context, RecyclerView recyclerView){

        // Open pdf
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), mExamPaperCode + ".pdf");
        Log.i("pdf file name", mExamPaperCode + ".pdf");
        MyListActivity myListActivity = new MyListActivity();

        if(file.exists()) {

            if(file.length() >  21000) {

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
            else{

                Snackbar snackbar = Snackbar.make(recyclerView, "Paper maybe corrupted", Snackbar.LENGTH_LONG);

                snackbar.setAction("Delete", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        myListActivity.deleteItem(position);
                    }
                }).show();
            }
        }
        else{

            Snackbar.make(recyclerView, "File may have been moved or deleted", Snackbar.LENGTH_LONG).show();
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


                if(which == 0){

                    // Share
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){

                        final String AUTHORITY = context.getPackageName() + ".fileprovider";

                        Uri contentUri = FileProvider.getUriForFile(context, AUTHORITY, file);

                        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        shareIntent.setType("application/pdf");
                        context.startActivity(Intent.createChooser(shareIntent, "Share file via"));

                    }
                    else{

                        final Intent shareIntent = new Intent(Intent.ACTION_SEND);

                        Uri fileUri = Uri.parse(file.getAbsolutePath());

                        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                        shareIntent.setType("application/pdf");
                        context.startActivity(Intent.createChooser(shareIntent, "Share file via"));

                    }
                }

                else if(which == 1){

                    // Details
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Papers");
                    query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    query.whereEqualTo("paper", mExamPaperCode);
                    query.setLimit(1);

                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if(e == null){
                                if(objects.size() > 0){

                                    for(ParseObject object: objects){

                                        Date date = object.getCreatedAt();

                                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                                        new AlertDialog.Builder(context)
                                                .setTitle("Date created for " + mExamPaperCode)
                                                .setMessage("Created on " + sdf.format(date))
                                                .setPositiveButton("OK", null)
                                                .show();

                                    }
                                }
                            }
                        }
                    });

                }

            }
        }).show();
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

                            if(file.exists()){

                                file.delete();
                            }
                        }
                    }
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
}
