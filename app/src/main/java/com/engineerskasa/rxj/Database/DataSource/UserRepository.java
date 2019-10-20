package com.engineerskasa.rxj.Database.DataSource;

import com.engineerskasa.rxj.Model.User;

import java.util.List;

import io.reactivex.Flowable;

public class UserRepository implements IUserDataSource{
    private IUserDataSource iUserDataSource;

    public UserRepository(IUserDataSource iUserDataSource) {
        this.iUserDataSource = iUserDataSource;
    }

    private static UserRepository instance;

    public static UserRepository getInstance(IUserDataSource iUserDataSource) {
        if(instance == null)
            instance = new UserRepository(iUserDataSource);
        return instance;
    }

    @Override
    public Flowable<List<User>> getAllUsers() {
        return null;
    }

    @Override
    public Flowable<List<User>> getUserWithPhone(String phone) {
        return iUserDataSource.getAllUsers();
    }

    @Override
    public int countTotalUsers() {
        return iUserDataSource.countTotalUsers();
    }

    @Override
    public void emptyUsers() {
        iUserDataSource.emptyUsers();
    }

    @Override
    public void addUser(User... users) {
        iUserDataSource.addUser(users);
    }

    @Override
    public void updateUser(User... users) {
        iUserDataSource.updateUser(users);
    }

    @Override
    public void deleteUser(User user) {
        iUserDataSource.deleteUser(user);
    }
}
