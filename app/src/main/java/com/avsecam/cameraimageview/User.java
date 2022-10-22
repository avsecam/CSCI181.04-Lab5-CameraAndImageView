package com.avsecam.cameraimageview;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class User extends RealmObject {
    private String uuid;
    @Required
    private String name;
    @Required
    private String password;
    private String imageFilename;

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public String getUuid() {
        return uuid;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }
    public String getImageFilename() {
        return imageFilename;
    }
}
