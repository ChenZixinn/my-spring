package com.tzuxin.service.impl;

import com.tzuxin.anno.Bean;
import com.tzuxin.anno.Di;
import com.tzuxin.dao.UserDao;
import com.tzuxin.service.UserService;

@Bean
public class UserServiceImpl implements UserService {

    @Di
    UserDao userDao;

    @Override
    public void add() {
        System.out.println("service add...");
        userDao.add();
    }
}
