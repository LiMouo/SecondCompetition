package limou.com.ServiceCatalog;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import limou.com.NetworkHome.OkHttpData;
import limou.com.SQLiteCatalog.SQLiteMaster;
import limou.com.ThresholdsCatalog.ThresholdsActivity;
import limou.com.ThresholdsCatalog.ThresholdsGson;
import limou.com.secondcompetition.R;

public class ThresholdsService_3 extends Service {
    private String TAG = "ThresholdsActivity";
    private SharedPreferences preferences_read,preferences_write;
    private SharedPreferences.Editor editor ;
    private int arr_1[] = new int[6]; //获取到的值
    private String arr_2[] = new String[6]; //用户设置阈值
    private int arr_3[] = new int[6]; //将用户设置阈值的值准换为int
    private SQLiteDatabase db;
    private ContentValues values = new ContentValues();
    private Thread t_1,t_2;
    private String url = "http://192.168.3.5:8088/transportservice/action/GetAllSense.do";
    private String url_2 = "http://192.168.3.5:8088/transportservice/action/GetRoadStatus.do";
    private ThresholdsGson thresholdsGson,thresholdsGson_2;
    private Handler handler = new Handler();
    private NotificationManager manager;

    public ThresholdsService_3() {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        InitEvent();//初始化数据
        InsertData();//将数据进行存储
        InitComparison();//将数据与用户设置阈值进行对比
        Log.d(TAG, "阈值服务器启动");
        super.onCreate();
    }

