package com.hudzah.snapapaper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.Distribution;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.hudzah.snapapaper.MainActivity.todayDate;

public class MyListActivity extends AppCompatActivity {

    static ArrayAdapter<String> arrayAdapter;
    
    String key;

    CharSequence ago;

    AlertDialog.Builder choiceBuilder;

    HashMap<String, String> listItemsMap;

    LoadingDialog loadingDialog;

    ConnectionDetector connectionDetector;

    static RelativeLayout view;

    String paperSelected;

    private static final String TAG = "MyListActivity";

    EditText search;

    private RecyclerView recyclerView;

    static private RecyclerViewAdapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    static ArrayList<ListItem> listItems;

    TextView emptyView;

    ImageView paperIcon;

    FloatingActionButton fab;

    Dialog help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        loadingDialog = new LoadingDialog(this);

        connectionDetector = new ConnectionDetector(this);

        view = (RelativeLayout)findViewById(R.id.view);

        listItems = new ArrayList<>();

        emptyView = (TextView) findViewById(R.id.textV);

        paperIcon = (ImageView) findViewById(R.id.paperIcon);

        paperIcon.setVisibility(View.GONE);

        emptyView.setVisibility(View.GONE);

        help = new Dialog(this);

        fab = (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabOnClick();
            }
        });


        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



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

                                Log.d(TAG, "done: preparing items");


                                listItems.add(new ListItem(object.getString("subject"), object.getString("paper"),object.getString("examLevel")));


                                recyclerView = findViewById(R.id.recycler_view);
                                layoutManager = new LinearLayoutManager(MyListActivity.this);
                                adapter = new RecyclerViewAdapter(listItems);

                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(adapter);
                                adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                                    @Override
                                    public void OnItemClick(int position) {

                                        openPdf(position);
                                    }

                                    @Override
                                    public void OnDeleteClick(int position) {

                                        deleteItem(position);
                                    }

                                    @Override
                                    public void OnLongClick(int position) {
                                        fileOptions(position);
                                    }
                                });


                            }

                        }
                        else{

                            emptyView.setVisibility(View.VISIBLE);
                            paperIcon.setVisibility(View.VISIBLE);
                        }
                    }

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

    public void openPdf(int position){

        listItems.get(position).openPdf(position, MyListActivity.this, recyclerView);
    }

    public void deleteItem(int position){

        listItems.get(position).deleteFile(position, MyListActivity.this);

        listItems.remove(position);

        adapter.notifyItemRemoved(position);
    }

    public void fileOptions(int position){

        listItems.get(position).fileOptions(position, MyListActivity.this);
    }

    public void fabOnClick(){

        Intent cameraIntent = new Intent(MyListActivity.this, MainActivity.class);
        startActivity(cameraIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        if(item.getItemId() == R.id.action_help){

            help.setContentView(R.layout.overlay_list_help);

            ImageView closeOverlay = (ImageView) help.findViewById(R.id.closeOverlay);

            Log.i(TAG, "Help");

            closeOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    help.dismiss();
                }
            });

            help.show();
        }

        return super.onOptionsItemSelected(item);
    }


}
