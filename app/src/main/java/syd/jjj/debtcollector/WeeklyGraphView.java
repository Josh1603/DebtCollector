package syd.jjj.debtcollector;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class WeeklyGraphView extends View {

    public WeeklyGraphView(Context context) {
        this(context, null);
    }

    public WeeklyGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public WeeklyGraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setFocusable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // The graph is a rectangle that fills as much space as possible.
        // Set the measured dimension by figuring out the shortest boundary, height or width.

        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);

        setMeasuredDimension (measuredWidth, measuredHeight);
    }

    private int measure(int measureSpec) {
        int result = 0;

        // Decode the measurement specifications.
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            // Return a default size of 200 if no bounds are specified.
            result = 200;
        } else {
            // As you want to fill the available space always return the fill available bounds.
            result = specSize;
        }
        return result;
    }
}
