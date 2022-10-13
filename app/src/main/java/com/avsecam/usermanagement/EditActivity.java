package com.avsecam.usermanagement;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.UUID;

import io.realm.Realm;

@EActivity(R.layout.activity_edit)
public class EditActivity extends AppCompatActivity {
    @ViewById(R.id.editTextEditUsername)
    EditText usernameField;
    @ViewById(R.id.editTextEditPassword)
    EditText passwordField;
    @ViewById(R.id.editTextConfirmNewPassword)
    EditText confirmPasswordField;

    private Realm realm;
    private User userToBeEdited;

    String username;
    String password;
    String confirmPassword;

    @AfterViews
    protected void init() {
        realm = Realm.getDefaultInstance();

        usernameField.setText(getIntent().getStringExtra(getString(R.string.USERNAME_KEY)));
        passwordField.setText(getIntent().getStringExtra(getString(R.string.PASSWORD_KEY)));
        confirmPasswordField.setText(passwordField.getText().toString());

        userToBeEdited = realm.where(User.class).equalTo("name", usernameField.getText().toString()).findFirst();
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
}
