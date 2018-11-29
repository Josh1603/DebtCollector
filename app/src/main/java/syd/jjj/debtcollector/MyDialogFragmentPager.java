package syd.jjj.debtcollector;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MyDialogFragmentPager extends DialogFragmentPager {

    @Override
    protected View getPageView (@NonNull LayoutInflater inflater, int position, @NonNull ViewGroup parent) {
        final View v = inflater.inflate(R.layout.page, parent, false);

        final TextView title = v.findViewById(R.id.title);
        title.setText("Page " + position);

        return v;
    }

    @Override
    protected int getPageCount() {
        return 5;
    }

    @Override
    protected void onPageSelected(int position) {
        Toast.makeText(getContext(), "Page " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onOutsideTouchDismissed(int currentPosition) {
        Toast.makeText(getContext(), "Outside touch dismissed on position " + currentPosition,
                Toast.LENGTH_SHORT).show();
    }
}
