package limou.com.TripHome;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import limou.com.ToolsHome.SecondTitleTools;
import limou.com.secondcompetition.R;

public class TripActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView trip_date;
    private String TAG = "TripActivity";
    private int[] toggleButtonsId = {R.id.text_red, R.id.text_yellow, R.id.text_green};
    private List<ToggleButton> listToggleButton;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        InitSwitch();
        setAnimation(new int[]{R.id.light_red, R.id.light_yellow, R.id.light_greed});//设置补间动画
        setToggleButtons();
    }

    private void setToggleButtons() {
        listToggleButton = new ArrayList<>();
        listToggleButton.add((ToggleButton) findViewById(R.id.text_red));
        listToggleButton.add((ToggleButton) findViewById(R.id.text_yellow));
        listToggleButton.add((ToggleButton) findViewById(R.id.text_green));
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    for (int i=0;i<3;i++){
                        final int finalI = i;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                setToggle(finalI);
                            }
                        });
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        }
        }).start();
    }

    private void setToggle(int finalI) {
        switch (finalI) {
            case 0:
                listToggleButton.get(0).setChecked(true);
                listToggleButton.get(1).setChecked(false);
                listToggleButton.get(2).setChecked(false);
                break;
            case 1:
                listToggleButton.get(0).setChecked(false);
                listToggleButton.get(1).setChecked(true);
                listToggleButton.get(2).setChecked(false);
                break;
            case 2:
                listToggleButton.get(0).setChecked(false);
                listToggleButton.get(1).setChecked(false);
                listToggleButton.get(2).setChecked(true);
                break;
        }
    }


    private void setAnimation(int[] id) {
        for (int i = 0; i < id.length; i++) {
            AnimationDrawable animationDrawable = (AnimationDrawable) findViewById(id[i]).getBackground();
            animationDrawable.setExitFadeDuration(1000);
            animationDrawable.start();
        }
    }


    private void InitView() {
        setContentView(R.layout.activity_trip);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SecondTitleTools.setTitle("出行管理");
        SecondTitleTools.MenuCreate();

        trip_date = findViewById(R.id.trip_date);
        trip_date.setOnClickListener(this);

    }

    /**
     * 点击时间事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trip_date:
                Calendar calendar = Calendar.getInstance();//得到系统时间
                int mYear = calendar.get(Calendar.YEAR);//年
                int mMonth = calendar.get(Calendar.MONTH);//月
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);//天

                //点击时间，弹出窗口。进行设置，进行更改渲染
                DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.d(TAG, "onDateSet: ");
                        final String date = year + "年" + month + "月" + dayOfMonth + "日";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                trip_date.setText(date);
                            }
                        });
                    }
                }, mYear, mMonth, mDay);
                pickerDialog.show();
                break;
        }
    }

    /**
     * Switch
     */
    private void InitSwitch() {
        final Switch mSwitch = findViewById(R.id.s_one);
        mSwitch.setChecked(false);
        mSwitch.setSwitchTextAppearance(TripActivity.this, R.style.s_false);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSwitch.setSwitchTextAppearance(TripActivity.this, R.style.s_true);
                } else {
                    mSwitch.setSwitchTextAppearance(TripActivity.this, R.style.s_false);
                }
            }
        });
    }
}
