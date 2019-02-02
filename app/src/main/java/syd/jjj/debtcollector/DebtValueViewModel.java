package syd.jjj.debtcollector;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import syd.jjj.debtcollector.DebtValue;
import syd.jjj.debtcollector.DebtValueDAO;
import syd.jjj.debtcollector.DebtValueDatabase;
import syd.jjj.debtcollector.DebtValueDatabaseAccessor;

public class DebtValueViewModel extends AndroidViewModel {
    private static final String TAG = "DebtValueUpdate";

    private List<DebtValue> debtValues;

    public DebtValueViewModel(Application application) {
        super(application);
    }

    public List<DebtValue> getDebtValues() {
        if(debtValues == null) {
            // Load debt values from the database.
            //debtValues = DebtValueDatabaseAccessor.allDebtValues();
        }
        return debtValues;
    }


}