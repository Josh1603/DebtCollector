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


    private DebtValueDatabaseAccessor() {}

    public static DebtValueDatabase getInstance(Context context) {
        if (debtValueDatabaseInstance == null) {
            // Create or open a new SQLite database, and return it as a Room Database instance.
            debtValueDatabaseInstance = Room
                    .databaseBuilder(context, DebtValueDatabase.class, DEBT_VALUE_DB_NAME).build();
            }
        return debtValueDatabaseInstance;
    }
}
