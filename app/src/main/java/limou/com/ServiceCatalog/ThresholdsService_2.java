package limou.com.ServiceCatalog;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import limou.com.NetworkHome.OkHttpData;
import limou.com.secondcompetition.R;

public class ThresholdsService_2 extends Service {
    private String TAG = "ThresholdsService_2";
    private String TAG_arr = "ThresholdsService_Arr";
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private Boolean isTrue;
    private NotificationManager manager;
    private String[] arr_1 = new String[6];
    private String[] arr_2 = new String[6];
    private Handler handler = new Handler();

    public ThresholdsService_2() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: " + "服务器初始化");
        editor = getSharedPreferences("Threshold", MODE_PRIVATE).edit();
        preferences = getSharedPreferences("Threshold", MODE_PRIVATE);
        /*MyNotification_Thresholds();*/
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        isTrue = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId == 1) {
            Log.d(TAG, "阈值服务 启动实体 => " + startId);
            arr_1[0] = preferences.getString("temperature", null);
            arr_1[1] = preferences.getString("humidity", null);
            arr_1[2] = preferences.getString("LightIntensity", null);
            arr_1[3] = preferences.getString("co2", null);
            arr_1[4] = preferences.getString("pm25", null);
            arr_1[5] = preferences.getString("Status", null);
            startT();
        }
        Log.d(TAG, "onStartCommand: " + "服务器连接");
        return super.onStartCommand(intent, flags, startId);
    }

    private void startT() {
        final String url_1 = "http://192.168.3.5:8088/transportservice/action/GetAllSense.do";
        final Map<String, String> map_1 = new HashMap<>();
        map_1.put("UserName", "user1");
        final JSONObject json_1 = new JSONObject(map_1);

        final String url_2 = "http://192.168.3.5:8088/transportservice/action/GetRoadStatus.do";
        final Map<String, String> map_2 = new HashMap<>();
        map_2.put("RoadId", "1");
        map_2.put("UserName", "user1");
        final JSONObject json_2 = new JSONObject(map_2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isTrue) {
                    long startTime = System.currentTimeMillis();
                    try {
                        OkHttpData.sendConnect(url_1, json_1.toString());
                        Log.d(TAG, "环境检查: " + OkHttpData.JsonObjectRead());
                    } catch (Exception e) {
                        e.printStackTrace();
                        error();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int i = 0; i < arr_1.length - 1; i++) {
                                    if (!arr_1[i].equals("")) {
                                        ifThreshold(i);
                                        Log.d(TAG_arr, "arr_2["+i+"] 的值为" + arr_2[2]);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                error();
                            }
                        }
                    });
                    try {
                        long stopTime = System.currentTimeMillis();
                        if (stopTime - startTime >= 0) {
                            Thread.sleep(10000 - (stopTime - startTime));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isTrue) {
                    long startTime = System.currentTimeMillis();
                    try {
                        OkHttpData.sendConnect(url_2, json_2.toString());
                        Log.d(TAG, "道路状态：" + OkHttpData.JsonObjectRead());
                    } catch (Exception e) {
                        e.printStackTrace();
                        error();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!arr_1[5].equals("")) {
                                    ifThreshold(5);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                error();
                            }
                        }
                    });
                    try {
                        long stopTime = System.currentTimeMillis();
                        if (stopTime - startTime >= 0) {
                            Thread.sleep(10000 - (stopTime - startTime));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void ifThreshold(int count) {
        try {
            switch (count) {
                case 0:
                    Log.e(TAG, "温度阈值: " + arr_1[count] + "  当前温度: " + OkHttpData.JsonObjectRead().getString("temperature"));
                    if (Integer.parseInt(arr_1[count]) < Integer.parseInt(OkHttpData.JsonObjectRead().getString("temperature")))
                        createNotification(this, 1, manager, "温度", arr_1[count] + "℃", OkHttpData.JsonObjectRead().getString("temperature") + " ℃");
                    break;
                case 1:
                    Log.d(TAG, "温度阈值: " + arr_1[count] + " 当前湿度：" + OkHttpData.JsonObjectRead().getString("humidity"));
                    if (Integer.parseInt(arr_1[count]) < Integer.parseInt(OkHttpData.JsonObjectRead().getString("humidity")))
                        createNotification(this, 2, manager, "湿度", arr_1[count] + " hPa", OkHttpData.JsonObjectRead().getString("humidity") + " hPa");
                    break;
                case 2:
                    Log.e(TAG, "光照阈值: " + arr_1[count] + "  当前光照: " + OkHttpData.JsonObjectRead().getString("LightIntensity"));
                    if (Integer.parseInt(arr_1[count]) < Integer.parseInt(OkHttpData.JsonObjectRead().getString("LightIntensity")))
                        createNotification(this, 3, manager, "光照", arr_1[count] + " Lux", OkHttpData.JsonObjectRead().getString("LightIntensity") + " Lux");
                    break;
                case 3:
                    Log.e(TAG, "C02阈值: " + arr_1[count] + "  当前C02: " + OkHttpData.JsonObjectRead().getString("co2"));
                    if (Integer.parseInt(arr_1[count]) < Integer.parseInt(OkHttpData.JsonObjectRead().getString("co2")))
                        createNotification(this, 4, manager, "CO₂", arr_1[count] + " mg/m3", OkHttpData.JsonObjectRead().getString("co2") + " mg/m3");
                    break;
                case 4:
                    Log.e(TAG, "PM2.5阈值: " + arr_1[count] + "  当前PM2.5: " + OkHttpData.JsonObjectRead().getString("pm2.5"));
                    if (Integer.parseInt(arr_1[count]) < Integer.parseInt(OkHttpData.JsonObjectRead().getString("pm2.5")))
                        createNotification(this, 5, manager, "PM2.5", arr_1[count] + " μg/m3", OkHttpData.JsonObjectRead().getString("pm2.5") + " μg/m3");
                    break;
                case 5:
                    Log.e(TAG, "道路阈值: " + arr_1[count] + "  当前道路: " + OkHttpData.JsonObjectRead().getString("Status"));
                    if (Integer.parseInt(arr_1[count]) < Integer.parseInt(OkHttpData.JsonObjectRead().getString("Status")))
                        createNotification(this, 6, manager, "道路", arr_1[5], OkHttpData.JsonObjectRead().getString("Status"));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*任务栏通知*/
    private void createNotification(Context context, int id, NotificationManager manager, String name, String n1, String n2) {
        String CHANNEL_ID = "Threshold"; /*频道ID*/
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.d(TAG, "当前SDK_INT=> " + Build.VERSION.SDK_INT);
            Log.d(TAG, "目标VERSION_CODES=> " + Build.VERSION_CODES.O);
            /* 渠道ID                通知状态等级*/
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"1",NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("阈值通知组");    /* 设置渠道描述*/
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
        Intent intent = new Intent(context, ThresholdsService_2.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);/*设置跳转 点击通知跳转页面*/
        notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setContentTitle(name+"报警")
                .setContentText("阈值"+name+"："+n1+" 当前"+name + "："+n2)
                .setAutoCancel(true)                /*点击通知后 关闭通知*/
                .setContentIntent(pendingIntent)    /*设置跳转页面*/
                .setSmallIcon(R.drawable.liaotian)  /*设置显示图标*/
                .setWhen(System.currentTimeMillis())
                .build();
                manager.notify(id,notification);    /*发布要在状态栏中显示的通知。如果您的应用程序已经发布了具有*相同ID的通知，
                                                    但尚未取消，则该*将被更新的信息替换。*/
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: " + "阈值服务关闭");
        isTrue = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        /*throw new UnsupportedOperationException("Not yet implemented");*/
        return null;
    }

    /*protected void MyNotification_Thresholds() {
        NotificationUtil notificationUtil = new NotificationUtil(this);
        notificationUtil.sendNotification(getString(R.string.Thresholds_title), getString(R.string.Thresholds_content));
        Log.d(TAG, "弹窗生成运行");
    }*/

    private void error() {
        Intent stopIntent = new Intent(this, ThresholdsService_2.class);
        stopService(stopIntent); /*关闭服务*/
        editor.putBoolean("isThreshold", false);
        editor.apply();
        Toast.makeText(ThresholdsService_2.this, "网络错误 停止监听 请检查网络设置", Toast.LENGTH_SHORT).show();
    }
}
