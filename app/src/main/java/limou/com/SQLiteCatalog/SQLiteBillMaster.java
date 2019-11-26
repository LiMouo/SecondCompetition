package limou.com.SQLiteCatalog;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class SQLiteBillMaster extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteMaster";
    private Context mContext;
    /* primary key 是主键 autoincrement 是自增序号*/
    private static final String DBRegistry = "create table bill(" +
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
    public SQLiteBillMaster(@Nullable Context context, String DBname) {
        super(context, DBname, null, Constants.VERSION_CODE);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "onCreate: "+"执行" );
        db.execSQL(DBRegistry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists bill");
        onCreate(db);
    }

    private static void insertData(SQLiteDatabase db,int car_id,int money,String user,String datetime){
        ContentValues values = new ContentValues();
        values.put("car_id", car_id);
        values.put("money", money);
        values.put("user", user);
        values.put("datetime", datetime);
        long result = db.insert("bill",null,values);
    }
}
