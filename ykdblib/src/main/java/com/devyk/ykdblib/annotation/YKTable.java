package com.devyk.ykdblib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *     author  : devyk on 2019-07-31 17:06
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is YKTable
 * </pre>
 */
@Target(ElementType.TYPE) //声明在类上面
@Retention(RetentionPolicy.RUNTIME)
public @interface YKTable {
    String value(); //用于创建数据库中的表名
}
