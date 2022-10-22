package com.avsecam.cameraimageview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import io.realm.Realm;


@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {
    @ViewById(R.id.editTextNewUsername) EditText usernameField;
    @ViewById(R.id.editTextNewPassword) EditText passwordField;
    @ViewById(R.id.editTextConfirmPassword) EditText confirmPasswordField;
    @ViewById(R.id.editImageNew) ImageView userImage;

    private SharedPreferences sharedPreferences;
    private Realm realm;
    private File imageDir;
    private boolean imageHasBeenTaken = false;
    private byte[] jpeg;

    @AfterViews
    protected void init() {
        Helper.refreshImageView(userImage);

        sharedPreferences = getSharedPreferences(getString(R.string.SHAREDPREFERENCES_NAME), MODE_PRIVATE);
        realm = Realm.getDefaultInstance();
        imageDir = getExternalCacheDir();
    }

    @Click(R.id.buttonSave)
    public void onSaveButtonPressed() {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();
        
        // Check if username is already taken
        boolean userExists = realm.where(User.class).equalTo("name", username).findFirst() != null;
        if (userExists) {
            Toast.makeText(this, "User already exists.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Check if all fields have values
        if (username.length() > 0 && password.length() > 0 && confirmPassword.length() > 0) {
            // Check if both passwords typed are equal
            if (password.equals(confirmPassword)) {
                realm.beginTransaction();
                    User newUser = realm.createObject(User.class);
                    String uuid = UUID.randomUUID().toString();
                    String uuidCompact = uuid.replace("-", "");
                    newUser.setUuid(uuid);
                    newUser.setName(username);
                    newUser.setPassword(password);
                    newUser.setImageFilename(uuidCompact);
                realm.commitTransaction();

                // Only save an image if an image has been taken
                if (imageHasBeenTaken) {
                    try {
                        Helper.saveFile(imageDir, uuidCompact, jpeg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                int userCount = realm.where(User.class).findAll().size();
                Toast.makeText(this, "New User saved. Total: " + userCount, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            }
        } else {
            String toastText = "";
            if (username.length() <= 0) toastText += "Username must not be blank!\n";
            if (password.length() <= 0) toastText += "Password must not be blank!\n";
            toastText = toastText.substring(0, toastText.length() - 1);
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.buttonCancel)
    public void onCancelButtonPressed() {
        finish();
    }

    @Click(R.id.editImageNew)
    public void onImagePressed() {
        ImageActivity_.intent(this).startForResult(Helper.REQUEST_CODE_IMAGE_SCREEN);
    }

    // SINCE WE USE startForResult(), code will trigger this once the next screen calls finish()
    public void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode == Helper.REQUEST_CODE_IMAGE_SCREEN)
        {
            if (responseCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN)
            {
                imageHasBeenTaken = true;

                // receive the raw JPEG data from ImageActivity
                // this can be saved to a file or save elsewhere like Realm or online
                jpeg = data.getByteArrayExtra("rawJpeg");

                try {
                    // Save temporarily. Save permanently when saving user
                    File savedImage = Helper.saveFile(imageDir, Helper.tempImageFilename, jpeg);
                    Helper.refreshImageView(userImage, savedImage.getAbsoluteFile());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}