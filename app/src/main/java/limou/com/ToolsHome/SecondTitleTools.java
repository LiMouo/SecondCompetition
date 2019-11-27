package limou.com.ToolsHome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import limou.com.EnvironCatalog.EnvironActivity_2;
import limou.com.MainActivity;
import limou.com.RealTimeHome.RealTimeActivity;
import limou.com.ThresholdsCatalog.ThresholdsActivity;
import limou.com.secondcompetition.R;


public class SecondTitleTools extends LinearLayout {

    private static Context mContext;
    private static Button btn_secondTitle;
    private static Toolbar toolbar;
    private static TextView title;
    private static Spinner spinner;
    private static List<String> list;
    private static boolean network;
    private static String TAG = "EnvironActivity";
    private static Handler handler = new Handler();
    private static Thread t1;

    public SecondTitleTools(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        InitView(context);
    }

    public static boolean InitNetwork(final Context context) {
        spinner.setVisibility(VISIBLE);
        list = new ArrayList<>();
        list.add("网络模式");
        list.add("离线模式");
        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                network = (position == 0 ? true : false);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        return network;
    }

    private  void InitView(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.activity_second_title_tools,this);
        toolbar = findViewById(R.id.second_toolbar);
        toolbar.setNavigationIcon(R.drawable.menu);
        title = findViewById(R.id.Second_Title_Toolbar);
        spinner = findViewById(R.id.sp_Second_Title_Network);
    }

    public static void MenuCreate(){
        toolbar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext,view);
                final MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.title_tools,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent;
                        switch (item.getItemId()){
                            case R.id.menu_main:
                                intent = new Intent(mContext,MainActivity.class);
                                Toast.makeText(mContext,"页面已跳转至 主页面", Toast.LENGTH_SHORT).show();
                                mContext.startActivity(intent);
                                break;
                            case R.id.menu_Environ:
                                intent = new Intent(mContext, EnvironActivity_2.class);
                                Toast.makeText(mContext,"页面已跳转至 环境指标 页面", Toast.LENGTH_SHORT).show();
                                mContext.startActivity(intent);
                                break;
                            case R.id.menu_Thresholds:
                                intent = new Intent(mContext, ThresholdsActivity.class);
                                Toast.makeText(mContext,"页面已跳转至 阈值设置 页面", Toast.LENGTH_SHORT).show();
                                mContext.startActivity(intent);
                                break;
                            case R.id.menu_RealTime:
                                intent = new Intent(mContext, RealTimeActivity.class);
                                mContext.startActivity(intent);
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();//显示
            }
        });
    }

    public static void  setTitle(String T){
        title.setText(T);
    }

    public static void CreateBackButton(){
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) mContext;
                activity.finish();
            }
        });
    }
}
