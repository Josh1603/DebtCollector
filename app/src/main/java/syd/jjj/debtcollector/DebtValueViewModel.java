package syd.jjj.debtcollector;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DebtValueViewModel extends AndroidViewModel {
    private static final String TAG = "DebtValueUpdate";

    private MutableLiveData<List<float[]>> debtValues;

    public DebtValueViewModel(Application application) {
        super(application);
    }

    public LiveData<List<float[]>> getData(String period) {
        switch (period) {
            case "WEEKLY":
                return getWeeklyData();
            case "MONTHLY":
                return getMonthlyData();
            case "YEARLY":
                return getYearlyData();
        }
        return null;
    }

    public LiveData<List<float[]>> getWeeklyData() {
        if (debtValues == null) {
            debtValues = new MutableLiveData<List<float[]>>();
        }
            loadWeeklyDebtValues();
        return debtValues;
    }

    public LiveData<List<float[]>> getMonthlyData() {
        if (debtValues == null) {
            debtValues = new MutableLiveData<List<float[]>>();
        }
            loadMonthlyDebtValues();
        return debtValues;
    }

    public LiveData<List<float[]>> getYearlyData() {
        if (debtValues == null) {
            debtValues = new MutableLiveData<List<float[]>>();
        }
            loadYearlyDebtValues();
        return debtValues;
    }

    public void loadWeeklyDebtValues(){
        new AsyncTask<Void, Void, List<float[]>>() {
            @Override
            protected List<float[]> doInBackground(Void... voids) {
                ArrayList<float[]> datasets = new ArrayList<>();
                DebtValueDatabase debtValueDatabase = DebtValueDatabaseAccessor.getInstance(getApplication());
                Date currentDate = new Date();
                Date startOfWeek = getStartOfWeek(currentDate);
                Date endOfWeek = getEndOfCurrentWeek(currentDate);
                Date startOfPreviousWeek = getStartOfPreviousWeek(startOfWeek);
                Date endOfPreviousWeek = getEndOfPreviousWeek(startOfWeek);
                Date startOfFollowingWeek = getStartOfFollowingWeek(startOfWeek);
                Date endOfFollowingWeek = getEndOfFollowingWeek(startOfWeek);

                List<DebtValue> debtValueListCurrent =
                        debtValueDatabase
                                .debtValueDAO()
                                .getDebtValuesBetween(
                                        startOfWeek,
                                        currentDate);

                List<DebtValue> debtValueListPrior =
                        debtValueDatabase
                                .debtValueDAO()
                                .getDebtValuesBetween(
                                        startOfPreviousWeek,
                                        endOfPreviousWeek);

                List<DebtValue> debtValueListFollowing =
                        debtValueDatabase
                                .debtValueDAO()
                                .getDebtValuesBetween(
                                        startOfFollowingWeek,
                                        endOfFollowingWeek);

                float[] dataset = convertDebtValueListToFloatArray(
                        debtValueListPrior,
                        debtValueListCurrent,
                        debtValueListFollowing,
                        startOfWeek.getTime(),
                        endOfWeek.getTime());
                if (dataset != null) {
                    datasets.add(dataset);
                }

                Date startOfNewCurrentWeek = getStartOfPreviousWeek(startOfWeek);
                Date endOfNewCurrentWeek = getEndOfPreviousWeek(startOfWeek);


                Date firstUseDate = debtValueDatabase.debtValueDAO().getEarliestDate();

                if (firstUseDate == null) {
                    firstUseDate = currentDate;
                }

                while (debtValueDatabase.debtValueDAO().getDebtValuesBetween(
                        firstUseDate,
                        endOfNewCurrentWeek) != null
                        && endOfNewCurrentWeek.getTime() > firstUseDate.getTime()) {
                    List<DebtValue> debtValueListNewCurrent =
                            debtValueDatabase
                                    .debtValueDAO()
                                    .getDebtValuesBetween(
                                            startOfNewCurrentWeek,
                                            endOfNewCurrentWeek);

                    List<DebtValue> debtValueListNewPrior =
                            debtValueDatabase
                                    .debtValueDAO()
                                    .getDebtValuesBetween(
                                            getStartOfPreviousWeek(startOfNewCurrentWeek),
                                            getEndOfPreviousWeek(startOfNewCurrentWeek));

                    List<DebtValue> debtValueListNewFollowing =
                            debtValueDatabase
                                    .debtValueDAO()
                                    .getDebtValuesBetween(
                                            getStartOfFollowingWeek(startOfNewCurrentWeek),
                                            getEndOfFollowingWeek(startOfNewCurrentWeek));

                    float[] datasetNew = convertDebtValueListToFloatArray(
                            debtValueListNewPrior,
                            debtValueListNewCurrent,
                            debtValueListNewFollowing,
                            startOfNewCurrentWeek.getTime(),
                            endOfNewCurrentWeek.getTime());
                    if (datasetNew != null){
                        datasets.add(datasetNew);
                    }
                    startOfNewCurrentWeek = getStartOfPreviousWeek(startOfNewCurrentWeek);
                    endOfNewCurrentWeek = getEndOfPreviousWeek(startOfNewCurrentWeek);
                }
                return datasets;
            }

            @Override
            protected void onPostExecute(List<float[]> data) {
                debtValues.setValue(data);
            }
        }.execute();
    }
    
    public void loadMonthlyDebtValues(){
        new AsyncTask<Void, Void, List<float[]>>() {
            @Override
            protected List<float[]> doInBackground(Void... voids) {
                ArrayList<float[]> datasets = new ArrayList<>();
                DebtValueDatabase debtValueDatabase = DebtValueDatabaseAccessor.getInstance(getApplication());
                Date currentDate = new Date();
                Date startOfMonth = getStartOfMonth(currentDate);
                Date endOfMonth = getEndOfCurrentMonth(currentDate);
                Date startOfPreviousMonth = getStartOfPreviousMonth(startOfMonth);
                Date endOfPreviousMonth = getEndOfPreviousMonth(startOfMonth);
                Date startOfFollowingMonth = getStartOfFollowingMonth(startOfMonth);
                Date endOfFollowingMonth = getEndOfFollowingMonth(startOfMonth);

                List<DebtValue> debtValueListCurrent =
                        debtValueDatabase
                                .debtValueDAO()
                                .getDebtValuesBetween(
                                        startOfMonth,
                                        currentDate);

                List<DebtValue> debtValueListPrior =
                        debtValueDatabase
                                .debtValueDAO()
                                .getDebtValuesBetween(
                                        startOfPreviousMonth,
                                        endOfPreviousMonth);

                List<DebtValue> debtValueListFollowing =
                        debtValueDatabase
                                .debtValueDAO()
                                .getDebtValuesBetween(
                                        startOfFollowingMonth,
                                        endOfFollowingMonth);

                float[] dataset = convertDebtValueListToFloatArray(
                        debtValueListPrior,
                        debtValueListCurrent,
                        debtValueListFollowing,
                        startOfMonth.getTime(),
                        endOfMonth.getTime());
                if (dataset != null) {
                    datasets.add(dataset);
                }

                Date startOfNewCurrentMonth = getStartOfPreviousMonth(startOfMonth);
                Date endOfNewCurrentMonth = getEndOfPreviousMonth(startOfMonth);

                Date firstUseDate = debtValueDatabase.debtValueDAO().getEarliestDate();

                if (firstUseDate == null) {
                    firstUseDate = currentDate;
                }

                while (debtValueDatabase.debtValueDAO().getDebtValuesBetween(
                                firstUseDate,
                                endOfNewCurrentMonth) != null
                        && endOfNewCurrentMonth.getTime() > firstUseDate.getTime()) {

                    List<DebtValue> debtValueListNewCurrent =
                            debtValueDatabase
                                    .debtValueDAO()
                                    .getDebtValuesBetween(
                                            startOfNewCurrentMonth,
                                            endOfNewCurrentMonth);

                    List<DebtValue> debtValueListNewPrior =
                            debtValueDatabase
                                    .debtValueDAO()
                                    .getDebtValuesBetween(
                                            getStartOfPreviousMonth(startOfNewCurrentMonth),
                                            getEndOfPreviousMonth(startOfNewCurrentMonth));

                    List<DebtValue> debtValueListNewFollowing =
                            debtValueDatabase
                                    .debtValueDAO()
                                    .getDebtValuesBetween(
                                            getStartOfFollowingMonth(startOfNewCurrentMonth),
                                            getEndOfFollowingMonth(startOfNewCurrentMonth));

                    float[] datasetNew = convertDebtValueListToFloatArray(
                            debtValueListNewPrior,
                            debtValueListNewCurrent,
                            debtValueListNewFollowing,
                            startOfNewCurrentMonth.getTime(),
                            endOfNewCurrentMonth.getTime());
                    if (datasetNew != null){
                        datasets.add(datasetNew);
                    }
                    startOfNewCurrentMonth = getStartOfPreviousMonth(startOfNewCurrentMonth);
                    endOfNewCurrentMonth = getEndOfPreviousMonth(startOfNewCurrentMonth);
                }
                return datasets;
            }

            @Override
            protected void onPostExecute(List<float[]> data) {
                debtValues.setValue(data);
            }
        }.execute();
    }

    public void loadYearlyDebtValues(){
        new AsyncTask<Void, Void, List<float[]>>() {
            @Override
            protected List<float[]> doInBackground(Void... voids) {
                ArrayList<float[]> datasets = new ArrayList<>();
                DebtValueDatabase debtValueDatabase = DebtValueDatabaseAccessor.getInstance(getApplication());
                Date currentDate = new Date();
                Date startOfYear = getStartOfYear(currentDate);
                Date endOfYear = getEndOfCurrentYear(currentDate);
                Date startOfPreviousYear = getStartOfPreviousYear(startOfYear);
                Date endOfPreviousYear = getEndOfPreviousYear(startOfYear);
                Date startOfFollowingYear = getStartOfFollowingYear(startOfYear);
                Date endOfFollowingYear = getEndOfFollowingYear(startOfYear);

                List<DebtValue> debtValueListCurrent =
                        debtValueDatabase
                                .debtValueDAO()
                                .getDebtValuesBetween(
                                        startOfYear,
                                        currentDate);

                List<DebtValue> debtValueListPrior =
                        debtValueDatabase
                                .debtValueDAO()
                                .getDebtValuesBetween(
                                        startOfPreviousYear,
                                        endOfPreviousYear);

                List<DebtValue> debtValueListFollowing =
                        debtValueDatabase
                                .debtValueDAO()
                                .getDebtValuesBetween(
                                        startOfFollowingYear,
                                        endOfFollowingYear);

                float[] dataset = convertDebtValueListToFloatArray(
                        debtValueListPrior,
                        debtValueListCurrent,
                        debtValueListFollowing,
                        startOfYear.getTime(),
                        endOfYear.getTime());
                if (dataset != null) {
                    datasets.add(dataset);
                }


                Date startOfNewCurrentYear = getStartOfPreviousYear(startOfYear);
                Date endOfNewCurrentYear = getEndOfPreviousYear(startOfYear);

                Date firstUseDate = debtValueDatabase.debtValueDAO().getEarliestDate();

                if (firstUseDate == null) {
                    firstUseDate = currentDate;
                }
                while (debtValueDatabase.debtValueDAO().getDebtValuesBetween(
                                firstUseDate,
                                endOfNewCurrentYear) != null
                        && endOfNewCurrentYear.getTime() > firstUseDate.getTime()) {

                    List<DebtValue> debtValueListNewCurrent =
                            debtValueDatabase
                                    .debtValueDAO()
                                    .getDebtValuesBetween(
                                            startOfNewCurrentYear,
                                            endOfNewCurrentYear);

                    List<DebtValue> debtValueListNewPrior =
                            debtValueDatabase
                                    .debtValueDAO()
                                    .getDebtValuesBetween(
                                            getStartOfPreviousYear(startOfNewCurrentYear),
                                            getEndOfPreviousYear(startOfNewCurrentYear));

                    List<DebtValue> debtValueListNewFollowing =
                            debtValueDatabase
                                    .debtValueDAO()
                                    .getDebtValuesBetween(
                                            getStartOfFollowingYear(startOfNewCurrentYear),
                                            getEndOfFollowingYear(startOfNewCurrentYear));

                    float[] datasetNew = convertDebtValueListToFloatArray(
                            debtValueListNewPrior,
                            debtValueListNewCurrent,
                            debtValueListNewFollowing,
                            startOfNewCurrentYear.getTime(),
                            endOfNewCurrentYear.getTime());
                    if (datasetNew != null){
                        datasets.add(datasetNew);
                    }
                    startOfNewCurrentYear = getStartOfPreviousYear(startOfNewCurrentYear);
                    endOfNewCurrentYear = getEndOfPreviousYear(startOfNewCurrentYear);
                }
                return datasets;
            }

            @Override
            protected void onPostExecute(List<float[]> data) {
                debtValues.setValue(data);
            }
        }.execute();
    }

    public float[] convertDebtValueListToFloatArray(List<DebtValue> priorDebtValues,
                                                    List<DebtValue> currentDebtValues,
                                                    List<DebtValue> followingDebtValues,
                                                    float startOfPeriod,
                                                    float endOfPeriod) {
        int floatArraySize;
        int incrementer;

        if (currentDebtValues != null) {

            if (followingDebtValues.size() > 0) {
                floatArraySize = (currentDebtValues.size() * 4) + 4;
            } else {
                floatArraySize = (currentDebtValues.size() * 4) + 2;
            }

            float[] debtValueCoordinates = new float[floatArraySize];

            if (priorDebtValues.size() > 0) {
                DebtValue lastPreviousDebtValue = priorDebtValues.get(priorDebtValues.size() - 1);
                DebtValue firstCurrentDebtValue = currentDebtValues.get(0);
                float xPrev = lastPreviousDebtValue.getRawX();
                float xCurr = firstCurrentDebtValue.getRawX();
                float x0 = startOfPeriod;
                float yPrev = lastPreviousDebtValue.getRawY();
                float yCurr = firstCurrentDebtValue.getRawY();
                float y0 = yCurr + (((xCurr - x0) * (yPrev - yCurr)) / (xCurr - xPrev));
                debtValueCoordinates[0] = x0;
                debtValueCoordinates[1] = y0;
                incrementer = 2;

            } else {
                debtValueCoordinates[0] = startOfPeriod;
                debtValueCoordinates[1] = 0;
                incrementer = 2;
            }

            for (DebtValue debtValue : currentDebtValues) {
                debtValueCoordinates[incrementer] = debtValue.getRawX();
                debtValueCoordinates[incrementer + 1] = debtValue.getRawY();
                debtValueCoordinates[incrementer + 2] = debtValue.getRawX();
                debtValueCoordinates[incrementer + 3] = debtValue.getRawY();
                incrementer = incrementer + 4;
            }

            if (followingDebtValues.size() > 0) {
                DebtValue lastCurrentDebtValue = currentDebtValues.get(currentDebtValues.size() - 1);
                DebtValue firstFollowingDebtValue = followingDebtValues.get(0);
                float xCurr = lastCurrentDebtValue.getRawX();
                float xFoll = firstFollowingDebtValue.getRawX();
                float x0 = endOfPeriod;
                float yCurr = lastCurrentDebtValue.getRawY();
                float yFoll = firstFollowingDebtValue.getRawY();
                float y0 = yFoll + (((xFoll - x0) * (yCurr - yFoll)) / (xFoll - xCurr));
                debtValueCoordinates[incrementer] = x0;
                debtValueCoordinates[incrementer + 1] = y0;


            }
            return debtValueCoordinates;
        }
        return null;
    }

    public Date getStartOfWeek(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int difference;
        switch (dayOfWeek) {
            case 1:
                difference = -6;
                break;
            default:
                difference = 2 - dayOfWeek;
                break;
        }
        calendar.add(Calendar.DATE, difference);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Date getStartOfPreviousWeek(Date currentWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeek);
        calendar.add(Calendar.DATE, -7);
        return calendar.getTime();
    }

    public Date getEndOfPreviousWeek(Date currentWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeek);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public Date getStartOfFollowingWeek(Date currentWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeek);
        calendar.add(Calendar.DATE, 7);
        return calendar.getTime();
    }

    public Date getEndOfFollowingWeek(Date currentWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeek);
        calendar.add(Calendar.DATE, 14);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public Date getEndOfCurrentWeek(Date currentDate) {
        Date currentWeek = getStartOfWeek(currentDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeek);
        calendar.add(Calendar.DATE, 7);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public Date getStartOfMonth(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Date getStartOfPreviousMonth(Date currentMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentMonth);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    public Date getEndOfPreviousMonth(Date currentMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentMonth);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public Date getStartOfFollowingMonth(Date currentMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentMonth);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }

    public Date getEndOfFollowingMonth(Date currentMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentMonth);
        calendar.add(Calendar.MONTH, 2);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public Date getEndOfCurrentMonth(Date currentDate) {
        Date startOfMonth = getStartOfMonth(currentDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startOfMonth);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public Date getStartOfYear(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Date getStartOfPreviousYear(Date currentYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentYear);
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
    }

    public Date getEndOfPreviousYear(Date currentYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentYear);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public Date getStartOfFollowingYear(Date currentYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentYear);
        calendar.add(Calendar.YEAR, 1);
        return calendar.getTime();
    }

    public Date getEndOfFollowingYear(Date currentYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentYear);
        calendar.add(Calendar.YEAR, 2);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public Date getEndOfCurrentYear(Date currentDate) {
        Date startOfYear = getStartOfYear(currentDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startOfYear);
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }
}