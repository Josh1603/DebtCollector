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

public class MyDialogFragment extends android.support.v4.app.DialogFragment {

    EditText dollars;
    EditText cents;
    DataPass dataPass;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment, container);
        dollars = v.findViewById(R.id.dollars);
        cents = v.findViewById(R.id.cents);

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
                dataPass.dialogFragmentNewDebtValue(dollars.getText().toString(), cents.getText().toString());
                getDialog().dismiss();
            }
        });

        Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPass.dialogFragmentAddDebt(dollars.getText().toString(), cents.getText().toString());
                getDialog().dismiss();
            }
        });

        Button payOffButton = view.findViewById(R.id.payOffButton);
        payOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPass.dialogFragmentPayOffDebt(dollars.getText().toString(), cents.getText().toString());
                getDialog().dismiss();
            }
        });
    }

    public interface DataPass {
        void dialogFragmentNewDebtValue(String newDollarValue, String newCentValue);

        void dialogFragmentAddDebt(String additionalDollarValue, String additionalCentValue);

        void dialogFragmentPayOffDebt(String minusDollarValue, String minusCentValue);
    }

}
