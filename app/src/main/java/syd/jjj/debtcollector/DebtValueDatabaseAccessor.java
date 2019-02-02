package syd.jjj.debtcollector;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;

public class DebtValueDatabaseAccessor {

    private static DebtValueDatabase debtValueDatabaseInstance;
    private static final String DEBT_VALUE_DB_NAME = "debtvalue_db";
    private static List<DebtValue> debtValueList;
    private static List<DebtValue> debtValueSelectedList;
    private static DebtValue debtValue;

    private DebtValueDatabaseAccessor() {}

    public static DebtValueDatabase getInstance(Context context) {
        if (debtValueDatabaseInstance == null) {
            // Create or open a new SQLite database, and return it as a Room Database instance.
            debtValueDatabaseInstance = Room
                    .databaseBuilder(context, DebtValueDatabase.class, DEBT_VALUE_DB_NAME).addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                        }
                    }).build();
            }
        return debtValueDatabaseInstance;
    }

    public static void insertDebtValues(final List<DebtValue> debtValues, Context context) {
        if (debtValueDatabaseInstance == null) {
            debtValueDatabaseInstance = DebtValueDatabaseAccessor.getInstance(context);
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                debtValueDatabaseInstance.debtValueDAO().insertDebtValues(debtValues);
                return null;
            }
        }.execute();
    }

    public static void insertDebtValue(final DebtValue debtValue, Context context) {
        if (debtValueDatabaseInstance == null) {
            debtValueDatabaseInstance = DebtValueDatabaseAccessor.getInstance(context);
        }        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                debtValueDatabaseInstance.debtValueDAO().insertDebtValue(debtValue);
                return null;
            }
        }.execute();
    }

    public static void deleteDebtValue(final DebtValue debtValue, Context context) {
        if (debtValueDatabaseInstance == null) {
            debtValueDatabaseInstance = DebtValueDatabaseAccessor.getInstance(context);
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                debtValueDatabaseInstance.debtValueDAO().deleteDebtValue(debtValue);
                return null;
            }
        }.execute();
    }

    public static List<DebtValue> allDebtValues(Context context) {
        if (debtValueDatabaseInstance == null) {
            debtValueDatabaseInstance = DebtValueDatabaseAccessor.getInstance(context);
        }
        new AsyncTask<Void, Void, List<DebtValue>>() {
            @Override
            protected List<DebtValue> doInBackground(Void... voids) {
                return debtValueDatabaseInstance.debtValueDAO().allDebtValues();
            }

            @Override
            protected void onPostExecute(List<DebtValue> result) {
                debtValueList = result;
            }
        }.execute();
        return debtValueList;
    }

    public static DebtValue getDebtValue(Date date, Context context) {
        if (debtValueDatabaseInstance == null) {
            debtValueDatabaseInstance = DebtValueDatabaseAccessor.getInstance(context);
        }
        new AsyncTask<Date, Void, DebtValue>() {
            @Override
            protected DebtValue doInBackground(Date... dates) {
                return debtValueDatabaseInstance.debtValueDAO().getDebtValue(dates[0]);
            }

            @Override
            protected void onPostExecute(DebtValue result) {
                debtValue = result;
            }
        }.execute(date);
        return debtValue;


    }

    public static List<DebtValue> getDebtValuesBetween(Date dateStart, Date dateEnd, Context context) {
        if (debtValueDatabaseInstance == null) {
            debtValueDatabaseInstance = DebtValueDatabaseAccessor.getInstance(context);
        }
        new AsyncTask<Date, Void, List<DebtValue>>() {

            @Override
            protected List<DebtValue> doInBackground(Date... dates) {
                return debtValueDatabaseInstance.debtValueDAO().getDebtValuesBetween(dates[0], dates[1]);            }

            @Override
            protected void onPostExecute(List<DebtValue> result) {
                debtValueSelectedList = result;
            }
        }.execute(dateStart, dateEnd);
        return debtValueSelectedList;


    }
}
