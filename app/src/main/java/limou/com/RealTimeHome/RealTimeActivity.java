package limou.com.RealTimeHome;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import limou.com.SQLiteCatalog.SQLiteMaster;
import limou.com.ToolsHome.SecondTitleTools;
import limou.com.secondcompetition.R;

public class RealTimeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    private static String[] names = {"温度","湿度","光照","CO2","pm2.5","道路状态"};
    private static int[] radioButtonsId = {R.id.rb1,R.id.rb2,R.id.rb3,R.id.rb4,R.id.rb5,R.id.rb6};
    private SQLiteDatabase db;
    private List<Integer>[] Data = new ArrayList[6]; //存放六个图表的数据
    private List<Integer>[] tempData = new ArrayList[6];
    private Thread QueryThread,BindThread;
    private List<String> temp_time = new ArrayList<>();
    private List<String> times = new ArrayList<>();
    private int position;
    private RadioGroup RG_main;
    private ViewPager VP_main;
    private RadioButton[] radioButton = new RadioButton[6];
    private List<View> ViewPages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        InitData();
        BindData();
    }

    private void BindData() {
        VP_main.setAdapter(new RealTimeAdapter(this,ViewPages));
        VP_main.addOnPageChangeListener(this); //添加一个侦听器，该侦听器将在页面更改或逐步滚动时被调用
        RG_main.setOnCheckedChangeListener(this);//设置RadioGroup的监听，要在设置选中以前设置监听
        radioButton[position].setChecked(true);//根据上一页面的点击项选中对应的radio
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

        //拿到上一页面点击的选项卡位置
        position = getIntent().getExtras().getInt("position");
        RG_main = findViewById(R.id.RG_main); //按钮控件
        VP_main = findViewById(R.id.VP_main); //滑动控件
        for (int i = 0;i<radioButton.length;i++){
            radioButton[i] = findViewById(radioButtonsId[i]);
        }
        for (int i = 0;i<6;i++){
            ViewPages.add(LayoutInflater.from(this).inflate(R.layout.realtime_mp,null));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    private void SetData(final int position){
        if (BindThread != null && BindThread.isAlive()){//用于刷新上一个图标的线程如果还活着，就发送一个 InterruptedException
            BindThread.interrupt(); //中断这个线程
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LineChart lineChart = ViewPages.get(position).findViewById(R.id.LineChart);//页面已经变了，获得当前页面的lineChart
        TextView title = ViewPages.get(position).findViewById(R.id.T_title);//获得当前页面的Title
        title.setText(names[position]);//设置当前页面Title
        lineChart.invalidate();//使其隐藏消失

        BindThread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Entry> entries =new ArrayList<>(); //存放坐标点
                while (true){
                    try {
                        long startTime = System.currentTimeMillis();
                        entries.clear(); //清空上一次绘制的坐标点
                        for (int i = 0;i<Data[position].size();i++){
                            entries.add(new Entry(i,Data[position].get(i)));
                            /*原本有数据则只更新数据，不重新创建视图，减少渲染时间*/
                        }
                    }
                }
            }
        });
        BindThread.start();
    }
}
