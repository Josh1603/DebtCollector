package syd.jjj.debtcollector;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DialogFragmentPagerAdapter extends PagerAdapter {

    public interface Callback {
        View getPageView(int position, ViewGroup parent);
        int getPageCount();
    }

    private final Context context;
    private final Callback callback;

    DialogFragmentPagerAdapter(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        final DialogFragmentPager viewPager = (DialogFragmentPager) container;
        final FrameLayout holder = new FrameLayout(context);
        final View pageView = callback.getPageView(position, container);
        holder.addView(pageView);

        final FrameLayout.LayoutParams pageViewLayoutParams =
                (FrameLayout.LayoutParams) pageView.getLayoutParams();
        pageViewLayoutParams.gravity = Gravity.CENTER;

        viewPager.addHolderView(holder, position);

        final ViewGroup.LayoutParams holderLayoutParams = holder.getLayoutParams();
        holderLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        holderLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

        holder.setFitsSystemWindows(true);

        return holder;
    }

    @Override
    public int getCount() {
        return callback.getPageCount();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    void onConfigurationChanged() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
 }
