package limou.com.EnvironCatalog;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import limou.com.NetworkHome.OkHttpData;
import limou.com.SQLiteCatalog.SQLiteMaster;
import limou.com.ThresholdsCatalog.ThresholdsGson;

public class EnvironService extends Service {

    private SQLiteDatabase db;
    private int[] arr_1 = new int[6];
    private String url = "http://192.168.3.5:8088/transportservice/action/GetAllSense.do";
    private String url_2 = "http://192.168.3.5:8088/transportservice/action/GetRoadStatus.do";
    private ThresholdsGson thresholdsGson;
    private int mPm25, mCo2, mLightIntensity, mHumidity, mTemperature, mStatus;
    private EnvironGson environGson, environGson_2;
    private Handler handler = new Handler();
    private ContentValues values = new ContentValues();
    private String TAG = "EnvironServiceTest";
    private EnvironAdapter adapter;
    private Thread t_1;
    private Boolean Run = true;

    public EnvironService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "Environ 服务器启动: ");
        db = SQLiteMaster.getInstance(this).getWritableDatabase();
        getData();
        super.onCreate();
    }

    private void getData() {
        t_1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long startTime = System.currentTimeMillis();
                    JSONObject json_1 = new JSONObject();
                    JSONObject json_2 = new JSONObject();
                    Gson gson_1 = new Gson();
                    Gson gson_2 = new Gson();
                    try {
                        json_1.put("UserName", "user1");
                        json_2.put("RoadId", "1");
                        json_2.put("UserName", "user1");

                        OkHttpData.sendConnect(url, json_1.toString());
                        environGson = gson_1.fromJson(OkHttpData.JsonObjectRead().toString(), EnvironGson.class);
                        values.put("pm25", environGson.get_$Pm2526());
                        values.put("co2", environGson.getCo2());
                        values.put("LightIntensity", environGson.getLightIntensity());
                        values.put("humidity", environGson.getHumidity());
                        values.put("temperature", environGson.getTemperature());


                        OkHttpData.sendConnect(url_2, json_2.toString());
                        environGson_2 = gson_2.fromJson(OkHttpData.JsonObjectRead().toString(), EnvironGson.class);
                        values.put("Status", environGson_2.getStatus());
                        values.put("datetime", getDate());

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                long count = db.insert("environ", null, values);
                                Log.e(TAG, "写入数据库 第: " + count + " 条");
                                if (count > 20) {
                                    db.execSQL("delete from environ where id = (select id from environ limit 1)");
                                }
                            }
                        });

                        try {
                            long endTime = System.currentTimeMillis();
                            Log.d(TAG, "代码运行时间" + (endTime - startTime) + "ms");//输出程序运行时间

                            if ((endTime - startTime) > 30000) {
                            } else {
                                Thread.sleep(3000 - (endTime - startTime));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        t_1.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Environ 服务器连接: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Environ 服务器关闭: ");
        if (t_1.isAlive()) {
            t_1.interrupt();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = format.format(date);
        return str;
    }
}
