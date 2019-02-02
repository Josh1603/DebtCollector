package syd.jjj.debtcollector;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GraphRecyclerViewAdapter extends RecyclerView.Adapter<GraphRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<float[]> datasets;
    private final String period;

    public GraphRecyclerViewAdapter(ArrayList<float[]> datasets, String period) {
        this.datasets = datasets;
        this.period = period;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_graph, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (!datasets.isEmpty()) {
            float highestDebtValue = getHighestDebtValue(datasets.get(i));
            float[] graphData = datasets.get(i);
            Date firstDateOfPeriod;
            if (graphData != null) {
                firstDateOfPeriod = new Date((long) graphData[0]);
            } else {
                firstDateOfPeriod = new Date();
            }
            viewHolder.periodGraphView.setXAxisScaleFactor(firstDateOfPeriod, period);
            viewHolder.periodGraphView.setYAxisScaleFactor(highestDebtValue);

            viewHolder.periodGraphView.setDataSet(graphData);

            Calendar cal = Calendar.getInstance();
            cal.setTime(firstDateOfPeriod);
            cal.setMinimalDaysInFirstWeek(7);

            String displayText;

            switch (period) {
                case ("WEEKLY"):
                    displayText = "Week " + cal.get(Calendar.WEEK_OF_YEAR) + " - " + cal.get(Calendar.YEAR);
                    viewHolder.detailsView.setText(displayText);
                    break;
                case ("MONTHLY"):
                    String month = getMonthString(cal.get(Calendar.MONTH));
                    displayText = month + " " + cal.get(Calendar.YEAR);
                    viewHolder.detailsView.setText(displayText);
                    break;
                case ("YEARLY"):
                    displayText = Integer.toString(cal.get(Calendar.YEAR));
                    viewHolder.detailsView.setText(displayText);
                    break;
                default:
                    displayText = "View Adapter 'display text' Error";
                    viewHolder.detailsView.setText(displayText);
                    break;
            }
        } else {
            Date currentDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.setMinimalDaysInFirstWeek(7);

            String displayText;

            switch (period) {
                case ("WEEKLY"):
                    displayText = "Week " + cal.get(Calendar.WEEK_OF_YEAR) + " - " + cal.get(Calendar.YEAR);
                    viewHolder.detailsView.setText(displayText);
                    break;
                case ("MONTHLY"):
                    String month = getMonthString(cal.get(Calendar.MONTH));
                    displayText = month + " " + cal.get(Calendar.YEAR);
                    viewHolder.detailsView.setText(displayText);
                    break;
                case ("YEARLY"):
                    displayText = Integer.toString(cal.get(Calendar.YEAR));
                    viewHolder.detailsView.setText(displayText);
                    break;
                default:
                    displayText = "View Adapter 'display text' Error";
                    viewHolder.detailsView.setText(displayText);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (datasets != null){
            return datasets.size();
        }
        return 1;
    }

    public float getHighestDebtValue(float[] dataset) {
        if (dataset != null) {
            float highestDebtValue = -1;
            for (int i = 0; i < (dataset.length / 2); i++) {
                int position = (2 * i) + 1;
                float debtValue = dataset[position];
                if (debtValue > highestDebtValue){
                    highestDebtValue = debtValue;
                }
            }
            return highestDebtValue;
        }
        return 1;
    }

    public String getMonthString(int month) {
        switch(month) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                return "Error getting month String";
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View parentView;
        public final TextView detailsView;
        public final PeriodGraphView periodGraphView;

        public ViewHolder(View view) {
            super(view);
            parentView = view;
            detailsView = (TextView) view.findViewById(R.id.list_item_graph_details);
            periodGraphView = (PeriodGraphView) view.findViewById(R.id.list_item_graph);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + detailsView.getText() + "'";
        }


    }
}
