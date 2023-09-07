package com.example.shop.server;


import com.example.shop.expection.emmoeceException;
import com.example.shop.model.pojo.User;

public interface UserService {
    User getUser();

    void register(String userName, String password) throws emmoeceException;

    User login(String userName, String password) throws emmoeceException;

    void updateINfo(User user) throws emmoeceException;

    boolean checkAdminRole(User user);
}
