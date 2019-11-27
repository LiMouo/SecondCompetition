package limou.com.RealTimeHome;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import limou.com.SQLiteCatalog.SQLiteMaster;
import limou.com.ToolsHome.SecondTitleTools;
import limou.com.secondcompetition.R;

public class RealTimeActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private List<Integer>[] Data = new ArrayList[6]; //存放六个图表的数据
    private List<Integer>[] tempData = new ArrayList[6];
    private Thread QueryThread;
    private List<String> temp_time = new ArrayList<>();
    private List<String> times = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        InitData();
    }

    private void InitData() {
        db = SQLiteMaster.getInstance(this).getWritableDatabase();
        for (int i = 0;i<Data.length;i++){
            Data[i] = new ArrayList<>();
            tempData[i] = new ArrayList<>();
        }
        QueryData();//开启线程查询数据后面待用
    }

    private void QueryData() {
        QueryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        long startTime = System.currentTimeMillis();
                        for (int i = 0;i<tempData.length;i++){
                            tempData[i].clear();
                            temp_time.clear();
                            Cursor cursor = db.query("Environ",null,null,null,null,null,null);
                            if (cursor.moveToFirst()){
                                do {
                                    for (int j = 1;j<7;j++){
                                        tempData[j-1].add(cursor.getInt(j));
                                        temp_time.add(cursor.getString(7));
                                    }
                                }while (cursor.moveToNext());
                            }
                        }
                        times = temp_time;
                        for (int i = 0;i<Data.length;i++){
                            Data[i] = tempData[i];
                        }
                        long endTime = System.currentTimeMillis();
                        if (endTime - startTime < 3000){
                            Thread.sleep(3000 - (endTime - startTime));
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        QueryThread.start();
    }

    private void InitView() {
        setContentView(R.layout.activity_real_time);
        SecondTitleTools.MenuCreate();
        SecondTitleTools.setTitle("实时显示");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
