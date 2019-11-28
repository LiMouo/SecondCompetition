package limou.com.EnvironCatalog;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import limou.com.RealTimeHome.RealTimeActivity;
import limou.com.secondcompetition.R;

public class EnvironAdapter extends RecyclerView.Adapter<EnvironAdapter.EnvironViewHolder> {
    private Context mContext;
    private String[] arr = {"温度", "湿度", "光照", "CQ2","PM2.5","道路状态"};
    private int[] arr_1 ;
    private int arr_2 ;
    private String TAG = "EnvironActivity";
    int o = 0;

    public EnvironAdapter(Context mContext,int[] arr_1) {
        this.mContext = mContext;
        this.arr_1 = arr_1;
        arr_2 = 4;
    }

    @NonNull
    @Override
    public EnvironAdapter.EnvironViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EnvironViewHolder environViewHolder = new EnvironViewHolder(LayoutInflater.from(mContext).inflate(R.layout.title_environ,parent,false));
        return environViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EnvironViewHolder holder, final int position) {
        holder.tv_en.setText(arr[position]);
        holder.tv_en_circle.setText(arr_1[position]+"");
        Log.d(TAG, "arr_1[1]"+arr_1[1]);
        /*if(arr_1[5] >=4 && position == 5){
            holder.line_back.setBackgroundResource(R.drawable.environ_red);
        }

        for (int i = 0;i<arr_2.length;i++){
            if (arr_2[i] < arr_1[i]){
                holder.line_back.setBackgroundResource(R.drawable.environ_red);
            }
        }*/

        if (arr_1[5] > arr_2 && position == 5 ){
            holder.line_back.setBackgroundResource(R.drawable.environ_red);
        }


        holder.tv_en_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点击的是"+position, Toast.LENGTH_SHORT).show();
            }
        });

        holder.line_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RealTimeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position",position);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return arr.length;
    }

    class EnvironViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_en,tv_en_circle;
        private LinearLayout line_back;
        public EnvironViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_en = itemView.findViewById(R.id.tv_en);
            tv_en_circle = itemView.findViewById(R.id.tv_en_circle);
            line_back =itemView.findViewById(R.id.line_back);
        }
    }


}
