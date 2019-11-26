package limou.com;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import limou.com.Test.ServiceTest.MyService;
import limou.com.Test.Test_Environ;
import limou.com.Test.Test_Thresholds;
import limou.com.ToolsHome.NotificationUtil;
import limou.com.ToolsHome.SecondTitleTools;
import limou.com.secondcompetition.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private Button bt_test_thresholds, bt_bind_service, bt_unbind_service, bt_test_environ, bt_start_service, bt_stop_service;
    private Thread t_1;
    private String TAG = "MyService";
    private Handler handler = new Handler();
    private MyService.DownloadBinder downloadBinder;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private boolean Bind;
    private boolean Unbind;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (MyService.DownloadBinder) service;
            downloadBinder.startDownload();
            downloadBinder.getProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        InitData();
        /*InitNotification();*/
    }

    private void InitData() {
        Bind = preferences.getBoolean("Bind", Bind);
        Log.d(TAG, "Bind 初始为" + Bind);
        Unbind = preferences.getBoolean("Unbind",Unbind);
    }

    private void InitView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        SecondTitleTools.MenuCreate();
        SecondTitleTools.setTitle("主页面");
        SecondTitleTools.InitNetwork(this);

        bt_test_environ = findViewById(R.id.bt_test_Environ);
        bt_test_environ.setOnClickListener(this);

        bt_test_thresholds = findViewById(R.id.bt_test_Thresholds);
        bt_test_thresholds.setOnClickListener(this);

        bt_start_service = findViewById(R.id.start_service);
        bt_start_service.setOnClickListener(this);

        bt_stop_service = findViewById(R.id.stop_service);
        bt_stop_service.setOnClickListener(this);

        bt_bind_service = findViewById(R.id.bind_service);
        bt_unbind_service = findViewById(R.id.unbind_service);

        bt_bind_service.setOnClickListener(this);
        bt_unbind_service.setOnClickListener(this);

        editor = getSharedPreferences("MainActivity", MODE_PRIVATE).edit();
        preferences = getSharedPreferences("MainActivity", MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Intent startService, stopService;
        Intent bindIntent;
        switch (v.getId()) {
            case R.id.bt_test_Environ:
                intent = new Intent(this, Test_Environ.class);
                Toast.makeText(this, "页面已跳转至 Test_Environ", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            case R.id.bt_test_Thresholds:
                intent = new Intent(this, Test_Thresholds.class);
                Toast.makeText(this, "页面已跳转至 Test_Thresholds", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            case R.id.start_service:
                startService = new Intent(this, MyService.class);
                Log.d(TAG, "服务器启动");
                startService(startService);
                break;
            case R.id.stop_service:
                stopService = new Intent(this, MyService.class);
                Log.d(TAG, "服务器停止");
                stopService(stopService);
                break;
            case R.id.bind_service:
                /*if (Bind == false){

                        Bind = true;
                        Unbind = true;
                        editor.putBoolean("Unbind",Unbind);
                        editor.putBoolean("Bind", Bind);
                        editor.apply();
                    }else {
                        Toast.makeText(MainActivity.this, "当前还在下载 不能进行新的下载操作 ", Toast.LENGTH_SHORT).show();
                    }*/
                bindIntent = new Intent(this, MyService.class);
                bindService(bindIntent, connection, BIND_AUTO_CREATE);//绑定服务器
                break;
            case R.id.unbind_service:
                /*if (Bind){
                    if (Unbind){

                        Bind = false;
                        Unbind = false;
                        editor.putBoolean("Unbind",Unbind);
                        editor.putBoolean("Bind",Bind);
                        editor.apply();
                    }else if (Unbind){
                        Toast.makeText(MainActivity.this, "下载服务未开启 操作未生效", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "下载服务未开启 操作未生效 外", Toast.LENGTH_SHORT).show();
                }*/
                ActivityManager myManager = (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
                ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
                for (int i = 0; i < runningService.size(); i++) {
                    Log.d("活着进程"," "+runningService.get(i).service.getClassName());
                    if (runningService.get(i).service.getClassName().equals("limou.com.Test.ServiceTest.MyService")) {
                        unbindService(connection);
//                        getContext().stopService(bindIntent);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (t_1 != null && t_1.isAlive()) {
            t_1.interrupt();
        }
        Bind = false;
        editor.putBoolean("Bind",Bind);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bind = false;
        editor.putBoolean("Bind",Bind);
        editor.apply();
    }

    protected void MyNotification() {
        NotificationUtil notificationUtil = new NotificationUtil(this);
        notificationUtil.sendNotification(getString(R.string.main_title), getString(R.string.main_content));
        Log.d(TAG, "弹窗生成运行");
    }

    private void InitNotification() {
        t_1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    MyNotification();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t_1.start();
    }
}
