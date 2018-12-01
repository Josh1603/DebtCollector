package syd.jjj.debtcollector;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public abstract class SwipeableDialogFragments extends AppCompatDialogFragment {

    private final DialogFragmentPagerAdapter.Callback adapterCallback =
            new DialogFragmentPagerAdapter.Callback() {

        @Override
        public View getPageView(int position, ViewGroup parent) {
            return SwipeableDialogFragments.this.getPageView(inflater, position, parent);
        }

        @Override
        public int getPageCount() {
            return SwipeableDialogFragments.this.getPageCount();
        }
            };

    private final DialogFragmentPager.OnOutsideTouchListener outsideTouchListener =
            new DialogFragmentPager.OnOutsideTouchListener() {
        @Override
                public void onOutsideTouch(int currentPosition) {
            dismiss();
            SwipeableDialogFragments.this.onOutsideTouchDismissed(currentPosition);
        }
            };

    private final ViewPager.OnPageChangeListener pageChangeListener =
            new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    SwipeableDialogFragments.this.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    SwipeableDialogFragments.this.onPageSelected(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    SwipeableDialogFragments.this.onPageScrollStateChanged(state);
                }
            };

    private DialogFragmentPagerAdapter adapter;
    private LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.Dialog_Pager);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                |WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return inflater.inflate(R.layout.dialog_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        final DialogFragmentPager viewPager = view.findViewById(R.id.view_pager);
        adapter = new DialogFragmentPagerAdapter(getContext(), adapterCallback);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
        viewPager.setOnOutsideTouchListener(outsideTouchListener);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AppCompatDialog dialog = (AppCompatDialog) super.onCreateDialog(savedInstanceState);

        final Window window = dialog.getWindow();
        if(window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            }
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        return dialog;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adapter.onConfigurationChanged();
    }

    protected abstract View getPageView(@NonNull LayoutInflater inflater, int position, @NonNull ViewGroup parent);

    protected abstract int getPageCount();

    protected void onPageSelected(int position) {}

    protected void onPageScrollStateChanged(int state) {}

    protected void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    protected void onOutsideTouchDismissed(int currentPosition) {}
}
