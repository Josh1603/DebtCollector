package syd.jjj.debtcollector;

import android.animation.LayoutTransition;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MySwipeableDialogFragments extends SwipeableDialogFragments {

    @Override
    protected View getPageView (@NonNull LayoutInflater inflater, int position, @NonNull ViewGroup parent) {

        switch(position) {
            case(0):
                final View v0 = inflater.inflate(R.layout.dci_view, parent, false);
                final TextView titleAdd = v0.findViewById(R.id.title);
                titleAdd.setText(R.string.add_to_debt);
                return v0;
            case(1):
                final View v1 = inflater.inflate(R.layout.dci_view, parent, false);
                final TextView titleSub = v1.findViewById(R.id.title);
                titleSub.setText(R.string.pay_off_debt);
                return v1;
            case(2):
                final View v2 = inflater.inflate(R.layout.dci_view, parent, false);
                final TextView titleReset = v2.findViewById(R.id.title);
                titleReset.setText(R.string.set_new_total);
                return v2;
        }

        return null;
    }

    @Override
    protected int getPageCount() {
        return 3;
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
