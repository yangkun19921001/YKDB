package com.devyk.ykdblib.db;

import java.util.List;

/**
 * <pre>
 *     author  : devyk on 2019-07-31 17:10
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is IBaseDao
 * </pre>
 */
public interface IBaseDao<T> {
    //插入数据
    long insert(T entity);

    //更新数据
    long updata(T entity, T where);

    //删除数据
    int delete(T where);

    //查询数据
    List<T> query(T where);

    //根据条件查询
    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);
}
