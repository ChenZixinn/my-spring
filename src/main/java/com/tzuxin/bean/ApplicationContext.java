package com.tzuxin.bean;

public interface ApplicationContext {
    /**
     * 根据类型返回对象
     * @param clazz 类型
     * @return 对象
     */
    Object getBean(Class<?> clazz);
}
