package com.hudzah.snapapaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button resetPasswordButton;
    EditText passwordEmail;
    TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetPasswordButton = (Button)findViewById(R.id.resetPasswordButton);
        passwordEmail = (EditText) findViewById(R.id.passwordEmail);
        errorTextView = (TextView)findViewById(R.id.errorTextView);
        errorTextView.setText("");

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = passwordEmail.getText().toString().trim();

                if(email.equals("")){

                    errorTextView.setText("Please enter a valid email");
                }
                else{

                   ParseUser.requestPasswordResetInBackground("chiefhudhayfa@gmail.com", new RequestPasswordResetCallback() {
                       @Override
                       public void done(ParseException e) {

                           if(e == null){

                               Toast.makeText(ForgotPasswordActivity.this, "An email was sent to your email address to reset your password", Toast.LENGTH_SHORT).show();

                           }
                           else{
                                Log.i("error", e.getMessage());

                               errorTextView.setText("Please enter a valid email address");
                           }
                       }
                   });
                }
            }
        });
    }
}
