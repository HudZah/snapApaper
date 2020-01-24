package com.hudzah.snapapaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    EditText usernameEditText;

    EditText emailEditText;

    EditText passwordEditText;

    EditText passwordEditText2;

    EditText phoneNumber;

    TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = (EditText)findViewById(R.id.usernameEditText);

        emailEditText = (EditText)findViewById(R.id.emailEditText);

        passwordEditText = (EditText)findViewById(R.id.passwordEditText);

        passwordEditText2 = (EditText)findViewById(R.id.usernameEditText);

        phoneNumber = (EditText)findViewById(R.id.phoneNumber);

        errorTextView = (TextView)findViewById(R.id.errorTextView);
    }

    public void signup(View view){

        if(usernameEditText.getText().toString().matches("^(?=.{5,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")){

            if(emailEditText.getText().toString().matches("^(.+)@(.+)$")){

                if(passwordEditText.getText().toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$\n")){

                    if(passwordEditText2.getText().toString().equals(passwordEditText.getText().toString())){

                        if(phoneNumber.getText().toString().length() == 10){


                            ParseUser user = new ParseUser();

                            user.setUsername(usernameEditText.getText().toString());
                            user.setPassword(passwordEditText.getText().toString());

                            user.signUpInBackground(new SignUpCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if(e == null){

                                        Log.i("Signup", "Successful");
                                    }
                                    else{

                                        errorTextView.setText(e.getMessage());
                                    }
                                }
                            });
                        }
                        else {

                            errorTextView.setText("Phone number has to be 10 digits long");
                        }
                    }
                    else{

                        errorTextView.setText("Passwords are not matching");
                    }
                }
                else{

                    errorTextView.setText("Password must be 8 characters in length, it has to include atleast one uppercase, lowercase letter and a number");
                }
            }
            else{

                errorTextView.setText("Email does not match a valid format");
            }
        }

        else{

            errorTextView.setText("Username must be more than 5 and less than 20 characters");
        }





    }
}
