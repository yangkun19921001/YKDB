package com.devyk.ykdblib.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 *     author  : devyk on 2019-08-04 20:59
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is Condition
 * </pre>
 */
public class Condition {

    private String whereClause;

    private String [] whereArgs;

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public String[] getWhereArgs() {
        return whereArgs;
    }

    public void setWhereArgs(String[] whereArgs) {
        this.whereArgs = whereArgs;
    }

    public Condition(Map<String,String> whereCasue) {
        //whereArgs 里面的内容存入的 list
        ArrayList list = new ArrayList();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("1=1");

        //取得所有成员变量的名字
        Set<String> keys = whereCasue.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = whereCasue.get(key);
            if (value != null){
                stringBuffer.append(" and " + key + "=?");
                list.add(value);
            }
        }

        this.whereClause = stringBuffer.toString();
        this.whereArgs = (String[]) list.toArray(new String[list.size()]);
    }

    @Override
    public String toString() {
        return "Condition{" +
                "whereClause='" + whereClause + '\'' +
                ", whereArgs=" + Arrays.toString(whereArgs) +
                '}';
    }
}
