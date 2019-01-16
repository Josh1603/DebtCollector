package syd.jjj.debtcollector;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import java.util.Date;
import java.util.List;

public class DebtValueDatabaseAccessor {

    private static DebtValueDatabase debtValueDatabaseInstance;
    private static final String DEBT_VALUE_DB_NAME = "debtvalue_db";

    private DebtValueDatabaseAccessor() {}

    public static DebtValueDatabase getInstance(Context context) {
        if (debtValueDatabaseInstance == null) {
            // Create or open a new SQLite database, and return it as a Room Database instance.
            debtValueDatabaseInstance = Room
                    .databaseBuilder(context, DebtValueDatabase.class, DEBT_VALUE_DB_NAME).build();
        }
        return debtValueDatabaseInstance;
    }

    public void insertDebtValues(final List<DebtValue> debtValues) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                debtValueDatabaseInstance.debtValueDAO().insertDebtValues(debtValues);
                return null;
            }
        }.execute();
    }

    public void insertDebtValue(final DebtValue debtValue) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                debtValueDatabaseInstance.debtValueDAO().insertDebtValue(debtValue);
                return null;
            }
        }.execute();
    }

    public void deleteDebtValue(final DebtValue debtValue) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                debtValueDatabaseInstance.debtValueDAO().deleteDebtValue(debtValue);
                return null;
            }
        }.execute();
    }

    public LiveData<List<DebtValue>> allDebtValues() {
        return debtValueDatabaseInstance.debtValueDAO().allDebtValues();
    }

    public LiveData<DebtValue> getDebtValue(Date date) {
        return debtValueDatabaseInstance.debtValueDAO().getDebtValue(date);
    }
}