    private void InsertData() {
        arr_2[0] = preferences_read.getString("temperature","");
        arr_2[1] = preferences_read.getString("humidity","");
        arr_2[2] = preferences_read.getString("LightIntensity","");
        arr_2[3] = preferences_read.getString("co2","");
        arr_2[4] = preferences_read.getString("pm2.5","");
        arr_2[5] = preferences_read.getString("Status","");

        arr_3[0] = Integer.parseInt(arr_2[0]);
        arr_3[1] = Integer.parseInt(arr_2[1]);
        arr_3[2] = Integer.parseInt(arr_2[2]);
        arr_3[3] = Integer.parseInt(arr_2[3]);
        arr_3[4] = Integer.parseInt(arr_2[4]);
        arr_3[5] = Integer.parseInt(arr_2[5]);
        Log.d(TAG, "arr_2[0]  数据是"+arr_2[0]  + "   arr_3[0]  数据是"+arr_3[0] +"  arr_1[0] 的数据是" + arr_1[0]);
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
                        thresholdsGson = gson_1.fromJson(OkHttpData.JsonObjectRead().toString(), ThresholdsGson.class);
                        values.put("pm25", thresholdsGson.get_$Pm2526());
                        values.put("co2", thresholdsGson.getCo2());
                        values.put("LightIntensity", thresholdsGson.getLightIntensity());
                        values.put("humidity", thresholdsGson.getHumidity());
                        values.put("temperature", thresholdsGson.getTemperature());


                        OkHttpData.sendConnect(url_2, json_2.toString());
                        thresholdsGson_2 = gson_2.fromJson(OkHttpData.JsonObjectRead().toString(), ThresholdsGson.class);
                        values.put("Status", thresholdsGson_2.getStatus());
                        values.put("datetime", getDate());

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                long count = db.insert("ThresholdsService_2", null, values);
                                Log.e(TAG, "写入数据库 第: " + count + " 条");
                                if (count > 20) {
                                    db.execSQL("delete from ThresholdsService where id = (select id from environ limit 1)");
                                }
                            }
                        });

                        try {
                            long endTime = System.currentTimeMillis();
                            Log.d(TAG, "代码运行时间" + (endTime - startTime) + "ms");//输出程序运行时间

                            if ((endTime - startTime) > 10000) {
                            } else {
                                Thread.sleep(10000 - (endTime - startTime));
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

    private void InitComparison() {
        final Cursor cursor = db.query("ThresholdsService_2",null,null,null,null,null,null);
        t_2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                    long startTime = System.currentTimeMillis();
                    if (cursor.moveToFirst()){
                        arr_1[0] = cursor.getInt(cursor.getColumnIndex("pm25"));
                        Log.d(TAG, "t_2的数据 arr_1[0] " + arr_1[0]);
                        arr_1[1] = cursor.getInt(cursor.getColumnIndex("co2"));
                        arr_1[2] = cursor.getInt(cursor.getColumnIndex("LightIntensity"));
                        arr_1[3] = cursor.getInt(cursor.getColumnIndex("humidity"));
                        arr_1[4] = cursor.getInt(cursor.getColumnIndex("temperature"));
                        arr_1[5] = cursor.getInt(cursor.getColumnIndex("Status"));
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            temperature:{//温度4
                                humidity:{//湿度3
                                    LightIntensity:{//光照2
                                        co2:{//1
                                            pm25:{//0
                                                Status:{//5
                                                    Comparison:{
                                                    if (arr_3[4] > arr_1[4])break temperature;
                                                    if (arr_3[3] > arr_1[3])break humidity;
                                                    if (arr_3[2] > arr_1[2])break LightIntensity;
                                                    if (arr_3[1] > arr_1[1])break co2;
                                                    if (arr_3[0] > arr_1[0])break pm25;
                                                    if (arr_3[5] > arr_1[5])break Status;
                                                    }
                                                }createNotification(ThresholdsService_3.this, 6, manager, "道路",  String.valueOf(arr_3[5]), String.valueOf(arr_1[4]));
                                            }createNotification(ThresholdsService_3.this, 5, manager, "PM2.5", arr_3[0] + " μg/m3", arr_1[0] + " μg/m3");
                                        }createNotification(ThresholdsService_3.this, 4, manager, "CO₂", arr_3[1] + " mg/m3", arr_1[1] + " mg/m3");
                                    }createNotification(ThresholdsService_3.this, 3, manager, "光照", arr_3[2] + " Lux", arr_1[2] + " Lux");
                                }createNotification(ThresholdsService_3.this, 2, manager, "湿度", arr_3[3] + " hPa", arr_1[3] + " hPa");
                               }createNotification(ThresholdsService_3.this, 1, manager, "温度", arr_3[4] + " ℃", arr_1[4] + " ℃");
                        }
                    });
                        long endTime = System.currentTimeMillis();
                        Log.d(TAG, "代码运行时间" + (endTime - startTime) + "ms");//输出程序运行时间
                        if ((endTime - startTime) > 10000) {
                        } else {
                            Thread.sleep(10000 - (endTime - startTime));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        t_2.start();
    }


    private void InitEvent() {
        editor = getSharedPreferences("ThresholdsService_2",MODE_PRIVATE).edit();
        preferences_read = getSharedPreferences("Threshold",MODE_PRIVATE);
        preferences_write = getSharedPreferences("ThresholdsService_2",MODE_PRIVATE);
        db = SQLiteMaster.getInstance(this).getWritableDatabase();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "阈值服务器连接");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "阈值服务器关闭");
        if (t_1.isAlive()) {
            t_1.interrupt();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (t_2.isAlive()) {
            t_2.interrupt();
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

    /*任务栏通知*/
    private void createNotification(Context context, int id, NotificationManager manager, String name, String n1, String n2) {
        String CHANNEL_ID = "Threshold"; /*频道ID*/
        /*判断API Android 版本 大于26 则执行*/
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e(TAG, "当前SDK_INT=> " + Build.VERSION.SDK_INT);
            Log.e(TAG, "目标VERSION_CODES=> " + Build.VERSION_CODES.O);
            /* 渠道ID                通知状态等级*/
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("阈值通知组");   /* 设置渠道描述*/
            channel.canBypassDnd();                 /* 是否绕过勿扰模式*/
            channel.setBypassDnd(true);             /* 设置绕过勿扰模式*/
            channel.canShowBadge();                 /* 桌面Launcher的消息角标*/
            channel.setShowBadge(true);             /* 桌面Launcher的消息角标*/
            channel.setSound(null, null); /*设置通知出现时的声音,默认是有声音*/
            channel.enableLights(true);             /*设置通知时出现时闪烁呼吸灯*/
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(false);         /*设置通知 是否出现震动*/
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            manager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(context, ThresholdsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0); /*设置跳转 点击通知跳转页面*/
        notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(name + "报警")
                .setContentText("阈值" + name + ": " + n1 + "  当前" + name + ": " + n2)
                .setAutoCancel(true)              /*点击通知后 关闭通知*/
                .setContentIntent(pendingIntent) /*设置跳转页面*/
                .setSmallIcon(R.drawable.liaotian)   /*设置显示图标*/
                .setWhen(System.currentTimeMillis())
                .build();
        manager.notify(id, notification);
    }
}
