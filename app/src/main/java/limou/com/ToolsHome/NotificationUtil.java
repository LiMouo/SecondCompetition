package limou.com.ToolsHome;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import limou.com.secondcompetition.R;

public class NotificationUtil extends ContextWrapper {

    private NotificationManager mManager;
    public static final String sID = "channel_1";
    public static final String sName = "channel_name_1";
    private String TAG = "NotificationUtil";


    public NotificationUtil(Context base) {
        super(base);
    }

    public void sendNotification(String title, String content) {
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            Notification notification = getNotification_26(title, content).build();
            getmManager().notify(1, notification);
        } else {
            Notification notification = getNotification_25(title, content).build();
            getmManager().notify(1, notification);
        }
    }

    private NotificationManager getmManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(sID, sName, NotificationManager.IMPORTANCE_HIGH);
        getmManager().createNotificationChannel(channel);
    }

    public NotificationCompat.Builder getNotification_25(String title, String content) {

        Log.d(TAG, "_25" + "运行");
        // 以下是展示大图的通知
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setBigContentTitle("BigContentTitle");
        style.setSummaryText("SummaryText");
        style.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.pic));

        // 以下是展示多文本通知
        NotificationCompat.BigTextStyle style1 = new NotificationCompat.BigTextStyle();
        style1.setBigContentTitle(title);
        style1.bigText(content);
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                 .setSmallIcon(R.drawable.ma_min)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ma_min))
                .setStyle(style)
                .setAutoCancel(true);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification_26(String title, String content) {
        Log.d(TAG, "_26" + "运行");
        return new Notification.Builder(getApplicationContext(), sID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round))
               .setStyle(new Notification.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.pic)))
                .setNumber(1)
                .setAutoCancel(true);
    }
}
