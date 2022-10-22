package com.avsecam.cameraimageview;

import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import io.realm.Realm;

@EActivity(R.layout.activity_edit)
public class EditActivity extends AppCompatActivity {
    @ViewById(R.id.editTextEditUsername)
    EditText usernameField;
    @ViewById(R.id.editTextEditPassword)
    EditText passwordField;
    @ViewById(R.id.editTextConfirmNewPassword)
    EditText confirmPasswordField;
    @ViewById(R.id.editImage)
    ImageView userImage;

    private File imageDir;
    private Realm realm;
    private User userToBeEdited;

    private boolean imageHasBeenTaken = false;
    private byte[] jpeg;

    String username;
    String password;
    String confirmPassword;

    @AfterViews
    protected void init() {
        imageDir = getExternalCacheDir();
        realm = Realm.getDefaultInstance();

        usernameField.setText(getIntent().getStringExtra(getString(R.string.USERNAME_KEY)));
        passwordField.setText(getIntent().getStringExtra(getString(R.string.PASSWORD_KEY)));
        confirmPasswordField.setText(passwordField.getText().toString());

        userToBeEdited = realm.where(User.class).equalTo("name", usernameField.getText().toString()).findFirst();
        File userImageFile = new File(imageDir, userToBeEdited.getImageFilename() + Helper.imageExtension);
        Helper.refreshImageView(userImage, userImageFile);
    }

    @Click(R.id.buttonSaveEdit)
    public void onSaveButtonPressed() {
        username = usernameField.getText().toString();
        password = passwordField.getText().toString();
        confirmPassword = confirmPasswordField.getText().toString();

        // Check if username is already taken
        boolean userExists = realm.where(User.class).equalTo("name", username).findFirst() != null;
        if (userExists) {
            // Check if the existing user is the user being edited
            boolean existingUserIsTheSame = realm.where(User.class).equalTo("name", username).findFirst().getName().equals(userToBeEdited.getName());
            if (!existingUserIsTheSame) {
                Toast.makeText(this, "User already exists.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Check if all fields have values
        if (username.length() > 0 && password.length() > 0) {
            // Check if both passwords typed are equal
            if (password.equals(confirmPassword)) {
                realm.beginTransaction();
                    userToBeEdited.setName(username);
                    userToBeEdited.setPassword(password);
                realm.commitTransaction();
                if (imageHasBeenTaken) {
                    // Overwrite existing image
                    try {
                        Helper.saveFile(imageDir, userToBeEdited.getImageFilename(), jpeg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(this, "User " + userToBeEdited.getName() + " saved.", Toast.LENGTH_LONG).show();
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

    @Click(R.id.buttonCancelEdit)
    public void onCancelButtonPressed() {
        finish();
    }


    @Click(R.id.editImage)
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
