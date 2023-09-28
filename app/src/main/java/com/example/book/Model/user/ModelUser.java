package com.example.book.Model.user;

import androidx.annotation.NonNull;

public class ModelUser implements CharSequence {
    String uid,email,name, password,profileimage,userType, major;
    long timestamp;

    public ModelUser() {
    }

    public ModelUser(String uid, String email, String name, String password, String profileimage, String userType, String major, long timestamp) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.password = password;
        this.profileimage = profileimage;
        this.userType = userType;
        this.major = major;
        this.timestamp = timestamp;
    }


    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }
}
