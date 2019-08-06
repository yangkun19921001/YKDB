package com.devyk.ykdb.bean;

import com.devyk.ykdb.Contacts;
import com.devyk.ykdblib.annotation.YKField;
import com.devyk.ykdblib.annotation.YKTable;

/**
 * <pre>
 *     author  : devyk on 2019-08-02 16:30
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is Police
 * </pre>
 */
@YKTable("tb_police")
public class Police {

    /**
     * 人员 id
     */
    @YKField("_id")
    private String id;

    /**
     * 人员姓名
     */
    private String name;

    public Police(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Police() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Police{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
