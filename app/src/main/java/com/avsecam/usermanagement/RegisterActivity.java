package com.avsecam.usermanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.UUID;

import io.realm.Realm;


@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {
    @ViewById(R.id.editTextNewUsername) EditText usernameField;
    @ViewById(R.id.editTextNewPassword) EditText passwordField;
    @ViewById(R.id.editTextConfirmPassword) EditText confirmPasswordField;

    private SharedPreferences sharedPreferences;
    private Realm realm;

    @AfterViews
    protected void init() {
        sharedPreferences = getSharedPreferences(getString(R.string.SHAREDPREFERENCES_NAME), MODE_PRIVATE);
        realm = Realm.getDefaultInstance();
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
                realm.executeTransactionAsync(realm -> {
                    User newUser = realm.createObject(User.class);
                    newUser.setUuid(UUID.randomUUID().toString());
                    newUser.setName(username);
                    newUser.setPassword(password);
                });
                int userCount = realm.where(User.class).findAll().size() + 1;
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
}