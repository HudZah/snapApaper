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

public class EnterCodeActivity extends AppCompatActivity {

    EditText keyEditText;

    Button submitCodeButton;

    TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);

        submitCodeButton = (Button) findViewById(R.id.submitCodeButton);

        SharedPreferences sharedPreferences = EnterCodeActivity.this.getSharedPreferences("com.hudzah.snapapaper", Context.MODE_PRIVATE);

        keyEditText = (EditText) findViewById(R.id.keyEditText);

        errorTextView = (TextView) findViewById(R.id.errorTextView);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getId() == R.id.layout){

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });

        submitCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String keyFromText = keyEditText.getText().toString();

                String key = sharedPreferences.getString("key", "");

                Log.i("Key", key + " Key from user " + keyFromText);

                if(key.equals(keyFromText)){

                    Intent changePasswordIntent = new Intent(EnterCodeActivity.this, ChangePasswordActivity.class);
                    startActivity(changePasswordIntent);
                }
                else{

                    errorTextView.setText("Incorrect code, please check enter the correct code.");
                }
            }
        });





    }
}
