package limou.com.EnvironCatalog;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import limou.com.NetworkHome.OKHttpJson;
import limou.com.SQLiteCatalog.SQLiteEnvironMaster;
import limou.com.ToolsHome.SecondTitleTools;
import limou.com.secondcompetition.R;
import okhttp3.MediaType;

public class EnvironActivity extends AppCompatActivity{

    private String url = "http://192.168.3.5:8088/transportservice/action/GetAllSense.do";
    private String url_2 = "http://192.168.3.5:8088/transportservice/action/GetRoadStatus.do";
    private EnvironAdapter adapter;
    private RecyclerView mRv_environ;
    private int[] arr_1 = new int[6];
    private MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    private EnvironGson environGson;
    private EnvironGson environGson_2;
    private Handler handler = new Handler();
    private int mPm25;
    private int mCo2;
    private int mLightIntensity;
    private int mHumidity;
    private int mTemperature;
    private int mStatus;
    private String TAG = "EnvironActivity";
    private int o = 0;
    private SQLiteDatabase db;
    private ContentValues values;
    private Thread t_1;
    private Thread t_2;
    private boolean run = true;
    private int min=10;
    private int max=99;
    private Random random = new Random();
    int pm25_num,co2_num,LightIntensity_num,humidity_num,temperature_num,Status_num ;
    private boolean network;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        InitView();
        network = SecondTitleTools.InitNetwork(this);
        if (network == false){
            InitEvent_2();
            System.out.println(network + "网络关闭");
        }else {
            InitEvent_1();
            System.out.println(network + "网络开启");
        }
    }

    private void InitEvent_2() {
        t_2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (run == true){
                            InitData_2();
                            run = false;
                        }
                        if(run == false){
                            pm25_num= random.nextInt(max)%(max-min+1) + min;
                            co2_num= random.nextInt(max)%(max-min+1) + min;
                            LightIntensity_num= random.nextInt(max)%(max-min+1) + min;
                            humidity_num= random.nextInt(max)%(max-min+1) + min;
                            temperature_num= random.nextInt(max)%(max-min+1) + min;
                            Status_num= random.nextInt(max)%(max-min+1) + min;
                            Thread.sleep(3000);
                            InitData_2();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });t_2.start();
    }

    private void InitData_2() {

        handler.post(new Runnable() {
            @Override
            public void run() {

                arr_1[0] = pm25_num;
                arr_1[1] = co2_num;
                arr_1[2] = LightIntensity_num;
                arr_1[3] = humidity_num;
                arr_1[4] = temperature_num;
                arr_1[5] = Status_num;
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void InitEvent_1() {
       t_1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        if (run == true){
                            InitData_1();
                            run = false;
                        }
                        if (run == false){
                            Thread.sleep(3000); //线程休眠
                            InitData_1();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d("线程", "循环线程 结束");
                        break;
                    }
                }
            }
        });
        t_1.start();
    }

    private void InitData_1() {
        db = new SQLiteEnvironMaster(this,"Bill.db").getWritableDatabase();
        new Thread(new Runnable() {
            @Override
            public void run() {
        for (int i = 0; i < arr_1.length; i++) {
            if (i <= 4) {
                JSONObject json = new JSONObject();
                try {
                    json.put("UserName", "user1");
                    OKHttpJson.SendOKHttp(url, mediaType,json);
                    Gson gson = new Gson();
                    environGson = gson.fromJson(OKHttpJson.JsonObjectRead().toString(),EnvironGson.class);
                    mPm25 = environGson.get_$Pm2526();
                    mCo2 = environGson.getCo2();
                    mLightIntensity = environGson.getLightIntensity();
                    mHumidity = environGson.getHumidity();
                    mTemperature = environGson.getTemperature();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if (i>4 && i<=5){
                JSONObject json = new JSONObject();
                try {
                    json.put("RoadId",1);
                    json.put("UserName","user1");
                    OKHttpJson.SendOKHttp(url_2, mediaType,json);
                    Gson gson_2 = new Gson();
                    environGson_2 = gson_2.fromJson(OKHttpJson.JsonObjectRead().toString(),EnvironGson.class);
                    mStatus = environGson_2.getStatus();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "handler 运行");
                values = new ContentValues();
                values.put("pm25",mPm25);
                values.put("co2",mCo2);
                values.put("LightIntensity",mLightIntensity);
                values.put("humidity",mHumidity);
                values.put("temperature",mTemperature);
                values.put("Status",mStatus);
                values.put("datetime",getDate());

                long count = db.insert("environ", null, values);
                Log.e(TAG, "写入数据库 第: " + count + " 条");
                if (count > 20) {
                    db.execSQL("delete from environ where id = (select id from environ limit 1)");
                }
                arr_1[0] = Integer.parseInt(values.getAsString("pm25"));
                arr_1[1] = Integer.parseInt(values.getAsString("co2"));
                arr_1[2] = Integer.parseInt(values.getAsString("LightIntensity"));
                arr_1[3] = Integer.parseInt(values.getAsString("humidity"));
                arr_1[4] = Integer.parseInt(values.getAsString("temperature"));
                arr_1[5] = Integer.parseInt(values.getAsString("Status"));
                adapter.notifyDataSetChanged();
                Log.d(TAG, "handler 结束");
            }
        });
            }
        }).start();
        o++;
        Log.d("线程", "循环线程第: " + o +"次");
    }

    /**
     * 初始化控件
     */
    private void InitView() {
        setContentView(R.layout.activity_environ);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SecondTitleTools.setTitle("环境指标");
        SecondTitleTools.MenuCreate();

        mRv_environ = findViewById(R.id.rv_Environ);
        mRv_environ.setLayoutManager(new GridLayoutManager(this, 3));
        mRv_environ.addItemDecoration(new MyDecoration());
        adapter = new EnvironAdapter(EnvironActivity.this,arr_1);
        mRv_environ.setAdapter(adapter);
    }

    /**
     * Adapter线
     */
    private class MyDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(1, 0, 0, 0);
        }
    }

    /**
     * 销毁时时事件
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: "+"结束");
        super.onDestroy();
        if (t_1 != null && t_1.isAlive()){
            t_1.interrupt();
            run = false;
            values.clear();
            db.close();
            Log.d("线程", "Destroy");
        }
        if (t_2 != null && t_2.isAlive()){
            t_2.interrupt();
            run = false;
            values.clear();
            db.close();
            Log.d("线程", "Destroy");
        }
    }

    /**
     * 停止时事件
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (t_1 != null && t_1.isAlive()){
            t_1.interrupt();
            run = false;
            Log.d("线程", "stop");
        }
        if (t_2 != null && t_2.isAlive()){
            t_2.interrupt();
            run = false;
            Log.d("线程", "stop");
        }
    }

    /**
     * 获取时间
     * @return
     */
    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = format.format(date);
        return str;
    }
}
