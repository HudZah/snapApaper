package com.hudzah.snapapaper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;
import static com.sun.activation.registries.LogSupport.log;

public class LoginActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 1;
    TextView errorTextView;

    TextView registerButton;

    ConnectionDetector connectionDetector;

    LoadingDialog loadingDialog;

    EditText passwordEditText;

    AppUpdateManager appUpdateManager;

    ScrollView relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        errorTextView = (TextView)findViewById(R.id.errorTextView);

        registerButton = (TextView)findViewById(R.id.registerButton);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        loadingDialog = new LoadingDialog(LoginActivity.this);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        relativeLayout = (ScrollView)findViewById(R.id.relativeLayout);

        checkForUpdates();

        if(ParseUser.getCurrentUser() != null){

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("username", ParseUser.getCurrentUser().getUsername());
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerIntent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(registerIntent);
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(v.getId() == R.id.relativeLayout){

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });

        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){

                    login(v);
                }
                return false;
            }
        });
    }

    private void checkForUpdates() {

        appUpdateManager = AppUpdateManagerFactory.create(LoginActivity.this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {

                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            IMMEDIATE,
                            this,
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            IMMEDIATE,
                                            this,
                                            MY_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
    }


    public void login(View view) {

        loadingDialog.startLoadingDialog();

        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);

        if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {

            loadingDialog.dismissDialog();

            errorTextView = (TextView) findViewById(R.id.errorTextView);
            errorTextView.setText("A username and password are required");
        } else if (!connectionDetector.checkConnection()) {

            loadingDialog.dismissDialog();
            Snackbar.make(view, "You are not connected to a network", Snackbar.LENGTH_LONG).show();
        }
        else{

//            ParseObject userChanges = new ParseObject("UserChanges");


            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            ParseUser.logInInBackground(username,  password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {

                    if (user != null) {

                        Log.i("Signup", "Login successful");

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("username", username);
                        startActivity(intent);

                    } else {

                        errorTextView.setText(e.getMessage());
                    }
                }
            });


            loadingDialog.dismissDialog();

        }

    }

    public void help(View view){

        Intent forgotPasswordIntent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(forgotPasswordIntent);
    }


}
