package com.tzuxin.bean.impl;

import com.tzuxin.bean.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class AnnotationApplicationContextImpl implements ApplicationContext {
    // 创建一个Map集合，用于存放对象
    Map<Class, Object> beanFactory = new HashMap<>();

    /**
     * 根据类型返回map中的对象
     * @param clazz 类型
     * @return 类型对应的对象
     */
    @Override
    public Object getBean(Class clazz) {
        return beanFactory.get(clazz);
    }

    /**
     * 根据包路径，扫描包下面哪个类有@Bean注解，把这个类通过反射实例化
     * @param basePackage 包名
     */
    public AnnotationApplicationContextImpl(String basePackage) {

    }


}
