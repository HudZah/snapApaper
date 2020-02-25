package com.hudzah.snapapaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public class PackageSelectorActivity extends AppCompatActivity {

    Button dropdownButtonFree;

    LinearLayout linearFree;

    CardView cardViewFree;

    Button dropdownButtonPlus;

    LinearLayout linearPlus;

    CardView cardViewPlus;

    Button dropdownButtonPremium;

    LinearLayout linearPremium;

    CardView cardViewPremium;

    ParseQuery<ParseUser> userQuery;

    String username;

    ConnectionDetector connectionDetector;

    RelativeLayout layout;

    LoadingDialog loadingDialog;

    public void premiumOnClick(View view){

        // Purchase
        String packageSelected = "Premium";


    }

    public void plusOnClick(View view){

        String packageSelected = "Plus";

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_selector);

        dropdownButtonFree = (Button)findViewById(R.id.dropdownButtonFree);
        linearFree = (LinearLayout)findViewById(R.id.linearFree);
        cardViewFree = findViewById(R.id.cardViewFree);

        dropdownButtonPlus = (Button)findViewById(R.id.dropdownButtonPlus);
        linearPlus = (LinearLayout)findViewById(R.id.linearPlus);
        cardViewPlus = findViewById(R.id.cardViewPlus);

        dropdownButtonPremium = (Button)findViewById(R.id.dropdownButtonPremium);
        linearPremium = (LinearLayout)findViewById(R.id.linearPremium);
        cardViewPremium = findViewById(R.id.cardViewPremium);

        Intent intent = getIntent();

        loadingDialog = new LoadingDialog(this);

        username = intent.getStringExtra("username");

        connectionDetector = new ConnectionDetector(getApplicationContext());

        layout = (RelativeLayout)findViewById(R.id.backgroundLinear);

        userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", username);
        //userQuery.findInBackground();

        Button continueButton = (Button) findViewById(R.id.continueButton);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseUser.logOut();

                changeIntent();
            }
        });


        Log.i("Usermame is", username);

        cardViewFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String packageSelected = "Free";

            }
        });


    }

    private void changeIntent() {

        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        Toast.makeText(getApplicationContext(), "Account has been created, login to continue", Toast.LENGTH_LONG).show();
        startActivity(loginIntent);
    }


    public void freeDropdown(View view){

        if(linearFree.getVisibility() == View.GONE){

            TransitionManager.beginDelayedTransition(cardViewFree, new AutoTransition());
            linearFree.setVisibility(View.VISIBLE);
            dropdownButtonFree.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        }
        else{

            TransitionManager.beginDelayedTransition(cardViewFree, new AutoTransition());
            linearFree.setVisibility(View.GONE);
            dropdownButtonFree.setBackgroundResource(R.drawable.arrow_bitmap);
        }
    }

    public void plusDropdown(View view){

        if(linearPlus.getVisibility() == View.GONE){

            TransitionManager.beginDelayedTransition(cardViewPlus, new AutoTransition());
            linearPlus.setVisibility(View.VISIBLE);
            dropdownButtonPlus.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        }
        else{

            TransitionManager.beginDelayedTransition(cardViewPlus);
            linearPlus.setVisibility(View.GONE);
            dropdownButtonPlus.setBackgroundResource(R.drawable.arrow_bitmap);
        }

    }

    public void premiumDropdown(View view){


        if(linearPremium.getVisibility() == View.GONE){

            TransitionManager.beginDelayedTransition(cardViewPremium, new AutoTransition());
            linearPremium.setVisibility(View.VISIBLE);
            dropdownButtonPremium.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        }
        else{

            TransitionManager.beginDelayedTransition(cardViewPremium, new AutoTransition());
            linearPremium.setVisibility(View.GONE);
            dropdownButtonPremium.setBackgroundResource(R.drawable.arrow_bitmap);
        }
    }

    public void openTerms(View view){

        Intent websiteIntent = new Intent("android.intent.action.VIEW",
                Uri.parse("https://snapapaper.wixsite.com/snapapaper/privacy-policy"));
        startActivity(websiteIntent);
    }
}
