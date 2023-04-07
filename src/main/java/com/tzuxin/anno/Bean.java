package com.tzuxin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明对象
 */
@Target(ElementType.TYPE) // 作用在类上
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
public @interface Bean {
}
