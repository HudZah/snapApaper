package com.hudzah.snapapaper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.hudzah.snapapaper.MainActivity.todayDate;

public class MyListActivity extends AppCompatActivity {

    ListView listView;

    static ArrayAdapter<String> arrayAdapter;

    ArrayList<String> arrayList;

    String key;

    CharSequence ago;

    AlertDialog.Builder choiceBuilder;

    HashMap<String, String> listItemsMap;

    LoadingDialog loadingDialog;

    ConnectionDetector connectionDetector;

    RelativeLayout view;

    String paperSelected;

    EditText search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        loadingDialog = new LoadingDialog(this);

        connectionDetector = new ConnectionDetector(this);

        view = (RelativeLayout)findViewById(R.id.view);

        TextView emptyText = (TextView)findViewById(R.id.emptyText);

        search = (EditText)findViewById(R.id.search);


        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        listView = (ListView)findViewById(R.id.listView);

        arrayList = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter<>(MyListActivity.this, android.R.layout.simple_list_item_1, arrayList);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                (MyListActivity.this).arrayAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i("Paper deets" ,arrayList.get(position));

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), arrayList.get(position) + ".pdf");

                if(file.exists()){

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
                else{

                    Snackbar.make(view, "Paper may have been deleted or moved", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                choiceBuilder = new AlertDialog.Builder(MyListActivity.this);

                paperSelected = arrayList.get(position);

                String[] items = getResources().getStringArray(R.array.paper_options);
                choiceBuilder.setTitle("Select an option for \n" + arrayList.get(position));
                choiceBuilder.setCancelable(true);
                choiceBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {

                            // Share
                            share(position);
                        } else if (which == 1) {

                            // Are you sure you want to Dialog
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), arrayList.get(position) + ".pdf");
                            Log.i("FilePath", String.valueOf(file));
                            arrayList.remove(position);
                            arrayAdapter.notifyDataSetChanged();

                            // remove from db

                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Papers");
                            query.whereEqualTo("paper", paperSelected);
                            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {

                                        if (objects.size() > 0) {

                                            for (ParseObject object : objects) {

                                                object.deleteInBackground();
                                                Log.i("Database paper", "Deleted");
                                            }
                                        }
                                    }
                                }
                            });

                            if (file.exists()) {

                                file.delete();
                            }

                        } else if (which == 2) {

                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), arrayList.get(position) + ".pdf");

                            Date creationDate = new Date(file.lastModified());

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                            new AlertDialog.Builder(MyListActivity.this)
                                    .setTitle("Details for " + arrayList.get(position))
                                    .setMessage("File was created on \n\n" + sdf.format(creationDate))
                                    .setPositiveButton("OK", null)
                                    .setNegativeButton("No", null)
                                    .show();
                        }
                    }
                }); choiceBuilder.show();


                return true;
            }
        });

        if(connectionDetector.checkConnection()) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Papers");
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.orderByDescending("createdAt");


            loadingDialog.startLoadingDialog();
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null) {

                        if (objects.size() > 0) {

                            for (ParseObject object : objects) {

                                listView.setVisibility(View.VISIBLE);
                                emptyText.setVisibility(View.GONE);
                                Log.i("Paper", "" + object.getString("paper"));
                                arrayList.add(object.getString("paper"));
                                Log.i("Paper", arrayList.get(0));


                            }
                            arrayAdapter.notifyDataSetChanged();

                        }
                    }

                    listView.setAdapter(arrayAdapter);
                    loadingDialog.dismissDialog();
                }
            });
        }
        else{

            final Snackbar snackBar = Snackbar.make(view, "You are not connected to a network", Snackbar.LENGTH_INDEFINITE);

            snackBar.setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            snackBar.dismiss();
                        }
                    }).show();


            Log.i("Internet", "Not connected");
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void share(int position){

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), arrayList.get(position) + ".pdf");

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                final String AUTHORITY = view.getContext().getPackageName() + ".fileprovider";

                Uri contentUri = FileProvider.getUriForFile(view.getContext(), AUTHORITY, file);

                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.setType("application/pdf");
                view.getContext().startActivity(Intent.createChooser(shareIntent, "Share audio via"));

            } else {

                final Intent shareIntent = new Intent(Intent.ACTION_SEND);

                Uri fileUri = Uri.parse(file.getAbsolutePath());

                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.setType("application/pdf");
                view.getContext().startActivity(Intent.createChooser(shareIntent, "Share audio via"));

            }
        }
        catch (Exception e){

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
