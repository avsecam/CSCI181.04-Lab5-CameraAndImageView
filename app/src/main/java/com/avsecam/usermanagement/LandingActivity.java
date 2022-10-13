package com.avsecam.usermanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;


@EActivity(R.layout.activity_landing)
public class LandingActivity extends AppCompatActivity {

    @ViewById(R.id.labelWelcome) TextView welcomeLabel;

    SharedPreferences sharedPreferences;
    Realm realm;

    @AfterViews
    protected void init() {
        sharedPreferences = getSharedPreferences(getString(R.string.SHAREDPREFERENCES_NAME), MODE_PRIVATE);
        realm = Realm.getDefaultInstance();

        String uuid = sharedPreferences.getString(getString(R.string.UUID_KEY), "");
        boolean rememberMe = sharedPreferences.getBoolean(getString(R.string.REMEMBERME_KEY), false);
        String username = realm.where(User.class).equalTo(getString(R.string.UUID_KEY), uuid).findFirst().getName();

        String welcomeText = "Welcome " + username + "#" + uuid + "!!!";
        if (rememberMe) {
            welcomeText += " You will be remembered.";
        }
        welcomeLabel.setText(welcomeText);
    }
}