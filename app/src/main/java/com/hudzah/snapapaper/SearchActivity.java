package com.hudzah.snapapaper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    Spinner spinnerExamBoard;

    Spinner spinnerSubject;

    Spinner spinnerSession;

    Spinner spinnerYear;

    EditText paperNumberEditText;

    ArrayList<String> arrayListAL;

    ArrayList<String> arrayListOL;

    ArrayList<String> arrayListIG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        spinnerExamBoard = (Spinner)findViewById(R.id.spinnerExamBoard);

        spinnerSubject = (Spinner)findViewById(R.id.spinnerSubject);

        spinnerSession = (Spinner)findViewById(R.id.spinnerSession);

        spinnerYear = (Spinner)findViewById(R.id.spinnerYear);

        paperNumberEditText = (EditText)findViewById(R.id.paperNumberEditText);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.examboard_spinner, android.R.layout.simple_spinner_dropdown_item);

        spinnerExamBoard.setAdapter(arrayAdapter);

        ArrayAdapter<CharSequence> arrayAdapterExamLevel = ArrayAdapter.createFromResource(this, R.array.exam_level_spinner, android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> arrayAdapterSession = ArrayAdapter.createFromResource(this, R.array.session_spinner_cambridge, android.R.layout.simple_spinner_dropdown_item);

        spinnerSession.setAdapter(arrayAdapterSession);

        ArrayAdapter<CharSequence> arrayAdapterYear = ArrayAdapter.createFromResource(this, R.array.years_spinner, android.R.layout.simple_spinner_dropdown_item);

        spinnerYear.setAdapter(arrayAdapterYear);

        HashMap<String, String> examCodesMap = (HashMap<String, String>) MainActivity.examCodesMap;

        //HashMap<String, String> examCodesMap = new HashMap<String, String>();


        ArrayList<String> arrayList = new ArrayList<>();

        ArrayList<String> examCodesArrayList = new ArrayList<String>();

        for(Map.Entry<String, String> entry : examCodesMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            arrayList.add(value);
            examCodesArrayList.add(key);

            Log.i("Test value", value);
        }

        ArrayAdapter<String> arrayAdapterSubject = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);

        spinnerSubject.setAdapter(arrayAdapterSubject);

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

    public void search(View view){


    }
}
