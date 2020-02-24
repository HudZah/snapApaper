package com.hudzah.snapapaper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class SettingsEditProfile extends AppCompatActivity {

    EditText updatedUsernameEditText;

    EditText updatedEmailEditText;

    Spinner updatedExamBoardSpinner;

    String currentExamBoard;

    ScrollView relativeLayout;

    ArrayAdapter<String> spinnerAdapter;

    ConnectionDetector connectionDetector;

    LoadingDialog loadingDialog;

    TextView errorTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_edit_profile);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        updatedUsernameEditText = (EditText) findViewById(R.id.updatedUsername);

        updatedEmailEditText = (EditText) findViewById(R.id.updatedEmail);

        updatedExamBoardSpinner = (Spinner) findViewById(R.id.updatedExamBoard);

        relativeLayout = (ScrollView) findViewById(R.id.relativeLayout);

        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.setLimit(1);

        connectionDetector = new ConnectionDetector(SettingsEditProfile.this);

        loadingDialog = new LoadingDialog(SettingsEditProfile.this);

        errorTextView = (TextView) findViewById(R.id.errorTextView);

        if(connectionDetector.checkConnection()) {

            loadingDialog.startLoadingDialog();

            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {

                    if (e == null) {

                        if (objects.size() > 0) {

                            for (ParseObject object : objects) {

                                updatedUsernameEditText.setText(ParseUser.getCurrentUser().getUsername());

                                updatedEmailEditText.setText(ParseUser.getCurrentUser().getEmail());

                                currentExamBoard = String.valueOf(object.get("examBoard"));

                                Log.i("Board", currentExamBoard);

                                String[] arraySpinner = new String[] {currentExamBoard,  "CIE (CAIE)", "Edexcel", "AQA"};

//                               , "OCR", "CCEA", "IB", "Local For SL"

                                spinnerAdapter  = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arraySpinner);

                                updatedExamBoardSpinner.setAdapter(spinnerAdapter);

                                loadingDialog.dismissDialog();
                                
                            }
                        }
                    } else {

                        Toast.makeText(SettingsEditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else{

            final Snackbar snackBar = Snackbar.make(relativeLayout, "You are not connected to a network", Snackbar.LENGTH_INDEFINITE);

            snackBar.setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            snackBar.dismiss();
                        }
                    }).show();


            Log.i("Internet", "Not connected");
        }


        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveChanges(View view){




        if(connectionDetector.checkConnection()){

            loadingDialog.startLoadingDialog();

            ParseQuery<ParseUser> query = ParseUser.getQuery();

            String newUsername = String.valueOf(updatedUsernameEditText.getText());

            String newExamBoard = String.valueOf(updatedExamBoardSpinner.getSelectedItem());

            // Check if the new username already exists on server
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {

                    if(e == null){

                        for(ParseUser object : objects){

                            object.setUsername(newUsername);
                            object.put("examBoard", newExamBoard);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    loadingDialog.dismissDialog();

                                    if(e == null){

                                        // Changes saved
                                        final Snackbar snackBar = Snackbar.make(relativeLayout, "Changes saved! Please re-login", Snackbar.LENGTH_INDEFINITE);

                                        snackBar.setAction("OK",
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        snackBar.dismiss();
                                                        ParseUser.logOut();
                                                        Intent intent = new Intent(SettingsEditProfile.this, LoginActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }).show();


                                    }
                                    else{

                                        errorTextView.setText(e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                }
            });


        }
        else{

            final Snackbar snackBar = Snackbar.make(relativeLayout, "You are not connected to a network", Snackbar.LENGTH_INDEFINITE);

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
}
