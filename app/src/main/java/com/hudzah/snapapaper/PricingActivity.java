package com.hudzah.snapapaper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class PricingActivity extends AppCompatActivity implements PurchasesUpdatedListener {


    RecyclerView recyclerView;

    private static final String TAG = "PricingActivity";

    private static BillingClient billingClient;

    private List<String> skuList = new ArrayList<>();

    CardView cardView;

    Button dropDownButton;

    LinearLayout linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricing);

        setupBillingClient();

        skuList.add("remove_ads");

        // toolbar
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        initValues();

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

    public void setupBillingClient(){

        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this)
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    Log.d(TAG, "onBillingSetupFinished: Query");
                    loadProducts();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.d(TAG, "onBillingServiceDisconnected: Disconnected");
            }
        });
    }

    public void initValues(){

        cardView = findViewById(R.id.cardView);
        dropDownButton = (Button) findViewById(R.id.dropdownButton);
        linear = (LinearLayout) findViewById(R.id.linear);
    }

    public void loadProducts(){

        if(billingClient.isReady()){
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();

            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

            billingClient.querySkuDetailsAsync(params.build(),
                    new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                                Log.d(TAG, "onSkuDetailsResponse: ResponseCode is " + billingResult.getResponseCode());
                                cardView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onPurchaseListener(skuDetailsList);
                                    }
                                });
                            }
                            else Log.d(TAG, "onSkuDetailsResponse: ResponseCode is " + billingResult.getResponseCode());
                        }
                });
        }
    }

    public void setDropDownButton(View view){
        if(linear.getVisibility() == View.GONE){

            TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
            linear.setVisibility(View.VISIBLE);
            dropDownButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        }
                    else{

            TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
            linear.setVisibility(View.GONE);
            dropDownButton.setBackgroundResource(R.drawable.arrow_bitmap);
        }
    }


    public void onPurchaseListener(List<SkuDetails> skuDetailsList){
        launchBillingFlow(skuDetailsList.get(0));
    }

    public void launchBillingFlow(SkuDetails skuDetailsList){

        BillingFlowParams params = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetailsList)
                .build();
        billingClient.launchBillingFlow(PricingActivity.this, params);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        Log.d(TAG, "onPurchasesUpdated: " + billingResult.getResponseCode());
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
            for(Purchase purchase : purchases){
                acknowledgePurchase(purchase.getPurchaseToken());
                savePurchaseToServer();
            }
        }
        else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
            Toast.makeText(PricingActivity.this, "Sorry, you already own this item", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onPurchasesUpdated: Already Owned");
        }
    }

    public void acknowledgePurchase(String purchaseToken){

        AcknowledgePurchaseParams ackParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build();
        billingClient.acknowledgePurchase(ackParams, new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    Toast.makeText(PricingActivity.this, billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onAcknowledgePurchaseResponse: Payment Acknowledged");
                }

            }
        });
    }

    public void savePurchaseToServer(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    for(ParseUser object : objects){
                        object.put("adsRemoved", true);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    Log.d(TAG, "done: Saved Successfully On Parse");
                                }
                            }
                        });
                    }
                }
            }
        });

    }


}
