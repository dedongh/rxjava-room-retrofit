package com.engineerskasa.rxj.Database.Local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.engineerskasa.rxj.Model.User;

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
    private static UserDatabase instance;

    public static UserDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, UserDatabase.class, "db-users")
                    //.allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
