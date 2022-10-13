package com.avsecam.usermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;


public class UserAdapter extends RealmRecyclerViewAdapter<User, UserAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameLabel;
        private TextView passwordLabel;

        private ImageButton editButton;
        private ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameLabel = itemView.findViewById(R.id.labelUsername);
            passwordLabel = itemView.findViewById(R.id.labelPassword);

            editButton = itemView.findViewById(R.id.buttonEdit);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }

    AdminActivity adminActivity;

    public UserAdapter(AdminActivity adminActivity, @Nullable OrderedRealmCollection<User> data, boolean autoUpdate) {
        super(data, autoUpdate);

        this.adminActivity = adminActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = adminActivity.getLayoutInflater().inflate(R.layout.user_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = getItem(position);

        holder.usernameLabel.setText(user.getName());
        holder.passwordLabel.setText(user.getPassword());

        holder.editButton.setOnClickListener(event -> {
            Intent goToEdit = new Intent(adminActivity, EditActivity_.class);
            goToEdit.putExtra(adminActivity.getString(R.string.USERNAME_KEY), holder.usernameLabel.getText());
            goToEdit.putExtra(adminActivity.getString(R.string.PASSWORD_KEY), holder.passwordLabel.getText());
            adminActivity.startActivity(goToEdit);
        });

        holder.deleteButton.setOnClickListener(event -> {
            // Delete row and Realm object
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(r -> {
                User userToBeDeleted = r.where(User.class).equalTo(adminActivity.getString(R.string.USERNAME_KEY), holder.usernameLabel.getText().toString()).findFirst();
                userToBeDeleted.deleteFromRealm();
            });
        });
    }
}