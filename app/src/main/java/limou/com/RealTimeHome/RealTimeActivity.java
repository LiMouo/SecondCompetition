package limou.com.RealTimeHome;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import limou.com.SQLiteCatalog.SQLiteMaster;
import limou.com.ToolsHome.SecondTitleTools;
import limou.com.secondcompetition.R;

public class RealTimeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    private static String[] names = {"温度", "湿度", "光照", "CO2", "pm2.5", "道路状态"};
    private static int[] radioButtonsId = {R.id.rb1, R.id.rb2, R.id.rb3, R.id.rb4, R.id.rb5, R.id.rb6};
    private SQLiteDatabase db;
    private List<Integer>[] Data = new ArrayList[6]; //存放六个图表的数据
    private List<Integer>[] tempData = new ArrayList[6];
    private Thread QueryThread, BindThread;
    private List<String> temp_time = new ArrayList<>();
    private List<String> times = new ArrayList<>();
    private int position = 0;
    private RadioGroup RG_main;
    private ViewPager VP_main;
    private RadioButton[] radioButton = new RadioButton[6];
    private List<View> ViewPages = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        InitData();
        BindData();
    }

    private void BindData() {
        VP_main.setAdapter(new RealTimeAdapter(this, ViewPages));
        VP_main.addOnPageChangeListener(this); //添加一个侦听器，该侦听器将在页面更改或逐步滚动时被调用
        RG_main.setOnCheckedChangeListener(this);//设置RadioGroup的监听，要在设置选中以前设置监听
        radioButton[position].setChecked(true);//根据上一页面的点击项选中对应的radio
    }

    private void InitData() {
        db = SQLiteMaster.getInstance(this).getWritableDatabase();
        for (int i = 0; i < Data.length; i++) {
            Data[i] = new ArrayList<>();
            tempData[i] = new ArrayList<>();
        }
        QueryData();//开启线程查询数据后面待用
    }

    private void QueryData() {
        QueryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        long startTime = System.currentTimeMillis();
                        for (int i = 0; i < tempData.length; i++) {
                            tempData[i].clear();
                            temp_time.clear();
                            Cursor cursor = db.query("Environ", null, null, null, null, null, null);
                            if (cursor.moveToFirst()) {
                                do {
                                    for (int j = 1; j < 7; j++) {
                                        tempData[j - 1].add(cursor.getInt(j));
                                        temp_time.add(cursor.getString(7));
                                    }
                                } while (cursor.moveToNext());
                            }
                        }
                        times = temp_time;
                        for (int i = 0; i < Data.length; i++) {
                            Data[i] = tempData[i];
                        }
                        long endTime = System.currentTimeMillis();
                        if (endTime - startTime < 3000) {
                            Thread.sleep(3000 - (endTime - startTime));
                        }
                    } catch (InterruptedException e) {
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
        for (int i = 0; i < radioButton.length; i++) {
            radioButton[i] = findViewById(radioButtonsId[i]);
        }
        for (int i = 0; i < 6; i++) {
            ViewPages.add(LayoutInflater.from(this).inflate(R.layout.realtime_mp, null));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        radioButton[position].setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb1:
                SetData(0);
                VP_main.setCurrentItem(0);
                break;
            case R.id.rb2:
                SetData(1);
                VP_main.setCurrentItem(1);
                break;
            case R.id.rb3:
                SetData(2);
                VP_main.setCurrentItem(2);
                break;
            case R.id.rb4:
                SetData(3);
                VP_main.setCurrentItem(3);
                break;
            case R.id.rb5:
                SetData(4);
                VP_main.setCurrentItem(4);
                break;
            case R.id.rb6:
                SetData(5);
                VP_main.setCurrentItem(5);
                break;
        }
    }

    private void SetData(final int position) {
        if (BindThread != null && BindThread.isAlive()) {//用于刷新上一个图标的线程如果还活着，就发送一个 InterruptedException
            BindThread.interrupt(); //中断这个线程
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        final LineChart lineChart = ViewPages.get(position).findViewById(R.id.LineChart);//页面已经变了，获得当前页面的lineChart
        TextView title = ViewPages.get(position).findViewById(R.id.T_title);//获得当前页面的Title
        title.setText(names[position]);//设置当前页面Title
        lineChart.invalidate();//使其隐藏消失

        BindThread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Entry> entries = new ArrayList<>(); //存放坐标点
                while (true) {
                    try {
                        long startTime = System.currentTimeMillis();
                        entries.clear(); //清空上一次绘制的坐标点
                        for (int i = 0; i < Data[position].size(); i++) {
                            entries.add(new Entry(i, Data[position].get(i)));
                            /*原本有数据则只更新数据，不重新创建视图，减少渲染时间*/
                            if (lineChart.getLineData() != null && lineChart.getLineData().getDataSets().size() > 0) { //!!!!!
                                for (ILineDataSet set : lineChart.getLineData().getDataSets()) {
                                    LineDataSet data = (LineDataSet) set;
                                    data.setValues(entries);
                                }
                            } else {
                                LineDataSet lineDataSet = new LineDataSet(entries, null);
                                LineData lineData = new LineData(lineDataSet);
                                lineDataSet.setColor(Color.parseColor("#8f8f8f"));//线条颜色
                                lineDataSet.setCircleColor(Color.parseColor("#8f8f8f"));//圆点颜色
                                lineDataSet.setDrawCircles(true);//绘制圆点
                                lineDataSet.setDrawCircleHole(false);//将此设置为true可以在每个数据圆上绘制孔
                                lineDataSet.setDrawValues(false);//绘制线条上文字

                                lineChart.setLogEnabled(false);//将此设置为true可启用图表的logcat输出。注意* logcat输出会降低渲染性能。默认值：禁用。 * * @参数已启用
                                lineChart.setData(lineData);//设置数据

                                YAxis yAxisRight = lineChart.getAxisRight();//水平图 yRight 是顶轴
                                YAxis yAxisLeft = lineChart.getAxisLeft();//水平图 yLeft 是底轴

                                XAxis xAxis = lineChart.getXAxis(); //返回代表所有x标签的对象，此方法可用于*获取XAxis对象并进行修改（例如，更改*标签的位置，样式等）
                                xAxis.setDrawAxisLine(false); //绘制轴线，最下面一根 如果是否应绘制沿轴的线，则将其设置为true。
                                xAxis.setDrawGridLines(false); //设置每个点的线 将此设置为true可以绘制该轴的网格线。
                                xAxis.setEnabled(true); //轴线启用
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //设置x轴的显示位置
                                xAxis.setDrawLabels(true);//绘制labels 将此属性设置为true可启用绘制该轴的标签
                                xAxis.setAvoidFirstLastClipping(false); //如果设置为true，则图表将避免图表中的第一个和最后一个标签条目*“截断”图表或屏幕的边缘
                                xAxis.setLabelRotationAngle(90f); //设置绘制X轴标签的角度（以度为单位）
                                xAxis.setAxisLineWidth(2f);//设置绘制X轴标签的角度（以度为单位）

                                //这是个啥？？？
                                lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value) {
                                        if ((int) value < 0 || (int) value >= times.size())
                                            return "";
                                        else return times.get((int) value);
                                    }
                                });
                                yAxisLeft.setAxisMinimum(0);
                                yAxisLeft.setDrawAxisLine(false);//绘制轴线，最下面一根
                                yAxisLeft.setEnabled(true);//轴线启用 如果禁用，则不会绘制此组件的任何内容 *默认值：true
                                yAxisRight.setEnabled(false);
                                Description description = new Description();
                                description.setEnabled(false);//描述启用

                                lineChart.setDescription(description);//为图表设置一个新的Description对象。
                                lineChart.setTouchEnabled(false);//将此设置为false可禁用图表上的所有手势和触摸，*默认值：true
                                lineChart.getLegend().setEnabled(false);
                            }
                            lineChart.getXAxis().setLabelRotationAngle(times.size());//设置X轴的label数量
                            lineChart.getData().notifyDataChanged();//更新坐标数据
                            lineChart.notifyDataSetChanged();//更新图标数据
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    lineChart.invalidate();//使整个视图无效。如果该视图可见
                                }
                            });
                            long endTime = System.currentTimeMillis();
                            if (endTime - startTime < 3000) {
                                Thread.sleep(3000 - (endTime - startTime));
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        BindThread.start();
    }
}
