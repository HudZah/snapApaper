package com.hudzah.snapapaper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {

    TextView errorTextView;

    TextView registerButton;

    ConnectionDetector connectionDetector;

    LoadingDialog loadingDialog;

    EditText passwordEditText;

    RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        errorTextView = (TextView)findViewById(R.id.errorTextView);

        registerButton = (TextView)findViewById(R.id.registerButton);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        loadingDialog = new LoadingDialog(LoginActivity.this);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);

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


            ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {

                    if (user != null) {

                        Log.i("Signup", "Login successful");

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("username", usernameEditText.getText().toString());
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
