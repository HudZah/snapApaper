package com.hudzah.snapapaper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText newPasswordEditText;
    Button changePasswordButton;
    TextView errorTextView;
    RelativeLayout layout;
    String initialUsername;
    String initialEmail;
    String username;
    String password;
    String emailFromUser;
    String selectedPackage;
    String phoneNumber;
    String examBoard;
    String monthlyRemaining;
    String dailyRemaining;
    String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        newPasswordEditText = (EditText) findViewById(R.id.newPasswordEditText);

        changePasswordButton = (Button) findViewById(R.id.changePasswordButton);

        errorTextView = (TextView) findViewById(R.id.errorTextView);

        layout = (RelativeLayout) findViewById(R.id.layout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getId() == R.id.layout){

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });

        SharedPreferences sharedPreferences = ChangePasswordActivity.this.getSharedPreferences("com.hudzah.snapapaper", Context.MODE_PRIVATE);

        String email = sharedPreferences.getString("email", "");

        ConnectionDetector connectionDetector = new ConnectionDetector(ChangePasswordActivity.this);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (connectionDetector.checkConnection()) {

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("email", email);
                    query.setLimit(1);

                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {

                            if(e == null){

                                Log.i("Password", newPasswordEditText.getText().toString());


                                if(objects.size() > 0){

                                    for(ParseUser object : objects){

                                        if(newPasswordEditText.getText().toString().length() > 7){

                                            initialUsername = object.getUsername().toString();

                                            ParseObject userChanges = new ParseObject("UserChanges");

                                            userChanges.put("initialUsername", initialUsername);
                                            userChanges.put("updatedPassword", newPasswordEditText.getText().toString());


                                            userChanges.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {

                                                        Toast.makeText(ChangePasswordActivity.this, "Password has been changed!", Toast.LENGTH_SHORT).show();

                                                    } else {

                                                        Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }


                                            });

                                            username = object.getUsername();
                                            password = newPasswordEditText.getText().toString();
                                            emailFromUser = object.getEmail();
                                            selectedPackage = object.get("package").toString();
                                            phoneNumber = object.get("phoneNumber").toString();
                                            examBoard = object.get("examBoard").toString();
                                            monthlyRemaining = object.get("monthlyRemaining").toString();
                                            dailyRemaining = object.get("dailyRemaining").toString();
                                            objectId = object.getObjectId().toString();

                                           deleteUser();

                                        }
                                        else{

                                            errorTextView.setText("Password has to be longer than 7 characters");
                                        }
                                    }
                                }
                            }
                        }
                    });

                }

                else{

                    final Snackbar snackBar = Snackbar.make(layout, "You are not connected to a network", Snackbar.LENGTH_INDEFINITE);

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
        });


    }

    public void deleteUser(){

        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereEqualTo("objectId", objectId);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if(e == null){

                    objects.get(0).deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {

                            if(e == null){

                                Log.i("Deleted", "User successfully deleted");
                                Log.i("Deleted", "New User Attrs " +  username + password + emailFromUser + selectedPackage
                                        + phoneNumber + examBoard + monthlyRemaining + dailyRemaining);

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                overridePendingTransition(0, 0);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                            }

                            else{

                                Log.i("Deleted", e.getMessage());
                            }
                        }
                    });
                }
            }
        });


    }
}
