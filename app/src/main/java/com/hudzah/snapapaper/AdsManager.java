package com.hudzah.snapapaper;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class AdsManager {

    Context context;
    InterstitialAd mInterstitialAd;
    private static final String TAG = "AdsManager";
    public AdsManager(Context context) {
        this.context = context;
    }

    public void initInterstitialAd(){

        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        Log.d(TAG, "initInterstitialAd: Initialization");

        mInterstitialAd = new InterstitialAd(context);
        String interId = context.getResources().getString(R.string.test_unit_ad);
        mInterstitialAd.setAdUnitId(interId);
        loadInterstitialAd();

    }

    public void initBannerAd(){
        MobileAds.initialize(context, context.getResources().getString(R.string.banner_test_unit_ad));
    }

    public void loadBannerAd(AdView adView){
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void loadInterstitialAd(){
        if(!mInterstitialAd.isLoaded()){
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            Log.d(TAG, "loadInterstitialAd: Ad is being loaded");
        }

    }

    public boolean checkIfInterstitialIsLoaded(){
        return mInterstitialAd.isLoaded();
    }

    public void showInterstitialAd(){
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
            Log.d(TAG, "showInterstitialAd: Ad is being shown");
            mInterstitialAd.setAdListener(new AdListener()
            {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    loadInterstitialAd();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            });
        }
        else{
            loadInterstitialAd();
            Log.d(TAG, "showInterstitialAd: Ad not loaded, reloading...");
        }
    }
}
