package limou.com.SQLiteCatalog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class SQLiteEnvironMaster extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteMaster";
    private Context mContext;
    /* primary key 是主键 autoincrement 是自增序号*/
    private static final String DBRegistry = "create table environ(" +
            "id integer primary key autoincrement," +
            "temperature integer," +    /*温度*/
            "humidity integer," +       /*湿度*/
            "LightIntensity integer," + /*光照*/
            "co2 integer," +            /*CQ2*/
            "pm25 integer," +           /*pm2.5*/
            "Status integer,"+          /*道路状态*/
            "datetime text)";           /*时间*/

    /**
     * context 上下文
     * name    数据库名称
     * factory 游标工厂
     * version 版本号
     */
    public SQLiteEnvironMaster(@Nullable Context context, String DBname) {
        super(context, DBname, null, Constants.VERSION_CODE);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.e(TAG, "开始创建数据库");
        sqLiteDatabase.execSQL(DBRegistry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
