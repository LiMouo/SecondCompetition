package limou.com.SQLiteCatalog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class SQLiteMaster extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteMaster";
    private static SQLiteMaster mInstance = null;

    private Context mContext;
    /* primary key 是主键 autoincrement 是自增序号*/
    private static final String DBRegistry = "create table Users(" +
            "id integer primary key autoincrement," +
            "username text," +
            "password text," +
            "phone text)";
    private static final String DBEnviron = "create table Environ(" +
            "id integer primary key autoincrement," +
            "temperature integer," +    /*温度*/
            "humidity integer," +       /*湿度*/
            "LightIntensity integer," + /*光照*/
            "co2 integer," +            /*CQ2*/
            "pm25 integer," +           /*pm2.5*/
            "Status integer,"+          /*道路状态*/
            "datetime text)";           /*时间*/
    private static final String DBEnvironTest = "create table EnvironTest(" +
            "id integer primary key autoincrement," +
            "temperature integer," +    /*温度*/
            "humidity integer," +       /*湿度*/
            "LightIntensity integer," + /*光照*/
            "co2 integer," +            /*CQ2*/
            "pm25 integer," +           /*pm2.5*/
            "Status integer,"+          /*道路状态*/
            "datetime text)";           /*时间*/
    private static final String DBEnvironTestData = "create table EnvironTestData(" +
            "id integer primary key autoincrement," +
            "temperature integer," +    /*温度*/
            "humidity integer," +       /*湿度*/
            "LightIntensity integer," + /*光照*/
            "co2 integer," +            /*CQ2*/
            "pm25 integer," +           /*pm2.5*/
            "Status integer,"+          /*道路状态*/
            "datetime text)";           /*时间*/

    private static final String DBThresholdsService = "create table ThresholdsService(" +
            "id integer primary key autoincrement," +
            "temperature integer," +    /*温度*/
            "humidity integer," +       /*湿度*/
            "LightIntensity integer," + /*光照*/
            "co2 integer," +            /*CQ2*/
            "pm25 integer," +           /*pm2.5*/
            "Status integer,"+          /*道路状态*/
            "datetime text)";           /*时间*/

    private static final String DBBillM = "create table Bill(" +
            "id integer primary key autoincrement," +
            "car_id integer," +
            "money integer," +
            "user text," +
            "datetime text)";

    /**
     * context 上下文
     * name    数据库名称
     * factory 游标工厂
     * version 版本号
     */

    public SQLiteMaster(@Nullable Context context) {
        super(context, "INFO.db", null, Constants.VERSION_CODE);
        this.mContext = context;
    }

    public synchronized static SQLiteMaster getInstance(Context context){
        if(mInstance == null)
            mInstance = new SQLiteMaster(context);
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "开始创建数据库");
        db.execSQL(DBRegistry);
        db.execSQL(DBEnviron);
        db.execSQL(DBBillM);
        db.execSQL(DBEnvironTest);
        db.execSQL(DBEnvironTestData);
        db.execSQL(DBThresholdsService);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists Users");
        db.execSQL("drop table if exists Environ");
        db.execSQL("drop table if exists EnvironTest");
        db.execSQL("drop table if exists EnvironTestData");
        db.execSQL("drop table if exists Bill");
        db.execSQL("drop table if exists DBThresholdsService");
        db.execSQL(DBRegistry);
        db.execSQL(DBEnviron);
        db.execSQL(DBBillM);
        db.execSQL(DBEnvironTest);
        db.execSQL(DBEnvironTestData);
        db.execSQL(DBThresholdsService);
    }
}
