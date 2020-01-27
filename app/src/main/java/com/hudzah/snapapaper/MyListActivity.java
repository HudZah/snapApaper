package com.hudzah.snapapaper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    HashMap<String, String> listItemsMap;

    LoadingDialog loadingDialog;

    ConnectionDetector connectionDetector;

    RelativeLayout view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        loadingDialog = new LoadingDialog(this);

        connectionDetector = new ConnectionDetector(this);

        view = (RelativeLayout)findViewById(R.id.view);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        listView = (ListView)findViewById(R.id.listView);

        arrayList = new ArrayList<String>();

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

                                Log.i("Paper", "" + object.getString("paper"));
                                arrayList.add(object.getString("paper"));
                                Log.i("Paper", arrayList.get(0));
                                arrayAdapter = new ArrayAdapter<>(MyListActivity.this, android.R.layout.simple_list_item_1, arrayList);
                                arrayAdapter.notifyDataSetChanged();
                            }

                            listView.setAdapter(arrayAdapter);
                            loadingDialog.dismissDialog();
                        }
                    }
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
}
