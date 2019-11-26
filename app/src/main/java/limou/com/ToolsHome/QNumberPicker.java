package limou.com.ToolsHome;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

public class QNumberPicker extends NumberPicker {
    public QNumberPicker(Context context) {
        super(context);
        setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    public QNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    public QNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    public void addView(View child){
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child,int index,android.view.ViewGroup.LayoutParams params){
        super.addView(child, index, params);
        updateView(child);
    }

    private void updateView(View child) {
        if (child instanceof EditText){
            ((EditText) child).setTextColor(Color.parseColor("#ffffff"));
            ((EditText) child).setTextSize(20);
        }
    }

}
