package com.avsecam.usermanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_admin)
public class AdminActivity extends AppCompatActivity {

    @ViewById(R.id.recyclerViewUsers) RecyclerView usersList;

    private Realm realm;

    @AfterViews
    protected void init() {
        realm = Realm.getDefaultInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        usersList.setLayoutManager(linearLayoutManager);

        RealmResults<User> allUsers = realm.where(User.class).findAll();
        UserAdapter adapter = new UserAdapter(this, allUsers, true);
        usersList.setAdapter(adapter);
    }

    @Click(R.id.buttonAdd)
    public void onAddButtonPressed() {
        Intent goToRegister = new Intent(this, RegisterActivity_.class);
        startActivity(goToRegister);
    }

    @Click(R.id.buttonClearUsers)
    public void onClearButtonPressed() {
        realm.executeTransactionAsync(r ->
            r.where(User.class)
             .findAll()
             .deleteAllFromRealm()
        );
        Toast.makeText(this, "All users deleted.", Toast.LENGTH_SHORT).show();
    }
}