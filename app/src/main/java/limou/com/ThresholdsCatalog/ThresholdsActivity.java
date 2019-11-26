package limou.com.ThresholdsCatalog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import limou.com.MyDecoration.MyDecoration;
import limou.com.ServiceCatalog.ThresholdsService;
import limou.com.ToolsHome.SecondTitleTools;
import limou.com.secondcompetition.R;

public class ThresholdsActivity extends AppCompatActivity {

    private RecyclerView rv_thresholds;
    private ThresholdsAdapter adapter;
    private ToggleButton toggle;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private List<EditText> editTextList;
    private String[] arr_1 = {"temperature","humidity","LightIntensity","co2","pm2.5","Status"};
    private String[] arr_2 = new String[6];
    private int[] arr_3 = new int[6];
    private String TAG = "ThresholdsActivity";
    private Button bt_thresholds_save;
    private boolean toggleData;
    private Intent startIntent ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        InitData();
        listenerToggle();//开关按钮的监听
        InitRecoverData();
        setService();
    }

    private void InitView() {
        setContentView(R.layout.activity_thresholds);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SecondTitleTools.setTitle("阈值设置");
        SecondTitleTools.MenuCreate();
        rv_thresholds = findViewById(R.id.rv_Thresholds);
        toggle = findViewById(R.id.toggle);
        bt_thresholds_save = findViewById(R.id.bt_Thresholds_save);
    }

    private void InitData() {
        startIntent  = new Intent(this, ThresholdsService.class);
        rv_thresholds.addItemDecoration(new MyDecoration());
        rv_thresholds.setLayoutManager(new GridLayoutManager(this,3));
        adapter = new ThresholdsAdapter(this, new ThresholdsAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, View v) {
                arr_2[position] = String.valueOf(((EditText)v).getText());
                editor.putString(arr_1[position],arr_2[position]);
                Log.d(TAG, arr_1[position]+"的值是"+arr_2[position]);
                editor.apply();
            }
        },arr_2);
        rv_thresholds.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        editor = getSharedPreferences("Threshold", MODE_PRIVATE).edit();
        preferences = getSharedPreferences("Threshold", MODE_PRIVATE);
    }

    private void listenerToggle() {
        toggle.setChecked(preferences.getBoolean("isThresholds",true));
        bt_thresholds_save.setEnabled(preferences.getBoolean("isBtSave",false));

        //ToggleButton 的判断
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    bt_thresholds_save.setEnabled(!isChecked);
                    Log.d(TAG, "Activity 服务器开启");
                    /*setService();*/
                    startService(startIntent);
                }else {
                    bt_thresholds_save.setEnabled(!isChecked);
                    Log.d(TAG, "Activity 服务器关闭");
                    stopService(startIntent);
                }
                adapter.setEnable(!isChecked);
            }
        });

        bt_thresholds_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle.setChecked(true);
                toggle.setEnabled(true);
                adapter.setEnable(false);
                startService(startIntent);
            }
        });
    }

    private void setService() {
            startService(startIntent);
            Log.d(TAG, "setService: "+"启动");
    }

    private void InitRecoverData() {
        arr_2[0] = preferences.getString("temperature",arr_2[0]);
        arr_2[1] = preferences.getString("humidity",arr_2[1]);
        arr_2[2] = preferences.getString("LightIntensity",arr_2[2]);
        arr_2[3] = preferences.getString("co2",arr_2[3]);
        arr_2[4] = preferences.getString("pm2.5",arr_2[4]);
        arr_2[5] = preferences.getString("Status",arr_2[5]);
        adapter.notifyDataSetChanged();


        /*arr_3[0] = preferences.getInt("temperature",0);
        arr_3[0] = preferences.getInt("temperature",0);
        arr_3[0] = preferences.getInt("temperature",0);
        arr_3[0] = preferences.getInt("temperature",0);
        arr_3[0] = preferences.getInt("temperature",0);*/
    }
}
