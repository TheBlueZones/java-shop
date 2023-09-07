package com.example.shop.server.impl;

import com.example.shop.expection.emmoeceException;
import com.example.shop.expection.expectionEnum;
import com.example.shop.model.dao.UserMapper;
import com.example.shop.model.pojo.User;
import com.example.shop.server.UserService;
import com.example.shop.untils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(16);
    }

    @Override
    public void register(String userName, String password) throws emmoeceException {
        /*check if the user has the same name*/
        System.out.println(userName);
        User result = userMapper.selectByName(userName);
        if (result != null) {
            throw new emmoeceException(expectionEnum.DUPLICATE_NAME);
        }
        /*write to database*/
        User user = new User();
        user.setUsername(userName);
        try {
            user.setPassword(MD5Utils.getMDstr(password));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        int count = userMapper.insertSelective(user);
        if (count == 0) {
            throw new emmoeceException(expectionEnum.INSERT_FAILED);
        }
    }

    @Override
    public User login(String userName, String password) throws emmoeceException {
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMDstr(password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);

        }
        User user = userMapper.selectByName(userName);
        if ( user== null){
            throw new emmoeceException(expectionEnum.USER_ERROR);
        }else if(!user.getPassword().equals(md5Password)){
            System.out.println(user.getPassword());
            throw new emmoeceException(expectionEnum.PASSWORD_WRONG);
        }

        return user;
    }

    @Override
    public void updateINfo(User user) throws emmoeceException {
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        /*只想查找一条记录*/
        if (updateCount > 1) {
            throw new emmoeceException(expectionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public boolean checkAdminRole(User user)  {
        return user.getRole().equals(2);
        /*1 general user 2 administractor*/
    }
}
