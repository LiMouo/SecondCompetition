package limou.com.Test.ServiceTest;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import limou.com.SQLiteCatalog.SQLiteMaster;

public class EnvironServiceTest extends Service {

    private Thread thread_1;
    private Random random = new Random();
    private int min = 10;
    int pm25_num, co2_num, LightIntensity_num, humidity_num, temperature_num, Status_num;
    private int max = 99;
    private int[] arr_1 = new int[6];
    private ContentValues values;
    private String TAG = "EnvironServiceTest";
    private SQLiteDatabase db;
    private Boolean isService = true;

    public EnvironServiceTest() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        db = SQLiteMaster.getInstance(this).getWritableDatabase();
        thread_1 = new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        InitNum();
                        saveData();
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        });
        thread_1.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void saveData() {
        values = new ContentValues();
        values.put("pm25", pm25_num);
        values.put("co2", co2_num);
        values.put("LightIntensity", LightIntensity_num);
        values.put("humidity", humidity_num);
        values.put("temperature", temperature_num);
        values.put("Status", Status_num);
        values.put("datetime", getDate());
        long count = db.insert("EnvironTest", null, values);
        Log.e(TAG, "写入数据库 第: " + count + " 条");
        if (count > 20) {
            db.execSQL("delete from EnvironTest where id = (select id from environ limit 1)");
        }
    }

    private void InitNum() {
            pm25_num = random.nextInt(max) % (max - min + 1) + min;
            co2_num = random.nextInt(max) % (max - min + 1) + min;
            LightIntensity_num = random.nextInt(max) % (max - min + 1) + min;
            humidity_num = random.nextInt(max) % (max - min + 1) + min;
            temperature_num = random.nextInt(max) % (max - min + 1) + min;
            Status_num = random.nextInt(max) % (max - min + 1) + min;
    }

    @Override
    public void onDestroy() {
        thread_1.interrupt();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = format.format(date);
        isService = false;
        return str;
    }
}
