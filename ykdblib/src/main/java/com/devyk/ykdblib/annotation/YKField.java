package com.devyk.ykdblib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *     author  : devyk on 2019-07-31 17:03
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is YKField
 * </pre>
 */

@Target(ElementType.FIELD) //用于描述属性
@Retention(RetentionPolicy.RUNTIME) //运行时期
public @interface YKField {
    String value(); //主键
}
