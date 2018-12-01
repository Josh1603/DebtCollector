package syd.jjj.debtcollector;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class DialogFragmentPager extends ViewPager {

    private static final int DEFAULT_OFFSCREEN_PAGES = 2;

    public interface OnOutsideTouchListener {
        void onOutsideTouch(int position);
    }

    private OnOutsideTouchListener outsideTouchListener;

    public DialogFragmentPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGES);
    }

    public void setOnOutsideTouchListener(OnOutsideTouchListener outsideTouchListener) {
        this.outsideTouchListener = outsideTouchListener;
    }

    public void addHolderView(View child, int position) {
        child.setTag(R.id.touchy_view_pager_index, position);
        addView(child);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            final int currentItem = getCurrentItem();
            final ViewGroup holder = findHolderByPosition(currentItem);
            if (holder != null) {
                final View pageView = holder.getChildAt(0);
                if (pageView != null) {
                    final int x = (int) event.getX();
                    final int y = (int) event.getY();
                    if (x < pageView.getLeft() || x > pageView.getRight()
                            || y < pageView.getTop() || y > pageView.getBottom()) {
                        outsideTouchListener.onOutsideTouch(currentItem);
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    private ViewGroup findHolderByPosition(int position) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = getChildAt(i);
            final int tag = (int) child.getTag(R.id.touchy_view_pager_index);
            if (tag == position) {
                return (ViewGroup) child;
            }
        }
        return null;
    }

}
