package com.devyk.ykdb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.devyk.ykdb.bean.Police;
import com.devyk.ykdblib.db.BaseDaoFactory;
import com.devyk.ykdblib.db.imp.BaseDaoImp;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BaseDaoImp<Police> mBaseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBaseDao = BaseDaoFactory.getOurInstance().getBaseDao(Police.class);

    }

    /**
     * 插入数据
     *
     * @param view
     */
    public void insert(View view) {
        long index = mBaseDao.insert(new Police("01", "yangkun"));
        showToast("插入数据成功");
    }


    void showToast(String meg) {
        Toast.makeText(getApplicationContext(), meg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 查询数据
     * @param view
     */
    public void query(View view) {

        Police police = new Police();
        police.setId("02");
        List<Police> policeLists = mBaseDao.query(police);
        Log.i(TAG,policeLists.toString());
        Toast.makeText(getApplicationContext(),"查询到了-》"+policeLists.size(),Toast.LENGTH_LONG).show();

    }

    /**
     * 修改
     * @param view
     */
    public void undata(View view) {
        long updata = mBaseDao.updata(new Police("02", "DevYK"), new Police("01", "yangkun"));
        Toast.makeText(getApplicationContext(),"更新-》"+updata + " 行",Toast.LENGTH_LONG).show();

    }

    /**
     * 删除数据
     * @param view
     */
    public void delete(View view) {
        int index = mBaseDao.delete(new Police("01", "yangkun"));
        Toast.makeText(getApplicationContext(),"删除了-》"+index + " 行",Toast.LENGTH_LONG).show();
    }
}
