package syd.jjj.debtcollector;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class DecimalPointInputFragment extends DialogFragment {
    String uIDollarCent;
    DPIF2DCA dataPass;
    EditText dollarCentView;


    /**
     * Assigns references to the EditText views when the the fragment is created.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.decimal_point_input_dialog_fragment, container);
        dollarCentView = v.findViewById(R.id.dollar_cent);
        dollarCentView.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5, 2)});

        //Automatically displays the soft input keyboard.
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return v;
    }

    /**
     * Allows the inner interface to interact with the activity once the fragment is attached.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dataPass = (DPIF2DCA) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DataPass");

        }
    }

    /**
     * Adds the button views and assigns listeners to the fragment once the view is created.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button newTotalButton = view.findViewById(R.id.newTotalButton);
        newTotalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uIDollarCent = dollarCentView.getText().toString();
                dataPass.NewDebtValue(uIDollarCent);
                getDialog().dismiss();
            }
        });

        Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uIDollarCent = dollarCentView.getText().toString();
                dataPass.AddDebt(uIDollarCent);
                getDialog().dismiss();
            }
        });

        Button payOffButton = view.findViewById(R.id.payOffButton);
        payOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uIDollarCent = dollarCentView.getText().toString();
                dataPass.PayOffDebt(uIDollarCent);
                getDialog().dismiss();
            }
        });
    }

    /**
     * This inner interface provides the methods which require interaction between the fragment and
     * activity.
     */
    public interface DPIF2DCA {
        void NewDebtValue(String uIDollarCentValue);

        void AddDebt(String uIDollarCentValue);

        void PayOffDebt(String uIDollarCentValue);
    }
}
