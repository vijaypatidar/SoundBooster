package com.vkpapps.soundbooster.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class UserViewModel extends ViewModel {

    private MutableLiveData<List<User>> users;

    public UserViewModel() {
        users = new MutableLiveData<>();
    }

    public MutableLiveData<List<User>> getUsers() {
        return users;
    }

    public void addUser(User user){
        if (users==null)users =new MutableLiveData<>();
        users.getValue().add(user);
    }
    public void removeUser(User user){
        if (users==null)users =new MutableLiveData<>();
        users.getValue().remove(user);
    }
}