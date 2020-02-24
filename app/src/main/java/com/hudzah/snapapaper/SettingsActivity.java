package com.hudzah.snapapaper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    ListView listView;
    String titles[] = {"Edit Profile", "Report A Bug", "Suggestions", "Rate snapApaper", "Privacy Policy"};
    int images[] = {R.drawable.ic_account_circle_black_24dp, R.drawable.ic_bug_report_black_24dp, R.drawable.ic_event_available_black_24dp, R.drawable.ic_star_black_24dp, R.drawable.ic_security_black_24dp};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        listView = findViewById(R.id.listview);

        MyAdapter adapter = new MyAdapter(SettingsActivity.this, titles, images);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){

                    Intent profileIntent = new Intent(SettingsActivity.this, SettingsEditProfile.class);
                    startActivity(profileIntent);
                }
                if(position == 1){

                    Intent bugIntent = new Intent(SettingsActivity.this, SettingsBugs.class);
                    startActivity(bugIntent);
                }
                if(position == 2){

                    Intent suggestIntent = new Intent(SettingsActivity.this, SettingsSuggestions.class);
                    startActivity(suggestIntent);
                }
                if(position == 3){

                    Uri uri = Uri.parse("market://details?id=" + SettingsActivity.this.getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?" + SettingsActivity.this.getPackageName())));
                    }
                }
                if(position == 4){

                    Intent websiteIntent = new Intent("android.intent.action.VIEW",
                            Uri.parse("https://snapapaper.wixsite.com/snapapaper/privacy-policy"));
                    startActivity(websiteIntent);
                }
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
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends ArrayAdapter<String>{

        Context context;
        String rTitle[];
        int rImages[];

        MyAdapter(Context c, String title[], int image[]){
            super(c,R.layout.layout_listitem_settings, title);
            this.context = c;
            this.rTitle = title;
            this.rImages = image;


        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = layoutInflater.inflate(R.layout.layout_listitem_settings, parent, false);

            ImageView imageViewForRow = row.findViewById(R.id.imageViewForRow);

            TextView titleTextView = row.findViewById(R.id.titleText);

            imageViewForRow.setImageResource(rImages[position]);

            titleTextView.setText(rTitle[position]);



            return row;
        }
    }


}