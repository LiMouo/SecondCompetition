package limou.com.ToolsHome;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import limou.com.ServiceCatalog.ThresholdsService_2;
import limou.com.secondcompetition.R;

public class NotificationThresholds extends ContextWrapper {

    private String TAG ="NotificationThresholds";

    public NotificationThresholds(Context base) {
        super(base);
    }

    public void createNotification(Context context, int id, NotificationManager manager, String name, String n1, String n2) {
        String CHANNEL_ID = "Threshold"; /*频道ID*/
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.d(TAG, "当前SDK_INT=> " + Build.VERSION.SDK_INT);
            Log.d(TAG, "目标VERSION_CODES=> " + Build.VERSION_CODES.O);
            /* 渠道ID                通知状态等级*/
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"1", NotificationManager.IMPORTANCE_HIGH);
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
}