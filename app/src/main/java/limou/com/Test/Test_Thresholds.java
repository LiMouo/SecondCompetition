package limou.com.Test;

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
import limou.com.ThresholdsCatalog.ThresholdsAdapter_2;
import limou.com.ToolsHome.SecondTitleTools;
import limou.com.secondcompetition.R;

public class Test_Thresholds extends AppCompatActivity {

    private RecyclerView rv_thresholds;
    private ThresholdsAdapter_2 adapter;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private EditText editText;
    private List<EditText> editTextList;
    private String[] arr_1 = {"temperature","humidity","LightIntensity","co2","pm2.5","Status"};
    private String[] arr_2 = new String[6];
    private String[] arr_3 = new String[6];
    private String TAG = "Test_Thresholds";
    private boolean toggleData; //记录保存按钮状态

    /**
     *  点击事件
     */
    /*private ThresholdsAdapter_2.OnItemClickListener listener = new ThresholdsAdapter_2.OnItemClickListener() {
        @Override
        public void onClick(int position, View v) {
            switch (position){
                case 0:
                    arr_2[0] = String.valueOf(((EditText)v).getText());
                    Log.d(TAG, "arr_2["+0+"] 的值是 "+ (((EditText)v).getText()));
                    editor.putString("temperature",arr_2[0]);
                    editor.apply();
                    break;
                case 1:
                    arr_2[1] = String.valueOf(((EditText)v).getText());
                    Log.d(TAG, "arr_2["+1+"] 的值是 "+ (((EditText)v).getText()));
                    editor.putString("humidity",arr_2[1]);
                    editor.apply();
                    break;
                case 2:
                    arr_2[2] = String.valueOf(((EditText)v).getText());
                    Log.d(TAG, "arr_2["+2+"] 的值是 "+ (((EditText)v).getText()));
                    editor.putString("LightIntensity",arr_2[2]);
                    editor.apply();
                    break;
                case 3:
                    arr_2[3] = String.valueOf(((EditText)v).getText());
                    Log.d(TAG, "arr_2["+3+"] 的值是 "+ (((EditText)v).getText()));
                    editor.putString("co2",arr_2[3]);
                    editor.apply();
                    break;
                case 4:
                    arr_2[4] = String.valueOf(((EditText)v).getText());
                    Log.d(TAG, "arr_2["+4+"] 的值是 "+ (((EditText)v).getText()));
                    editor.putString("pm2.5",arr_2[4]);
                    editor.apply();
                    break;
                case 5:
                    arr_2[5] = String.valueOf(((EditText)v).getText());
                    Log.d(TAG, "arr_2["+5+"] 的值是 "+ (((EditText)v).getText()));
                    editor.putString("Status",arr_2[5]);
                    editor.apply();
                    break;
                default:
                    break;
            }
        }
    };*/

    private ToggleButton toggle;
    private Button bt_test_thresholds_save;
    private Intent startIntent;


    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        InitData();
        InitRecoverData();
        InitToggle();
        setService();
    }


    private void InitView() {
        setContentView(R.layout.activity_test__thresholds);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SecondTitleTools.setTitle("测试 Thresholds");
        SecondTitleTools.MenuCreate();
        rv_thresholds = findViewById(R.id.rv_Thresholds);
        bt_test_thresholds_save = findViewById(R.id.bt_test_Thresholds_save);
    }

    private void InitData() {
        rv_thresholds.addItemDecoration(new MyDecoration());
        rv_thresholds.setLayoutManager(new GridLayoutManager(this,3));
        adapter = new ThresholdsAdapter_2(this, new ThresholdsAdapter_2.OnItemClickListener() {
            @Override
            public void onClick(int position, View v) {
                arr_2[position] = String.valueOf(((EditText)v).getText());
                editor.putString(arr_1[position],arr_2[position]);
                Log.d(TAG, arr_1[position]+"的值是"+arr_2[position]);
                editor.apply();
            }
        }, arr_2);
        rv_thresholds.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        editor = getSharedPreferences("Threshold_Test_2", MODE_PRIVATE).edit();
        preferences = getSharedPreferences("Threshold_Test_2", MODE_PRIVATE);
    }

    //将用户输入的数据进行保存，恢复
    private void InitRecoverData() {
        arr_2[0] = preferences.getString("temperature",arr_2[0]);
        arr_2[1] = preferences.getString("humidity",arr_2[1]);
        arr_2[2] = preferences.getString("LightIntensity",arr_2[2]);
        arr_2[3] = preferences.getString("co2",arr_2[3]);
        arr_2[4] = preferences.getString("pm2.5",arr_2[4]);
        arr_2[5] = preferences.getString("Status",arr_2[5]);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "arr_3["+0+"] = " + arr_3[0]);
        Log.d(TAG, "arr_3["+1+"] = " + arr_3[1]);
        Log.d(TAG, "arr_3["+2+"] = " + arr_3[2]);
        Log.d(TAG, "arr_3["+3+"] = " + arr_3[3]);
        Log.d(TAG, "arr_3["+4+"] = " + arr_3[4]);
        Log.d(TAG, "arr_3["+5+"] = " + arr_3[5]);
    }

    private void InitToggle() {
        toggle = findViewById(R.id.toggle);
        toggle.setChecked(preferences.getBoolean("isThreshold",true));
        bt_test_thresholds_save.setEnabled(preferences.getBoolean("toggleData",false));
        toggleData = preferences.getBoolean("isThreshold",toggleData);

        //ToggleButton 的判断
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    bt_test_thresholds_save.setEnabled(!isChecked);
                    editor.putBoolean("!test",!isChecked);
                    editor.apply();
                    startService(startIntent);
                }else {
                    bt_test_thresholds_save.setEnabled(!isChecked);
                    editor.putBoolean("!Test",!isChecked);
                    editor.apply();
                    stopService(startIntent);//关闭服务
                }
                adapter.setEnable(!isChecked);
            }
        });

        //按钮的判断
            bt_test_thresholds_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle.setChecked(true);
                    toggle.setEnabled(true);
                    adapter.setEnable(false);
                    startService(startIntent);
                }
            });

        /*if (toggleData == false){
            bt_test_thresholds_save.setEnabled(false);
        }else {
            bt_test_thresholds_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle.setChecked(preferences.getBoolean("isThreshold",false));
                    editor.putBoolean("isThreshold",false);
                    editor.apply();
                }
            });*/
        }
        //服务
    private void setService() {
        if (preferences.getBoolean("isThreshold",true)){
            startIntent = new Intent(Test_Thresholds.this, ThresholdsService.class);
            startService(startIntent);
        }
    }
    }
