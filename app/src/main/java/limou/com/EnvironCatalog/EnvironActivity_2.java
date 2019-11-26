package limou.com.EnvironCatalog;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import limou.com.MyDecoration.MyDecoration;
import limou.com.SQLiteCatalog.SQLiteMaster;
import limou.com.ToolsHome.SecondTitleTools;
import limou.com.secondcompetition.R;

public class EnvironActivity_2 extends AppCompatActivity {

    private EnvironAdapter adapter;
    private RecyclerView mRv_environ;
    private int[] arr_1 = new int[6];
    private SQLiteDatabase DB;
    private Thread thread;
    private Intent intent;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        InitService();
        InitData();
    }

    private void InitService() {
        intent = new Intent(this, EnvironService.class);
        startService(intent);
    }

    private void InitData() {
        DB = SQLiteMaster.getInstance(this).getWritableDatabase();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Cursor cursor = DB.query("Environ", null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                            arr_1[0] = cursor.getInt(cursor.getColumnIndex("pm25"));
                            arr_1[1] = cursor.getInt(cursor.getColumnIndex("co2"));
                            arr_1[2] = cursor.getInt(cursor.getColumnIndex("LightIntensity"));
                            arr_1[3] = cursor.getInt(cursor.getColumnIndex("humidity"));
                            arr_1[4] = cursor.getInt(cursor.getColumnIndex("temperature"));
                            arr_1[5] = cursor.getInt(cursor.getColumnIndex("Status"));
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        thread.start();
    }

    private void InitView() {
        setContentView(R.layout.activity_environ);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SecondTitleTools.setTitle("环境指标");
        SecondTitleTools.MenuCreate();

        mRv_environ = findViewById(R.id.rv_Environ);
        mRv_environ.setLayoutManager(new GridLayoutManager(this, 3));
        mRv_environ.addItemDecoration(new MyDecoration());
        adapter = new EnvironAdapter(EnvironActivity_2.this, arr_1);
        mRv_environ.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        if (thread.isAlive()) {
            thread.interrupt();
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
