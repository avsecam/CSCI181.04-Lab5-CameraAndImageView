package com.avsecam.usermanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;


@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    @ViewById(R.id.editTextUsername) EditText usernameField;
    @ViewById(R.id.editTextPassword) EditText passwordField;
    @ViewById(R.id.checkBoxRememberMe) CheckBox rememberMeCheckBox;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Realm realm;

    @AfterViews
    protected void init() {
        sharedPreferences = getSharedPreferences(getString(R.string.SHAREDPREFERENCES_NAME), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        realm = Realm.getDefaultInstance();

        // Remember the remember me checkbox state
        if (sharedPreferences.contains(getString(R.string.REMEMBERME_KEY))) {
            rememberMeCheckBox.setChecked(sharedPreferences.getBoolean(getString(R.string.REMEMBERME_KEY), false));
        }
    }

    @Click(R.id.buttonSignIn)
    public void onLoginButtonPressed() {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        // Check if there are already saved credentials
        if (checkForCredentials(username)) {
            String savedPassword = realm.where(User.class).equalTo(getString(R.string.USERNAME_KEY), username).findFirst().getPassword();
            // Compare password
            if (password.equals(savedPassword)) {
                // Save uuid to sharedPreferences
                String savedUuid = realm.where(User.class).equalTo(getString(R.string.USERNAME_KEY), username).findFirst().getUuid();
                editor.putString(getString(R.string.UUID_KEY), savedUuid);
                editor.apply();
                // Go to landing page
                Intent intent = new Intent(this, LandingActivity_.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No User found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.buttonAdmin)
    public void onAdminButtonPressed() {
        Intent goToAdmin = new Intent(this, AdminActivity_.class);
        startActivity(goToAdmin);
    }

    @Click(R.id.buttonClear)
    public void onClearButtonPressed() {
        if (sharedPreferences.contains(getString(R.string.UUID_KEY)) || sharedPreferences.contains(getString(R.string.REMEMBERME_KEY))) {
            editor.clear();
            editor.apply();
            Toast.makeText(this, "SharedPreferences cleared.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nothing to clear.", Toast.LENGTH_SHORT).show();
        }
    }

    // Saves the checkbox state on every press
    @Click(R.id.checkBoxRememberMe)
    public void onRememberMeCheckboxToggled() {
        editor.putBoolean(getString(R.string.REMEMBERME_KEY), rememberMeCheckBox.isChecked());
        editor.apply();
    }

    private boolean checkForCredentials(String username) {
        return realm.where(User.class).equalTo("name", username).findAll().size() == 1;
    }
}