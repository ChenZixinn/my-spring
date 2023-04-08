## 1、原理-手写IoC

我们都知道，Spring框架的IOC是基于Java反射机制实现的，下面我们先回顾一下java反射。

### 1.1、回顾Java反射

`Java`反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能够调用它的任意方法和属性；这种动态获取信息以及动态调用对象方法的功能称为`Java`语言的反射机制。简单来说，反射机制指的是程序在运行时能够获取自身的信息。

要想解剖一个类，必须先要**获取到该类的Class对象**。而剖析一个类或用反射解决具体的问题就是使用相关API

**（1）java.lang.Class**

**（2）java.lang.reflect**，所以，**Class对象是反射的根源**。

**自定义类**

```java
@Data
public class Car {

    //属性
    private String name;
    private int age;
    private String color;

    //无参数构造
    public Car() {
    }
}
```

**编写测试类**

```java
public class TestCar {

    //1、获取Class对象多种方式
    @Test
    public void test01() throws Exception {
        //1 类名.class
        Class clazz1 = Car.class;

        //2 对象.getClass()
        Class clazz2 = new Car().getClass();

        //3 Class.forName("全路径")
        Class clazz3 = Class.forName("com.atguigu.reflect.Car");

        //实例化
        Car car = (Car)clazz3.getConstructor().newInstance();
        System.out.println(car);
    }

    //2、获取构造方法
    @Test
    public void test02() throws Exception {
        Class clazz = Car.class;
        //获取所有构造
        // getConstructors()获取所有public的构造方法
//        Constructor[] constructors = clazz.getConstructors();
        // getDeclaredConstructors()获取所有的构造方法public  private
        Constructor[] constructors = clazz.getDeclaredConstructors();
        for (Constructor c:constructors) {
            System.out.println("方法名称："+c.getName()+" 参数个数："+c.getParameterCount());
        }

        //指定有参数构造创建对象
        //1 构造public
//        Constructor c1 = clazz.getConstructor(String.class, int.class, String.class);
//        Car car1 = (Car)c1.newInstance("夏利", 10, "红色");
//        System.out.println(car1);
        
        //2 构造private
        Constructor c2 = clazz.getDeclaredConstructor(String.class, int.class, String.class);
        c2.setAccessible(true);
        Car car2 = (Car)c2.newInstance("捷达", 15, "白色");
        System.out.println(car2);
    }

    //3、获取属性
    @Test
    public void test03() throws Exception {
        Class clazz = Car.class;
        Car car = (Car)clazz.getDeclaredConstructor().newInstance();
        //获取所有public属性
        //Field[] fields = clazz.getFields();
        //获取所有属性（包含私有属性）
        Field[] fields = clazz.getDeclaredFields();
        for (Field field:fields) {
            if(field.getName().equals("name")) {
                //设置允许访问
                field.setAccessible(true);
                field.set(car,"五菱宏光");
                System.out.println(car);
            }
            System.out.println(field.getName());
        }
    }

    //4、获取方法
    @Test
    public void test04() throws Exception {
        Car car = new Car("奔驰",10,"黑色");
        Class clazz = car.getClass();
        //1 public方法
        Method[] methods = clazz.getMethods();
        for (Method m1:methods) {
            //System.out.println(m1.getName());
            //执行方法 toString
            if(m1.getName().equals("toString")) {
                String invoke = (String)m1.invoke(car);
                //System.out.println("toString执行了："+invoke);
            }
        }

        //2 private方法
        Method[] methodsAll = clazz.getDeclaredMethods();
        for (Method m:methodsAll) {
            //执行方法 run
            if(m.getName().equals("run")) {
                m.setAccessible(true);
                m.invoke(car);
            }
        }
    }
}
```



### 1.2、实现Spring的IoC

我们知道，IoC（控制反转）和DI（依赖注入）是Spring里面核心的东西，那么，我们如何自己手写出这样的代码呢？下面我们就一步一步写出Spring框架最核心的部分。

**①搭建子模块**

搭建模块：my-spring，搭建方式如其他spring子模块

**②准备测试需要的bean**

添加依赖

```xml
<dependencies>
    <!--junit5测试-->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.3.1</version>
    </dependency>
</dependencies>
```

创建[UserDao](src/main/java/com/tzuxin/dao/UserDao.java)接口

```java
public interface UserDao {
    public void add();
}

```

创建[UserDaoImpl](src/main/java/com/tzuxin/dao/impl/UserDaoImpl.java)实现

```java
@Bean
public class UserDaoImpl implements UserDao {
    @Override
    public void add() {
        System.out.println("dao add....");
    }
}
```

创建[UserService](src/main/java/com/tzuxin/service/UserService.java)接口

```java
package com.tzuxin.service;

public interface UserService {
    public void add();
}
```

创建[UserServiceImpl](src/main/java/com/tzuxin/service/impl/UserServiceImpl.java)实现类

```java
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
```

**③定义注解**

我们通过注解的形式加载bean与实现依赖注入

[Bean](src/main/java/com/tzuxin/anno/Bean.java )注解

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
}
```

[Di](src/main/java/com/tzuxin/anno/Di.java)依赖注入注解

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Di {
}
```

说明：上面两个注解可以随意取名

**④定义bean容器接口**

```java
public interface ApplicationContext {
    /**
     * 根据类型返回对象
     * @param clazz 类型
     * @return 对象
     */
    Object getBean(Class<?> clazz);
}
```

**⑤编写注解bean容器接口实现**

AnnotationApplicationContext基于注解扫描bean

```java
	public class AnnotationApplicationContext implements ApplicationContext {

    //存储bean的容器
    private HashMap<Class, Object> beanFactory = new HashMap<>();

    @Override
    public Object getBean(Class clazz) {
        return beanFactory.get(clazz);
    }

    /**
     * 根据包扫描加载bean
     * @param basePackage
     */
    public AnnotationApplicationContext(String basePackage) {
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
}
```

**⑥编写扫描bean逻辑**

我们通过构造方法传入包的base路径，扫描被@Bean注解的java对象，完整代码如下：

```java
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
                                // 有接口，用接口作为key
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

    // 属性注入
    loadDi(); // 在后续步骤实现
}

```

**⑦java类标识Bean注解**

```java
@Bean
public class UserServiceImpl implements UserService
```

```java
@Bean
public class UserDaoImpl implements UserDao 
```

**⑧测试Bean加载**

```java
ApplicationContext context = new AnnotationApplicationContextImpl("com.tzuxin");
UserService userService = (UserService)context.getBean(UserService.class);
System.out.println(userService);
```

控制台打印测试

**⑨依赖注入**

只要userDao.print();调用成功，说明就注入成功

```java
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
```

报错了，当前userDao是个空对象

**⑩依赖注入实现**

```java
/**
 * 属性注入
 */
private void loadDi() throws IllegalAccessException {
    Set<Map.Entry<Class<?>, Object>> entrys = beanFactory.entrySet();
    // 1、遍历beanFactory
    for (Map.Entry<Class<?>, Object> entry : entrys) {
        // 2、得到每个对象的属性
        Object obj = entry.getValue();
        // 3、 遍历属性
        Class<?> aClass = obj.getClass();
        // 得到所有属性
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            Di annotation = field.getAnnotation(Di.class);
            // 4、如果属性有@Di注解
            if (annotation != null) {
                // 4.1 如果是私有属性，设置setAccessible(true)
                field.setAccessible(true);
                // 4.2、注入属性
                field.set(obj, beanFactory.get(field.getType()));
            }
        }
    }
}

```

执行第八步：执行成功，依赖注入成功
