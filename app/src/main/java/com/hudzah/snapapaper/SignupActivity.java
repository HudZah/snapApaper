package com.hudzah.snapapaper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    EditText usernameEditText;

    EditText emailEditText;

    EditText passwordEditText;

    EditText passwordEditText2;

    EditText phoneNumber;

    TextView errorTextView;

    static ParseUser user;

    Spinner examBoardSpinner;

    ConnectionDetector connectionDetector;

    RelativeLayout view;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = (EditText)findViewById(R.id.usernameEditText);

        emailEditText = (EditText)findViewById(R.id.emailEditText);

        passwordEditText = (EditText)findViewById(R.id.passwordEditText);

        passwordEditText2 = (EditText)findViewById(R.id.passwordEditText2);

        phoneNumber = (EditText)findViewById(R.id.phoneNumber);

        errorTextView = (TextView)findViewById(R.id.errorTextView);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        examBoardSpinner = (Spinner)findViewById(R.id.examSpinner);

        loadingDialog = new LoadingDialog(SignupActivity.this);

        view = (RelativeLayout)findViewById(R.id.view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getId() == R.id.view){

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });
    }

    public void signup(View view) {


        if (!connectionDetector.checkConnection()) {

            Snackbar.make(view, "You are not connected to a network", Snackbar.LENGTH_LONG).show();
        } else {

            String itemSelected = examBoardSpinner.getSelectedItem().toString();

            if(itemSelected.equals("CIE (CAIE)")) {

                // Show loading 'Login button collapses to circle and loads'

                if (usernameEditText.getText().toString().matches("^(?=.{5,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")) {

                    if (emailEditText.getText().toString().matches("^(.+)@(.+)$")) {

                        if (passwordEditText.getText().toString().length() > 7) {

                            if (passwordEditText2.getText().toString().matches(passwordEditText.getText().toString())) {

                                if (phoneNumber.getText().toString().length() == 10 || phoneNumber.getText().toString().matches("")) {

                                    loadingDialog.startLoadingDialog();

                                    ParseUser.logOut();

                                    user = new ParseUser();

                                    user.setUsername(usernameEditText.getText().toString());
                                    user.setPassword(passwordEditText.getText().toString());
                                    user.setEmail(emailEditText.getText().toString());
                                    user.put("phoneNumber", phoneNumber.getText().toString());
                                    user.put("examBoard", itemSelected);
                                    user.put("package", "Free");
//                                    user.put("dailyRemaining", "5");
//                                    user.put("monthlyRemaining", "30");

                                    user.signUpInBackground(new SignUpCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                            if (e == null) {

                                                // Stop loading

                                                Log.i("Signup", "Successful");

                                                Intent packageActivity = new Intent(getApplicationContext(), PackageSelectorActivity.class);
                                                packageActivity.putExtra("username", usernameEditText.getText().toString());
                                                packageActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(packageActivity);
                                            } else {

                                                errorTextView.setText(e.getMessage());
                                            }

                                            loadingDialog.dismissDialog();
                                        }
                                    });
                                } else {

                                    errorTextView.setText("Phone number has to be 10 digits long");
                                }
                            } else {

                                Log.i("password", passwordEditText2.getText().toString() + passwordEditText.getText().toString());
                                errorTextView.setText("Passwords are not matching");
                            }
                        } else {

                            errorTextView.setText("Password must be atleast 8 characters in length");
                        }
                    } else {

                        errorTextView.setText("Email does not match a valid format");
                    }
                } else {

                    errorTextView.setText("Username must be more than 5 and less than 20 characters");
                }
            }
            else{

                if (emailEditText.getText().toString().matches("^(.+)@(.+)$")) {

                    new AlertDialog.Builder(SignupActivity.this)
                            .setTitle("Not Available Yet")
                            .setMessage("Sorry, " + itemSelected + " is not available yet. Would like to receive updates for when we release snapApaper for " + itemSelected + "?")
                            .setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    LoadingDialog loadingDialog = new LoadingDialog(SignupActivity.this);

                                    loadingDialog.startLoadingDialog();

                                    ParseObject object = new ParseObject("ComingSoonList");

                                    object.put("email", emailEditText.getText().toString());
                                    object.put("examBoard", itemSelected);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e != null){

                                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                            else{

                                                loadingDialog.dismissDialog();

                                                new AlertDialog.Builder(SignupActivity.this)
                                                        .setTitle("Thank You!")
                                                        .setMessage("Thank you! We will inform you once we release our app for your exam board")
                                                        .setPositiveButton("OK", null)
                                                        .show();

                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Not now", null)
                            .show();
                }
                else{

                    errorTextView.setText("Email does not match a valid format");
                }


            }


        }
    }

    public void login(View view){

        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
    }

}
