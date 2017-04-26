package berlin.volders.rxdownload.example;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

// For details follow the link:
// https://github.com/chrisbanes/PhotoView#issues-with-viewgroups
public class HackyViewPager extends ViewPager {

    public HackyViewPager(Context context) {
        super(context);
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            //uncomment if you really want to see these errors
            //e.printStackTrace();
            return false;
        }
    }
}
