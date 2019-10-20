package com.engineerskasa.rxj.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.engineerskasa.rxj.Database.DataSource.UserRepository;
import com.engineerskasa.rxj.Database.Local.UserDatabase;
import com.engineerskasa.rxj.Model.User;
import com.engineerskasa.rxj.Retrofit.IRxJAPI;
import com.engineerskasa.rxj.Retrofit.InstantSearchAPIService;
import com.engineerskasa.rxj.Retrofit.RetrofitClient;

public class Common {
    private static final String BASE_URL = "http://api.engineerskasa.com/dwom/";
    //private static final String BASE_URL = "https://api.androidhive.info/json/";
    public static User currentUser = null;

    public static IRxJAPI getAPI() {
        return RetrofitClient.getClient(BASE_URL).create(IRxJAPI.class);
    }

    // Database
    public static UserDatabase userDatabase;
    public static UserRepository userRepository;

    // check network
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    // contact list activity
    public static String CONTACT_BASE_URL = "https://api.androidhive.info/json/";

    public static InstantSearchAPIService getSearchAPI() {
        return RetrofitClient.getContactClient(CONTACT_BASE_URL).create(InstantSearchAPIService.class);
    }
}
