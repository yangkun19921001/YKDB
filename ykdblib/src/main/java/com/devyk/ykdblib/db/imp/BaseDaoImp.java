package com.devyk.ykdblib.db.imp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.devyk.ykdblib.annotation.YKField;
import com.devyk.ykdblib.annotation.YKTable;
import com.devyk.ykdblib.db.Condition;
import com.devyk.ykdblib.db.IBaseDao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 *     author  : devyk on 2019-07-31 17:17
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is BaseDaoImp
 * </pre>
 */
public class BaseDaoImp<T> implements IBaseDao<T> {
    private String TAG = getClass().getSimpleName();
    /**
     * 持有数据库的引用
     */
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * 表名
     */
    private String mTableName;

    /**
     * 持有操作数据库所对应的 java 类型
     */
    private Class<T> mEntityClass;

    /**
     * 标记：用来表示是否已经初始化操作
     */
    private boolean isInit = false;

    /**
     * 定义一个缓存空间 （key:字段名 value: 成员变量）
     */
    private HashMap<String, Field> mCacheMap;


    /**
     * 初始化
     *
     * @param sqLiteDatabase
     * @param entityClass
     */
    public void init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        this.mSQLiteDatabase = sqLiteDatabase;
        this.mEntityClass = entityClass;

        if (!isInit) {
            //自动建表，取得表名
            if (entityClass != null && (entityClass.getAnnotation(YKTable.class) == null)) {
                //通过反射得到类名
                this.mTableName = entityClass.getSimpleName();
            } else {
                if (TextUtils.isEmpty(entityClass.getAnnotation(YKTable.class).value())) {
                    //如果有注解但是注解为空的话，就取当前 类名
                    this.mTableName = entityClass.getSimpleName();
                } else {
                    //取得注解上面的表名
                    this.mTableName = entityClass.getAnnotation(YKTable.class).value();
                }

            }

            //执行创建表的操作， 使用 getCreateTabeSql () 生成 sql 语句
            String autoCreateTabSql = getCreateTableSql();
            Log.i(TAG, "tagSQL-->" + autoCreateTabSql);
            //执行创建表的 SQL
            this.mSQLiteDatabase.execSQL(autoCreateTabSql);
            mCacheMap = new HashMap<>();
            initCacheMap();
            isInit = true;

        }
    }

    private void initCacheMap() {
        //取得所有字段名称
        String sql = "select * from " + mTableName + " limit 1, 0";//创建一个空间 sql
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        //2. 取得所有的成员变量
        Field[] declaredFields = mEntityClass.getDeclaredFields();


        //字段 跟 成员变量
        for (String columnName : columnNames) {
            Field columnField = null;
            for (Field field : declaredFields) {
                //访问权限打开
                field.setAccessible(true);
                String fileName = ""; //对象中成员变量名字
                if (field.getAnnotation(YKField.class) != null) {
                    fileName = field.getAnnotation(YKField.class).value();
                } else {
                    fileName = field.getName();
                }

                if (columnName.equals(fileName)) {//匹配
                    columnField = field;
                    break;
                }
            }


            if (columnField != null) {
                mCacheMap.put(columnName, columnField);
            }
        }
    }

    /**
     * 需要被执行创建表的 sql
     *
     * @return
     */
    private String getCreateTableSql() {
        StringBuffer sb = new StringBuffer();
        sb.append("create table if not exists ").append(mTableName + "(");

        //反射得到所有的成员变量
        Field[] declaredFields = mEntityClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            //首先拿到成员的属性
            Class<?> type = declaredField.getType();
            //通过注解获取
            if (declaredField.getAnnotation(YKField.class) != null) {
                //通过注解获取
                if (type == String.class) {
                    sb.append(declaredField.getAnnotation(YKField.class).value() + " TEXT,");
                } else if (type == Integer.class) {
                    sb.append(declaredField.getAnnotation(YKField.class).value() + " INTEGER,");
                } else if (type == Long.class) {
                    sb.append(declaredField.getAnnotation(YKField.class).value() + " BIGINT,");
                } else if (type == Double.class) {
                    sb.append(declaredField.getAnnotation(YKField.class).value() + " DOUBLE,");
                } else if (type == byte[].class) {
                    sb.append(declaredField.getAnnotation(YKField.class).value() + " BLOB,");
                } else {
                    //不支持的类型
                    continue;
                }
            } else {
                //通过反射获取
                if (type == String.class) {
                    sb.append(declaredField.getName() + " TEXT,");
                } else if (type == Integer.class) {
                    sb.append(declaredField.getName() + " INTEGER,");
                } else if (type == Long.class) {
                    sb.append(declaredField.getName() + " BIGINT,");
                } else if (type == Double.class) {
                    sb.append(declaredField.getName() + " DOUBLE,");
                } else if (type == byte[].class) {
                    sb.append(declaredField.getName() + " BLOB,");
                } else {
                    //不支持的类型
                    continue;
                }
            }
        }

        //删除最后一位
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);

        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 插入数据
     * @param entity
     * @return
     */
    @Override
    public long insert(T entity) {
        //1. 准备好 ContentValues 中需要的数据
        Map<String, String> map = getValues(entity);
        if (map == null || map.size() == 0) return 0;
        //2. 把数据转移到 ContentValues 中
        ContentValues values = getContentValues(map);
        //将数据插入表中
        return mSQLiteDatabase.insert(mTableName, null, values);
    }

    /**
     * @param map 成员变量的名字，成员变量的值
     * @return
     */
    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String values = map.get(key);
            if (values != null) {
                contentValues.put(key, values);
            }
        }
        return contentValues;
    }

    /**
     * key(字段) - values(成员变量) ---》getValues 后 ---》key (成员变量的名字) ---values 成员变量的值 id 1，name alan , password 123
     *
     * @param entity
     * @return
     */
    private Map<String, String> getValues(T entity) {
        HashMap<String, String> map = new HashMap<>();
        //返回所有的成员变量
        Iterator<Field> iterator = mCacheMap.values().iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            field.setAccessible(true);

            try {
                Object object = field.get(entity);
                if (object == null) {
                    continue;
                }
                String values = object.toString();

                String key = "";

                if (field.getAnnotation(YKField.class) != null) {
                    key = field.getAnnotation(YKField.class).value();
                } else {
                    key = field.getName();

                }

                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(values)) {
                    map.put(key, values);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return map;
    }

    /**
     * 更新数据
     *
     * @param entity 更新的数据实体
     * @param where 更新的条件
     * @return
     */
    @Override
    public long updata(T entity, T where) {
//        mSQLiteDatabase.update(mTableName,contentValues,"id = ?",new String[]{"1"});
        //准备好 ContentValues 数据
        Map<String, String> values = getValues(entity);
        ContentValues contentValues = getContentValues(values);

        //条件
        Map<String, String> whereMp = getValues(where);
        Condition condition = new Condition(whereMp);
        return  mSQLiteDatabase.update(mTableName,contentValues,condition.getWhereClause(),condition.getWhereArgs());

    }

    /**
     * 删除数据
     *
     * @param where
     * @return
     */
    @Override
    public int delete(T where) {
        //        mSQLiteDatabase.delete(mTableName,"id = ?",new String[]{"1"});
        //准备好 ContentValues 中的数据
        Map<String, String> values = getValues(where);
        Condition condition = new Condition(values);
        int delete = mSQLiteDatabase.delete(mTableName, condition.getWhereClause(), condition.getWhereArgs());
        return delete;
    }

    /**
     * 查询数据
     *
     * @param where
     * @return
     */
    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    /**
     * 根据条件查询数据
     *
     * @param where
     * @param orderBy
     * @param startIndex
     * @param limit
     * @return
     */
    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        //1. 准备好 ContentValues 需要的数据
        Map<String, String> values = getValues(where);
        String limitString = ""; //"2,6"
        if (startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }

        Condition condition = new Condition(values);

        Cursor cursor = mSQLiteDatabase.query(mTableName, null, condition.getWhereClause()
                , condition.getWhereArgs(), null, limitString,  orderBy);

        List<T> result = getResult(cursor, where);

        return result;
    }

    /**
     * 得到查询出来的数据
     *
     * @param query
     * @param where
     * @return
     */
    private List<T> getResult(Cursor query, T where) {
        ArrayList list = new ArrayList();
        Object item = null;
        while (query.moveToNext()) {
            try {
                item = where.getClass().newInstance();
                //cache (字段 -- 成员变量的名字)
                Iterator<Map.Entry<String, Field>> iterator = mCacheMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Field> entry = iterator.next();
                    //取列明
                    String columnName = entry.getKey();
                    //以列名拿到列名在游标中的位置
                    int columnIndex = query.getColumnIndex(columnName);

                    //拿到 id
                    Field value = entry.getValue();
                    Class<?> type = value.getType();

                    if (columnIndex != -1) {
                        if (type == String.class) {
                            value.set(item, query.getString(columnIndex));//setId(1)
                        } else if (type == Double.class) {
                            value.set(item, query.getDouble(columnIndex));

                        } else if (type == Integer.class) {

                            value.set(item, query.getInt(columnIndex));
                        } else if (type == Long.class) {

                            value.set(item, query.getLong(columnIndex));
                        } else if (type == byte[].class) {
                            value.set(item, query.getBlob(columnIndex));
                        } else {
                            continue;
                        }
                    }

                }

                list.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        query.close();
        return list;
    }
}
