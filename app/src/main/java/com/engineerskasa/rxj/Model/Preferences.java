package com.engineerskasa.rxj.Model;

import androidx.annotation.NonNull;

public class Preferences {
    private String userPhone;
    private String userName;
    private String userBirthdate;

    private boolean saveUserCredential;
    private boolean retrieveUserCredential;



    public String getUserPhone() {
        return userPhone;
    }

    public Preferences setUserPhone(String userPhone) {
        this.userPhone = userPhone;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public Preferences setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserBirthdate() {
        return userBirthdate;
    }

    public Preferences setUserBirthdate(String userBirthdate) {
        this.userBirthdate = userBirthdate;
        return this;
    }

    public boolean isSaveUserCredential() {
        return saveUserCredential;
    }

    public Preferences setSaveUserCredential(boolean saveUserCredential) {
        this.saveUserCredential = saveUserCredential;
        return this;
    }

    public boolean isRetrieveUserCredential() {
        return retrieveUserCredential;
    }

    public Preferences setRetrieveUserCredential(boolean retrieveUserCredential) {
        this.retrieveUserCredential = retrieveUserCredential;
        return this;
    }

   /* @NonNull
    @Override
    public String toString() {
        return "Preferences{"+
                ""
                ;

       *//* StringBuilder sb = new StringBuilder();
        sb.append("First name : ").append(this.firstName).append("\n");
        sb.append("Last name : ").append(this.lastName).append("\n");
        sb.append("Email : ").append(this.email).append("\n");
        return sb.toString();*//*
    }*/
}
