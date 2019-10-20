package com.engineerskasa.rxj.Database.DataSource;

import com.engineerskasa.rxj.Model.User;

import java.util.List;

import io.reactivex.Flowable;

public interface IUserDataSource {
    Flowable<List<User>> getAllUsers();
    Flowable<List<User>> getUserWithPhone(String phone);
    int countTotalUsers();
    void emptyUsers();
    void addUser(User... users);
    void updateUser(User... users);
    void deleteUser(User user);
}
