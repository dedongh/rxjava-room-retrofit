package com.engineerskasa.rxj.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.engineerskasa.rxj.Model.Preferences;

public class Management {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public Management(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("rxJ", Context.MODE_PRIVATE);
    }

    /**
     * <p>It is used to get Preference Data</p>
     *
     * @param prefObject
     * @return
     */
    public Preferences getPreferences(Preferences prefObject) {
        Preferences pref = new Preferences();

        if (prefObject.isRetrieveUserCredential()) {
            pref.setUserName(sharedPreferences.getString(Constants.SharedPref.USER_NAME, null));
            pref.setUserPhone(sharedPreferences.getString(Constants.SharedPref.USER_PHONE, null));
            pref.setUserBirthdate(sharedPreferences.getString(Constants.SharedPref.USER_BDAY, null));
        }

        return pref;
    }

    /**
     * <p>It is used to save Preferences data</p>
     *
     * @param prefObject
     */
    public void savePreferences(Preferences prefObject) {
        editor = sharedPreferences.edit();

        if (prefObject.isSaveUserCredential()) {
            editor.putString(Constants.SharedPref.USER_NAME, prefObject.getUserName());
            editor.putString(Constants.SharedPref.USER_PHONE, prefObject.getUserPhone());
            editor.putString(Constants.SharedPref.USER_BDAY, prefObject.getUserBirthdate());
        }

        editor.commit();
    }
}
