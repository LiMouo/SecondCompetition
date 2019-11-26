package limou.com.ThresholdsCatalog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import limou.com.secondcompetition.R;

public class ThresholdsAdapter_2 extends RecyclerView.Adapter<ThresholdsAdapter_2.ThresholdsViewHolder> {
    private Context mContext;
    private ThresholdsViewHolder holder;
    private String[] arr_1 ={"温度:","湿度:","光照:","CO₂:","PM2.5:","道路状态:"};
    private String[] arr_2 = new String[6];
    private String[] arr_4 = new String[6];
    private String[] arr_3 ={"℃","hPa","Lux","mg/m3","μg/m3","P"};
    private OnItemClickListener listener;
    private boolean toggleData = false;
    private EditText editText;
    private String data;
    private String TAG = "ThresholdsAdapter_2";
    private List<EditText> editTextList;

    public ThresholdsAdapter_2(Context mContext, OnItemClickListener listener, String[] arr_2) {
        this.mContext = mContext;
        this.listener = listener;
        this.arr_2 = arr_2;
        this.toggleData = toggleData;
    }

    @NonNull
    @Override
    public ThresholdsAdapter_2.ThresholdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        holder = new ThresholdsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.title_thresholds_2,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ThresholdsViewHolder holder, final int position) {

        editTextList = new ArrayList<>();
        holder.tv_Thresholds_text.setText(arr_1[position]);
        holder.tv_Thresholds_num.setText(arr_3[position]);
        holder.ed_Thresholds_user.setText(arr_2[position]);
        holder.ed_Thresholds_user.setEnabled(toggleData);
        holder.ed_Thresholds_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(position,v);
            }
        });
//        if (toggleData == true){
//            holder.ed_Thresholds_user.setText(arr_2[position]);
//            holder.ed_Thresholds_user.setFocusable(false);
//            holder.ed_Thresholds_user.setFocusableInTouchMode(false);
//            holder.ed_Thresholds_user.requestFocus();
//        }
    }

    public void setEnable(boolean enable){
        toggleData = enable;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arr_1.length;
    }

    class ThresholdsViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_Thresholds_text,tv_Thresholds_num;
        private EditText ed_Thresholds_user;
        public ThresholdsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_Thresholds_text = itemView.findViewById(R.id.tv_Thresholds_text_2);
            tv_Thresholds_num = itemView.findViewById(R.id.tv_Thresholds_num_2);
            ed_Thresholds_user = itemView.findViewById(R.id.ed_Thresholds_user_2);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position,View v);
    }

    public interface OnToggle{
        void OnToggle(int position,View v);
    }
}
