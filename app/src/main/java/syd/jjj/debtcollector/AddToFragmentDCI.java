package syd.jjj.debtcollector;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class AddToFragmentDCI extends DialogFragment {

    String uIDollars;
    String uICents;
    DCIDialogFragmentInterface dataPass;
    EditText dollarsView;
    EditText centsView;


    /**
     * Assigns references to the EditText views when the the fragment is created.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dci_add_to_dialog_fragment, container);
        dollarsView = v.findViewById(R.id.dollars);
        centsView = v.findViewById(R.id.cents);

        //Automatically displays the soft input keyboard.
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return v;
    }

    /**
     * Allows the DCI interface to interact with the activity once the fragment is attached.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dataPass = (DCIDialogFragmentInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DCIDialogFragmentInterface");

        }
    }

    /**
     * Adds the button views and assigns listeners to the fragment once the view is created.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }
}
