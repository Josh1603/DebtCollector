package syd.jjj.debtcollector;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface DebtValueDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertDebtValues(List<DebtValue> debtValues);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertDebtValue(DebtValue debtValue);

    @Delete
    public void deleteDebtValue(DebtValue debtValue);


    // TODO ***CHECK WHETHER THIS WORKS. IF NOT REMOVE DATETIME AND CHANGE TO ORDER BY mDate.***
    @Query("SELECT * FROM debtvalue ORDER BY datetime(mDate) DESC")
    public LiveData<List<DebtValue>> allDebtValues();

    @Query("SELECT * FROM debtvalue WHERE mDate =:date")
    LiveData<DebtValue> getDebtValue(Date date);
}

