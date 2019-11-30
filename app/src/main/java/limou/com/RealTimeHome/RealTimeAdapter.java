package limou.com.RealTimeHome;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class RealTimeAdapter extends PagerAdapter {
    private List<View> views;
    private Context context;

    public RealTimeAdapter(Context context, List<View> views) {
        this.context = context;
        this.views = views;
    }

    //返回viewpager页面的个数
    @Override
    public int getCount() {
        return views.size();
    }

    //判断是否是否为同一张图片，这里返回方法中的两个参数做比较就可以
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    //设置viewpage内部东西的方法，如果viewpage内没有子空间滑动产生不了动画效果
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    //因为它默认是看六张图片，第七张图片的时候就会报错，还有就是不要返回父类的作用
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
       container.removeView(views.get(position));
    }


}
