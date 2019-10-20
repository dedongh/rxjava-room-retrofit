package com.engineerskasa.rxj.Database.Local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.engineerskasa.rxj.Model.User;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserDAO {
    // select all users
    @Query("select * from users")
    Flowable<List<User>> getAllUsers();

    // select all users based on param
    @Query("select * from users where phone = :phone")
    Flowable<List<User>> getUserWithPhone(String phone);

    // total users
    @Query("select count(*) from users")
            int countTotalUsers();

    // delete all users
    @Query("delete from  users")
    void emptyUsers();

    // add new user
    @Insert
    void addUser(User... users);

    // update user
    @Update
    void updateUser(User... users);

    // delete user
    @Delete
    void deleteUser(User user);
}
