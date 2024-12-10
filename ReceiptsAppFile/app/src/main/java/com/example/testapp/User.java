//package com.example.testapp;
//
//public class User {
//    private String userId;
//    private String name;
//    private String username;
//    private String password;
//
//    public User(String name, String username, String password){
//        this.name = name;
//        this.username = username;
//        this.password = password;
//    }
//
//    // Getters and Setters
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//}

package com.example.testapp;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private int userId;
    private String name;
    private String username;
    private String password;

    public User(int userId, String name, String username, String password) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    protected User(Parcel in) {
        userId = in.readInt();
        name = in.readString();
        username = in.readString();
        password = in.readString();
    }

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
        dest.writeInt(userId);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(password);
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
