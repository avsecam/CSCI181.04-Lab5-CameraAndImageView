package com.avsecam.cameraimageview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import io.realm.Realm;


@EActivity(R.layout.activity_landing)
public class LandingActivity extends AppCompatActivity {

    @ViewById(R.id.labelWelcome) TextView welcomeLabel;
    @ViewById(R.id.imageUserLanding) ImageView userImage;

    File imageDir;
    SharedPreferences sharedPreferences;
    Realm realm;

    @AfterViews
    protected void init() {
        imageDir = getExternalCacheDir();
        sharedPreferences = getSharedPreferences(getString(R.string.SHAREDPREFERENCES_NAME), MODE_PRIVATE);
        realm = Realm.getDefaultInstance();

        String uuid = sharedPreferences.getString(getString(R.string.UUID_KEY), "");
        boolean rememberMe = sharedPreferences.getBoolean(getString(R.string.REMEMBERME_KEY), false);

        User user = realm.where(User.class).equalTo(getString(R.string.UUID_KEY), uuid).findFirst();
        String username = user.getName();
        String imageFilename = user.getImageFilename();
        File image = new File(imageDir, imageFilename + Helper.imageExtension);

        String welcomeText = "Welcome " + username + "#" + uuid + "!!!";
        if (rememberMe) {
            welcomeText += " You will be remembered.";
        }
        welcomeLabel.setText(welcomeText);

        Helper.refreshImageView(userImage, image);
    }
}