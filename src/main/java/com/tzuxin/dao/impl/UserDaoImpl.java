package com.tzuxin.dao.impl;

import com.tzuxin.anno.Bean;
import com.tzuxin.dao.UserDao;

@Bean
public class UserDaoImpl implements UserDao {
    @Override
    public void add() {
        System.out.println("dao add....");
    }
}
