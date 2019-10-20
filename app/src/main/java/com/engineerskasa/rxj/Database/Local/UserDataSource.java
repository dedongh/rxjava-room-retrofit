package com.engineerskasa.rxj.Database.Local;

import com.engineerskasa.rxj.Database.DataSource.IUserDataSource;
import com.engineerskasa.rxj.Model.User;

import java.util.List;

import io.reactivex.Flowable;

public class UserDataSource implements IUserDataSource {

    private UserDAO userDAO;
    private static UserDataSource instance;

    public UserDataSource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public static UserDataSource getInstance(UserDAO userDAO) {
        if (instance == null)
            instance = new UserDataSource(userDAO);
        return instance;
    }

    @Override
    public Flowable<List<User>> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Override
    public Flowable<List<User>> getUserWithPhone(String phone) {
        return userDAO.getUserWithPhone(phone);
    }

    @Override
    public int countTotalUsers() {
        return userDAO.countTotalUsers();
    }

    @Override
    public void emptyUsers() {
        userDAO.emptyUsers();
    }

    @Override
    public void addUser(User... users) {
        userDAO.addUser(users);
    }

    @Override
    public void updateUser(User... users) {
        userDAO.updateUser(users);
    }

    @Override
    public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }
}
