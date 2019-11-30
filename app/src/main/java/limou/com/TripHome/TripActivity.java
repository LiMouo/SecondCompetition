package limou.com.TripHome;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import limou.com.secondcompetition.R;

public class TripActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView trip_date;
    private String TAG = "TripActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        InitSwitch();
    }



    private void InitView() {
        setContentView(R.layout.activity_trip);
        trip_date = findViewById(R.id.trip_date);
        trip_date.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                        final String date = year + "年" + month + "月" + dayOfMonth+ "日";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                trip_date.setText(date);
                            }
                        });
                    }
                },mYear, mMonth, mDay);
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
        mSwitch.setSwitchTextAppearance(TripActivity.this,R.style.s_false);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mSwitch.setSwitchTextAppearance(TripActivity.this,R.style.s_true);
                }else {
                    mSwitch.setSwitchTextAppearance(TripActivity.this,R.style.s_false);
                }
            }
        });
    }
}
