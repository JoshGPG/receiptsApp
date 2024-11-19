package com.example.testapp;

import android.os.Parcel;
import android.os.Parcelable;

public class CreateUser implements Parcelable {
    private String name;
    private String username;
    private String password;

    public CreateUser(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    protected CreateUser(Parcel in) {
        name = in.readString();
        username = in.readString();
        password = in.readString();
    }

    // Parcelable implementation
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(password);
    }

    // Getters
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
