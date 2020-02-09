package com.hudzah.snapapaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button resetPasswordButton;
    EditText passwordEmail;
    TextView errorTextView;
    RelativeLayout layout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetPasswordButton = (Button)findViewById(R.id.resetPasswordButton);
        passwordEmail = (EditText) findViewById(R.id.passwordEmail);
        errorTextView = (TextView)findViewById(R.id.errorTextView);
        layout = (RelativeLayout)findViewById(R.id.layout);
        errorTextView.setText("");

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getId() == R.id.layout){

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = passwordEmail.getText().toString().trim();

                ConnectionDetector connectionDetector = new ConnectionDetector(ForgotPasswordActivity.this);

                if (connectionDetector.checkConnection()) {

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("email", email);
                    query.setLimit(1);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {

                                if (objects.size() > 0) {

                                    for (ParseUser object : objects) {

                                        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                                            @Override
                                            public void done(ParseException e) {

                                                if(e == null){

                                                    Intent intent = new Intent(ForgotPasswordActivity.this, EnterCodeActivity.class);
                                                    startActivity(intent);

                                                }
                                                else{


                                                }
                                            }
                                        });

                                    }
                                } else {

                                    errorTextView.setText("This email is not a registered email");
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




}



