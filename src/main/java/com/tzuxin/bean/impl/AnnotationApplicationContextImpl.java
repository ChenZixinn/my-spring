package com.tzuxin.bean.impl;

import com.tzuxin.anno.Bean;
import com.tzuxin.bean.ApplicationContext;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class AnnotationApplicationContextImpl implements ApplicationContext {
    // 创建一个Map集合，用于存放对象
    Map<Class<?>, Object> beanFactory = new HashMap<>();

    public static String rootPath;

    /**
     * 根据类型返回map中的对象
     * @param clazz 类型
     * @return 类型对应的对象
     */
    @Override
    public Object getBean(Class<?> clazz) {
        return beanFactory.get(clazz);
    }

    /**
     * 根据包路径，扫描包下面哪个类有@Bean注解，把这个类通过反射实例化
     * @param basePackage 包名
     */
    public AnnotationApplicationContextImpl(String basePackage) {
        // 1、把.替换成/
        String packagePath = basePackage.replaceAll("\\.", "\\/");
        // 2、获取路径
        String filePath = Thread.currentThread().getContextClassLoader().getResource(packagePath).getPath();
        try {
            // 转为utf-8
            filePath = URLDecoder.decode(filePath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        // 取出根路径
        rootPath = filePath.substring(0, filePath.length() - packagePath.length());
        // 调用loadBean 加载Bean
        try {
            loadBean(new File(filePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 完成对象的扫描
     * @param file 包的File对象
     */
    private void loadBean(File file) throws Exception {
        // 1、判断是否是一个文件夹
        if (file.isDirectory()){
            // 2、获取文件夹里所有文件
            File[] childrenFiles = file.listFiles();
            // 3、判断文件夹是否为空
            if (childrenFiles == null || childrenFiles.length == 0){
                return;
            }
            // 4、如果文件夹不为空，遍历文件夹内容
            for (File childrenFile : childrenFiles) {
                // 4.1 文件夹：进行递归，找文件
                if (childrenFile.isDirectory()){
                    loadBean(childrenFile);
                }else{
                    // 4.2 文件：通过反射加载到map中
                    // 4.3 得到包路径+类名称部分
                    String pathWithClass = childrenFile.getAbsolutePath().substring(rootPath.length());
//                    System.out.println("类路径：" + pathWithClass);
                    // 4.4 判断当前文件类型是否为.class
                    if (pathWithClass.endsWith(".class")){
                        // 4.5 如果是.class类型，把路径的/替换成. ，并把.class去掉
                        // com.tzuxin.service.UserServiceImpl
                        String fullName = pathWithClass.replaceAll("\\/", ".").replaceAll(".class", "");
                        // 4.6 判断类上面是否有@Bean注解，没有的话不进行实例化
                        // 4.6.1 获取类的class
                        Class<?> clazz = Class.forName(fullName);
                        // 4.6.2 如果不是一个接口
                        if (!clazz.isInterface()){
                            // 4.6.3 判断是否有@Bean注解
                            Bean annotation = clazz.getAnnotation(Bean.class);
                            if (annotation != null){
                                // 4.6.4 实例化
                                Object instance = clazz.newInstance();
                                // 4.7 把对象放到map中
                                // 4.7.1 如果对象有接口，用接口作为key
                                if (clazz.getInterfaces().length > 0){
                                    // 有借口，用接口作为key
                                    beanFactory.put(clazz.getInterfaces()[0], instance);
                                }else {
                                    beanFactory.put(clazz, instance);
                                }
                            }

                        }
                    }
                }
        }

        }
    }

    public static void main(String[] args) {
    }
}
