package com.hudzah.snapapaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class PricingActivity extends AppCompatActivity {

    Button dropdownButtonPlus;

    LinearLayout linearPlus;

    CardView cardViewPlus;

    Button dropdownButtonPremium;

    LinearLayout linearPremium;

    CardView cardViewPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricing);

        cardViewPlus = (CardView)findViewById(R.id.cardViewPlus);

        cardViewPremium = (CardView)findViewById(R.id.cardViewPremium);

        dropdownButtonPlus = (Button)findViewById(R.id.dropdownButtonPlus);

        dropdownButtonPremium = (Button)findViewById(R.id.dropdownButtonPremium);

        linearPlus = (LinearLayout)findViewById(R.id.linearPlus);

        linearPremium = (LinearLayout)findViewById(R.id.linearPremium);



        cardViewPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Purchase
                String packageSelected = "Plus";

                //parseSave(packageSelected);

            }
        });

        cardViewPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String packageSelected = "Premium";

                //parseSave(packageSelected);
            }
        });

        // toolbar
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
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
}
