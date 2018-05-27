package com.example.nico.firebaseproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.content.Intent;
import android.preference.ListPreference;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Locale;

public class Languages extends Activity implements View.OnClickListener {

    private ImageView swiss, british, turkish;
    private Locale myLocale;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);
        Intent intent = getIntent();

        //LINK TO IMAGEVIEW
        swiss = findViewById(R.id.swiss);
        swiss.setOnClickListener(this);

        british = findViewById(R.id.british);
        british.setOnClickListener(this);

        turkish = findViewById(R.id.turkish);
        turkish.setOnClickListener(this);

        loadLocale();
    }

    //Loading a saved locale
    public void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }

    //Save the current locale
    private void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    //Changing the language in the application
    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    //HERE, DEPEND ON WITCH FLAG YOU CLICK ON, IT TAKES THE RIGHT CASE
    public void onClick(View v) {

        String lang ="en";
        switch (v.getId()) {
            case R.id.british:
                lang="en";
                break;

            case R.id.swiss:
                lang="fr";
                break;

            case R.id.turkish:
                lang="tr";
                break;

            default:
                break;
        }
        changeLang(lang);

        //WHEN THE USER CHANGE THE LANGUAGES, HE MUST SIGN IN
        Intent intent = new Intent(Languages.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (myLocale != null){
            newConfig.locale = myLocale;
            Locale.setDefault(myLocale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }
}