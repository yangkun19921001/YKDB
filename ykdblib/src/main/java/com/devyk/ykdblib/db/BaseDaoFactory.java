package com.devyk.ykdblib.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.devyk.ykdblib.db.imp.BaseDaoImp;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     author  : devyk on 2019-08-01 20:02
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is BaseDaoFactory
 * </pre>
 */
public class BaseDaoFactory {

    private static final BaseDaoFactory sOurInstance = new BaseDaoFactory();
    private final File file;

    private SQLiteDatabase mSqLiteDatabase;

    //定义数据库的路径
    private String mSqliteDatabasePath;

    //创建一个数据库连接池，安全的
    protected Map<String, BaseDaoImp> baseDaoImpMap = Collections.synchronizedMap(new HashMap<String, BaseDaoImp>());


    public static BaseDaoFactory getOurInstance() {
        return sOurInstance;
    }

    public BaseDaoFactory() {
        //数据库在 SD 便于 copy 保存
        mSqliteDatabasePath = Environment.getExternalStorageDirectory() + "/DevYK";
        file = new File(mSqliteDatabasePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File path = new File(mSqliteDatabasePath + "/t01.db");
        ;
        if (!path.exists()) {
            try {
                path.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.mSqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(mSqliteDatabasePath = path.getAbsolutePath(), null);
    }

    /**
     * 相当于获取数据库表里面内容的实例
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> BaseDaoImp<T> getBaseDao(Class<T> entityClass) {
        BaseDaoImp<T> baseDaoImp = null;
        try {
            //直接调用反射实例化对象
            baseDaoImp = BaseDaoImp.class.newInstance();
            baseDaoImp.init(mSqLiteDatabase, entityClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return baseDaoImp;
    }

    public <T extends BaseDaoImp<M>, M> T getBaseDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDaoImp baseDao = null;
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(mSqLiteDatabase, entityClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }
}
