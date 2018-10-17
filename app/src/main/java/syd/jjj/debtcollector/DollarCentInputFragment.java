package syd.jjj.debtcollector;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class DollarCentInputFragment extends android.support.v4.app.DialogFragment {

    String uIDollars;
    String uICents;
    DataPass dataPass;
    EditText dollarsView;
    EditText centsView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment, container);
        dollarsView = v.findViewById(R.id.dollars);
        centsView = v.findViewById(R.id.cents);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dataPass = (DataPass) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DataPass");

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button newTotalButton = view.findViewById(R.id.newTotalButton);
        newTotalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uIDollars = dollarsView.getText().toString();
                uICents = centsView.getText().toString();
                dataPass.NewDebtValue(uIDollars, uICents);
                getDialog().dismiss();
            }
        });

        Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uIDollars = dollarsView.getText().toString();
                uICents = centsView.getText().toString();
                dataPass.AddDebt(uIDollars, uICents);
                getDialog().dismiss();
            }
        });

        Button payOffButton = view.findViewById(R.id.payOffButton);
        payOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uIDollars = dollarsView.getText().toString();
                uICents = centsView.getText().toString();
                dataPass.PayOffDebt(uIDollars, uICents);
                getDialog().dismiss();
            }
        });
    }

    public interface DataPass {
        void NewDebtValue(String uIDollarValue, String uICentValue);

        void AddDebt(String uIDollarValue, String uICentValue);

        void PayOffDebt(String uIDollarValue, String uICentValue);
    }

}
