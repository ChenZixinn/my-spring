package com.tzuxin.service.impl;

import com.tzuxin.anno.Bean;
import com.tzuxin.service.UserService;

@Bean
public class UserServiceImpl implements UserService {
    @Override
    public void add() {
        System.out.println("service add...");
    }
}
