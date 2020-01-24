package com.hudzah.snapapaper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.hudzah.snapapaper.MainActivity.todayDate;

public class MyListActivity extends AppCompatActivity {

    ListView listView;

    ArrayAdapter<String> arrayAdapter;

    ArrayList<String> arrayList;

    ArrayList<String> downloadTimes;

    String key;

    CharSequence ago;

    HashMap<String, String> listItemsMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        listView = (ListView)findViewById(R.id.listView);

        arrayList = new ArrayList<String>();

        downloadTimes = new ArrayList<String>();

        listItemsMap = new HashMap<>();

        HashMap<String, String> myListMap = (HashMap<String, String>) MainActivity.myListMap;

        List<HashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[]{"First line", "Second line"},
                new int[]{R.id.mainText, R.id.subText});


        if(myListMap.size() > 0 ) {

            for (Map.Entry<String, String> entry : myListMap.entrySet()) {

                key = entry.getKey();
                String value = entry.getValue();

                arrayList.add(value);
                downloadTimes.add(key);

                listItemsMap.put("First line", value);

                if(key != null) {

                    listItemsMap.put("Second line", (String) key);

                }
                else{

                    listItemsMap.put("Second line", "Just now");
                }


                listItems.add(listItemsMap);
            }
        }

        else {

            Log.i("wfawfawfagaw", "EMpty");
        }

        listView.setAdapter(adapter);



        // toolbar
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
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
