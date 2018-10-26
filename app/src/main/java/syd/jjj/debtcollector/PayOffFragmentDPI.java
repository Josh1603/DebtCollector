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

public class PayOffFragmentDPI extends DialogFragment {

    String uIDollarCent;
    DPIDialogFragmentInterface dataPass;
    EditText dollarCentView;


    /**
     * Assigns references to the EditText views when the the fragment is created.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dpi_pay_off_dialog_fragment, container);
        dollarCentView = v.findViewById(R.id.dollar_cent);
        dollarCentView.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5, 2)});

        //Automatically displays the soft input keyboard.
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return v;
    }

    /**
     * Allows the DPI interface to interact with the activity once the fragment is attached.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dataPass = (DPIDialogFragmentInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DPIDialogFragmentInterface");

        }
    }

    /**
     * Adds the button views and assigns listeners to the fragment once the view is created.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button payOffButton = view.findViewById(R.id.payOffButton);
        payOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uIDollarCent = dollarCentView.getText().toString();
                dataPass.PayOffDebt(uIDollarCent);
                getDialog().dismiss();
            }
        });

        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }
}
