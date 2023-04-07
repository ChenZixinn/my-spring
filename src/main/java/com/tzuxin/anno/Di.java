package com.tzuxin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 依赖注入
 */
@Target({ElementType.FIELD}) // 作用在属性上
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
public @interface Di {
}
