package com.hudzah.snapapaper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    TextView errorTextView;

    TextView registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        errorTextView = (TextView)findViewById(R.id.errorTextView);

        registerButton = (TextView)findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerIntent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    public void login(View view){

        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);

        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        if(usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")){

            errorTextView = (TextView)findViewById(R.id.errorTextView);
            errorTextView.setText("A username and password are required");
        }

        else{

            ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {

                    if(user != null){

                        Log.i("Signup", "Login successful");

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    else{

                        errorTextView.setText(e.getMessage());
                    }
                }
            });


        }
    }
}
