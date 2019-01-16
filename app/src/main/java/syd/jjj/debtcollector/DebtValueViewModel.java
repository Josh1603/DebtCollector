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

    private LiveData<List<DebtValue>> debtValues;

    public DebtValueViewModel(Application application) {
        super(application);
    }

    public LiveData<List<DebtValue>> getDebtValues() {
        if(debtValues == null) {
            // Load debt values from the database.
            debtValues =
                    DebtValueDatabaseAccessor
                            .getInstance(getApplication())
                            .debtValueDAO()
                            .allDebtValues();
        }
        return debtValues;
    }


}