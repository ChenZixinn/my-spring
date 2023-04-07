package com.tzuxin;

import com.tzuxin.bean.ApplicationContext;
import com.tzuxin.bean.impl.AnnotaionApplicationImpl2;
import com.tzuxin.bean.impl.AnnotationApplicationContextImpl;
import com.tzuxin.service.UserService;
import com.tzuxin.service.impl.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationApplicationContextImpl("com.tzuxin");
        UserService userService = (UserService)context.getBean(UserService.class);
        System.out.println(userService);
        userService.add();
    }
}