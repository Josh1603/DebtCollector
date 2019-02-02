package syd.jjj.debtcollector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * The main activity for this app. Current debt values are stored as shared preferences as a form of
 * basic data persistence. Calculations are delegated to the DebtCalculation class.
 */
public class DebtCollectorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DPIDialogFragmentInterface, DCIDialogFragmentInterface {

    private TextView currentDebtValue;
    private DebtCalculations debtCalculations;

    private SharedPreferences sharedPreferences;
    private String sharedPreferenceName = "DebtCollectorSharedPreferences";

    private final String CURRENT_DOLLAR_TOTAL_KEY = "current_dollar_total";
    private final String CURRENT_CENT_TOTAL_KEY = "current_cent_total";

    private final String CURRENT_PERIOD_KEY = "current_period_key";
    private final String FIRST_USE_DATE = "first_use_date_key";

    private static final String TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT";

    private boolean decimalPointIncluded;

    private DebtValue currentDebtValueObj;

    private PeriodGraphView periodGraphView;

    private RecyclerView mRecyclerView;

    private float startOfPeriod;
    private float endOfPeriod;
    private Date currentDate;


    /**
     * Displays the current debt value and provides ImageButtons which open fragments to modify the
     * debt value, or in the case of the undo button, immediately undoes the last action.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initialiseTheme();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setCurrentPeriodButton();

        currentDebtValue = findViewById(R.id.current_debt_value);
        displayCurrentDebt();
        setButtonListeners();

        /*
        dummyGraphDataSet = new DummyGraphDataSet();

        mDummyGraphDataSets = new ArrayList<>(0);
        DummyGraphDataSet dummyGraphDataSet1 = new DummyGraphDataSet();
        DummyGraphDataSet dummyGraphDataSet2 = new DummyGraphDataSet();
        DummyGraphDataSet dummyGraphDataSet3 = new DummyGraphDataSet();
        DummyGraphDataSet dummyGraphDataSet4 = new DummyGraphDataSet();
        DummyGraphDataSet dummyGraphDataSet5 = new DummyGraphDataSet();
        DummyGraphDataSet dummyGraphDataSet6 = new DummyGraphDataSet();

        mDummyGraphDataSets.add(dummyGraphDataSet1);
        mGraphRecyclerViewAdapter.notifyItemInserted(mDummyGraphDataSets.indexOf(dummyGraphDataSet1));
        mDummyGraphDataSets.add(dummyGraphDataSet2);
        mGraphRecyclerViewAdapter.notifyItemInserted(mDummyGraphDataSets.indexOf(dummyGraphDataSet2));
        mDummyGraphDataSets.add(dummyGraphDataSet3);
        mGraphRecyclerViewAdapter.notifyItemInserted(mDummyGraphDataSets.indexOf(dummyGraphDataSet3));
        mDummyGraphDataSets.add(dummyGraphDataSet4);
        mGraphRecyclerViewAdapter.notifyItemInserted(mDummyGraphDataSets.indexOf(dummyGraphDataSet4));
        mDummyGraphDataSets.add(dummyGraphDataSet5);
        mGraphRecyclerViewAdapter.notifyItemInserted(mDummyGraphDataSets.indexOf(dummyGraphDataSet5));
        mDummyGraphDataSets.add(dummyGraphDataSet6);
        mGraphRecyclerViewAdapter.notifyItemInserted(mDummyGraphDataSets.indexOf(dummyGraphDataSet6));

        mRecyclerView.setAdapter(new GraphRecyclerViewAdapter(mDummyGraphDataSets));

        */

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = findViewById(R.id.graph_list);
        //mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        loadDataFromRoom();
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        if (id == R.id.nav_add_to) {
            if (decimalPointIncluded) {
                FragmentManager fm = getSupportFragmentManager();
                AddToFragmentDCI addFragDCI = new AddToFragmentDCI();
                addFragDCI.show(fm, "ui_add_to_DCI_fragment");
            } else {
                FragmentManager fm = getSupportFragmentManager();
                AddToFragmentDPI addFragDPI = new AddToFragmentDPI();
                addFragDPI.show(fm, "ui_add_to_DPI_fragment");
            }
        }

        if (id == R.id.nav_pay_off) {
            if (decimalPointIncluded) {
                FragmentManager fm = getSupportFragmentManager();
                PayOffFragmentDCI payFragDCI = new PayOffFragmentDCI();
                payFragDCI.show(fm, "ui_pay_off_DCI_fragment");
            } else {
                FragmentManager fm = getSupportFragmentManager();
                PayOffFragmentDPI payFragDPI = new PayOffFragmentDPI();
                payFragDPI.show(fm, "ui_pay_off_DPI_fragment");
            }
        }

        if (id == R.id.nav_set_new) {
            if (decimalPointIncluded) {
                FragmentManager fm = getSupportFragmentManager();
                SetNewFragmentDCI newFragDCI = new SetNewFragmentDCI();
                newFragDCI.show(fm, "ui_set_new_DCI_fragment");
            } else {
                FragmentManager fm = getSupportFragmentManager();
                SetNewFragmentDPI newFragDPI = new SetNewFragmentDPI();
                newFragDPI.show(fm, "ui_set_new_DPI_fragment");
            }
        }

        if(id == R.id.nav_undo) {
            undo();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Called by DCIDialogFragmentInterface. This method gets the new debt value and displays it on
     * the UI.
     */
    public void NewDebtValue(String uIDollars, String uICents) {

        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollars, uICents);
        newDebtValue();
    }

    /**
     * Called by DCIDialogFragmentInterface. This method gets the summed debt value and displays it
     * on the UI.
     */
    public void AddDebt (String uIDollars, String uICents){

        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollars, uICents);
        addDebt();
    }

    /**
     * Called by DCIDialogFragmentInterface. This method gets the paid off debt value and displays
     * it on the UI.
     */
    public void PayOffDebt (String uIDollars, String uICents){

        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollars, uICents);
        payOffDebt();
    }

    /**
     * Called by DPIDialogFragmentInterface. This method gets the new debt value and displays it on
     * the UI.
     */
    public void NewDebtValue(String uIDollarCentValue) {
        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollarCentValue);
        newDebtValue();
    }

    /**
     * Called by DPIDialogFragmentInterface. This method gets the summed debt value and displays it
     * on the UI.
     */
    public void AddDebt(String uIDollarCentValue) {
        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollarCentValue);
        addDebt();
    }

    /**
     * Called by DPIDialogFragmentInterface. This method gets the paid off debt value and displays
     * it on the UI.
     */
    public void PayOffDebt(String uIDollarCentValue) {
        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollarCentValue);
        payOffDebt();
    }

    /**
     * Stores and displays a new debt value.
     */
    public void newDebtValue() {
        debtCalculations.newDebtValue();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        storeData();
        displayCurrentDebt();
    }

    /**
     * Stores and displays a new debt value without storing it to the Room database.
     */
    public void newDebtValueNoStore() {
        debtCalculations.newDebtValue();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        displayCurrentDebt();
    }

    /**
     * Adds UI debt value and displays the new total.
     */
    public void addDebt() {
        debtCalculations.addDebt();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        storeData();
        displayCurrentDebt();
    }

    /**
     * Pays off UI debt value and displays the new total, including a Snackbar message if debt value
     * is completely paid off.
     */
    public void payOffDebt() {
        debtCalculations.payOffDebt();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        storeData();
        displayCurrentDebt();
        if (debtCalculations.isPaidOff() && debtCalculations.getRemainderText().equals("")) {
            Snackbar debtPaidOffSnackbar = Snackbar.make(findViewById(R.id.main_view), "Woohoo! You've paid off all your debt.", Snackbar.LENGTH_LONG);
            centerAlignSnackbarText(debtPaidOffSnackbar);
            debtPaidOffSnackbar.show();
        }

        if (debtCalculations.isPaidOff() && !debtCalculations.getRemainderText().equals("")) {
            Snackbar debtPaidOffSnackbar = Snackbar.make(findViewById(R.id.main_view), "You've paid off your debt and an additional " + debtCalculations.getRemainderText() + "!", Snackbar.LENGTH_LONG);
            centerAlignSnackbarText(debtPaidOffSnackbar);
            debtPaidOffSnackbar.show();
        }
    }

    /**
     * Undoes the previous action.
     */
    public void undo() {
        if (debtCalculations != null) {
            String previousDollarValue = debtCalculations.getPreviousDollars();
            String previousCentValue = debtCalculations.getPreviousCents();
            debtCalculations = new DebtCalculations(
                    getCurrentDollarValue(),
                    getCurrentCentValue(),
                    previousDollarValue,
                    previousCentValue);
            adjustMostRecentDebtValue();
            newDebtValueNoStore();
        }
    }

    /**
     * Stores the current dollar value to shared preferences.
     */
    public void storeCurrentDollarValue() {
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_DOLLAR_TOTAL_KEY, debtCalculations.getCurrentDollars());
        editor.apply();
    }

    /**
     * Stores the current cent value to shared preferences.
     */
    public void storeCurrentCentValue() {
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_CENT_TOTAL_KEY, debtCalculations.getCurrentCents());
        editor.apply();
    }

    /**
     * Stores current debt value to a Room database.
     */
    public void storeData() {
        currentDebtValueObj = new DebtValue(new Date(), getCurrentDollarValue(), getCurrentCentValue());
        DebtValueDatabaseAccessor.insertDebtValue(currentDebtValueObj, this);
    }

    /**
     * Adds or removes most recent debt value from the Room database depending on whether it was an
     * undo or a 'double' undo.
     */
    public void adjustMostRecentDebtValue() {
        if (currentDebtValueObj != null) {
            DebtValueDatabaseAccessor.deleteDebtValue(currentDebtValueObj, this);
            currentDebtValueObj = null;
        } else {
            storeData();
        }
    }

    /**
     * Gets the current dollar value from shared preferences.
     */
    public String getCurrentDollarValue() {
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        return sharedPreferences.getString(CURRENT_DOLLAR_TOTAL_KEY, "0");
    }

    /**
     * Gets the current cent value from shared preferences.
     */
    public String getCurrentCentValue() {
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        return sharedPreferences.getString(CURRENT_CENT_TOTAL_KEY, "00");
    }

    /**
     * Displays the current debt value on the UI.
     */
    public void displayCurrentDebt () {
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        String total = "$" + sharedPreferences.getString(CURRENT_DOLLAR_TOTAL_KEY, "0")
                + "." + sharedPreferences.getString(CURRENT_CENT_TOTAL_KEY, "00");
        currentDebtValue.setText(total);
    }

    /**
     * Centrally aligns the text in a given Snackbar.
     * @param snackbar The snackbar whose text is to be centrally aligned.
     */
    public void centerAlignSnackbarText(Snackbar snackbar) {
        View view = snackbar.getView();
        TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }


    public void setTheme(int resid) {
        super.setTheme(resid);
        Toolbar actionToolBar = findViewById(R.id.toolbar);
        if (actionToolBar != null) {
            //actionToolBar.setBackgroundColor();
            //Set shared preferences for the background colour
            //Do the same for the Navigation Header
        }
    }

    public void initialiseTheme() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String currentTheme = sharedPreferences.getString("theme_settings_list", "defaultTheme");

        if(currentTheme.equals("defaultTheme")){
            setTheme(R.style.DefaultAppTheme);
        }

        if(currentTheme.equals("halloweenTheme")){
            setTheme(R.style.Halloween);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Sets status bar icons to black on the main activity.
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        if(currentTheme.equals("rosesTheme")){
            setTheme(R.style.RosesTheme);
        }
    }

    private void setButtonListeners() {
        ImageButton addButton = findViewById(R.id.addButton);
        ImageButton payOffButton = findViewById(R.id.payOffButton);
        ImageButton newTotalButton = findViewById(R.id.newTotalButton);
        ImageButton undoButton = findViewById(R.id.undoButton);

        final Button weeklyButton = findViewById(R.id.weekly_button);
        final Button monthlyButton = findViewById(R.id.monthly_button);
        final Button yearlyButton = findViewById(R.id.yearly_button);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        decimalPointIncluded = sharedPreferences.getBoolean("decimal_point_separator_switch", false);

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undo();
            }
        });

        if (decimalPointIncluded) {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    AddToFragmentDCI addFragDCI = new AddToFragmentDCI();
                    addFragDCI.show(fm, "ui_add_to_DCI_fragment");
                }
            });
            payOffButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    PayOffFragmentDCI payFragDCI = new PayOffFragmentDCI();
                    payFragDCI.show(fm, "ui_pay_off_DCI_fragment");
                }
            });
            newTotalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    SetNewFragmentDCI newFragDCI = new SetNewFragmentDCI();
                    newFragDCI.show(fm, "ui_set_new_DCI_fragment");
                }
            });
        } else {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    AddToFragmentDPI addFragDPI = new AddToFragmentDPI();
                    addFragDPI.show(fm, "ui_add_to_DPI_fragment");
                }
            });
            payOffButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    PayOffFragmentDPI payFragDPI = new PayOffFragmentDPI();
                    payFragDPI.show(fm, "ui_pay_off_DPI_fragment");
                }
            });
            newTotalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    SetNewFragmentDPI newFragDPI = new SetNewFragmentDPI();
                    newFragDPI.show(fm, "ui_set_new_DPI_fragment");
                }
            });
        }

        weeklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CURRENT_PERIOD_KEY, "WEEKLY");
                editor.apply();
                v.setBackgroundResource(R.drawable.button_focused);

                Resources.Theme theme = getTheme();
                TypedValue typedValue = new TypedValue();
                theme.resolveAttribute(R.attr.background, typedValue, true);
                ((Button) v).setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                theme.resolveAttribute(R.attr.mainImageButtonTint, typedValue, true);

                monthlyButton.setBackgroundResource(R.drawable.button_unfocused);
                monthlyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                yearlyButton.setBackgroundResource(R.drawable.button_unfocused);
                yearlyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));


                loadDataFromRoom();
            }
        });

        monthlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CURRENT_PERIOD_KEY, "MONTHLY");
                editor.apply();
                v.setBackgroundResource(R.drawable.button_focused);

                Resources.Theme theme = getTheme();
                TypedValue typedValue = new TypedValue();
                theme.resolveAttribute(R.attr.background, typedValue, true);
                ((Button) v).setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                theme.resolveAttribute(R.attr.mainImageButtonTint, typedValue, true);

                weeklyButton.setBackgroundResource(R.drawable.button_unfocused);
                weeklyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                yearlyButton.setBackgroundResource(R.drawable.button_unfocused);
                yearlyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));


                loadDataFromRoom();
            }
        });

        yearlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CURRENT_PERIOD_KEY, "YEARLY");
                editor.apply();
                v.setBackgroundResource(R.drawable.button_focused);

                Resources.Theme theme = getTheme();
                TypedValue typedValue = new TypedValue();
                theme.resolveAttribute(R.attr.background, typedValue, true);
                ((Button) v).setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                theme.resolveAttribute(R.attr.mainImageButtonTint, typedValue, true);

                weeklyButton.setBackgroundResource(R.drawable.button_unfocused);
                weeklyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                monthlyButton.setBackgroundResource(R.drawable.button_unfocused);
                monthlyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                loadDataFromRoom();
            }
        });
    }

    /**
     * Gets the current period value from shared preferences.
     */
    public String getPeriod() {
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        return sharedPreferences.getString(CURRENT_PERIOD_KEY, "MONTHLY");
    }

    public long getFirstUseDate() {
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        return sharedPreferences.getLong(FIRST_USE_DATE, -1);
    }

    public void setCurrentPeriodButton() {

        final Button weeklyButton = findViewById(R.id.weekly_button);
        final Button monthlyButton = findViewById(R.id.monthly_button);
        final Button yearlyButton = findViewById(R.id.yearly_button);

        Resources.Theme theme = getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.background, typedValue, true);

        String period = getPeriod();
        switch (period) {
            case "WEEKLY":
                weeklyButton.setBackgroundResource(R.drawable.button_focused);
                weeklyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));
                break;
            case "MONTHLY":
                monthlyButton.setBackgroundResource(R.drawable.button_focused);
                monthlyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));
                break;
            case "YEARLY":
                yearlyButton.setBackgroundResource(R.drawable.button_focused);
                yearlyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));
                break;
        }
    }

    /*

    public void drawGraph() {
        DebtValue highestDebtValue = dummyGraphDataSet.getHighestDebtValue();
        float[] dummyGraphData = dummyGraphDataSet.getDummyData();
        periodGraphView = new PeriodGraphView(this);
        Calendar cal = Calendar.getInstance();
        cal.set(2019, Calendar.JANUARY, 14);
        String period = getPeriod();
        periodGraphView.setXAxisScaleFactor(cal.getTime(), period);
        periodGraphView.setYAxisScaleFactor(highestDebtValue);
        periodGraphView.setDataSet(dummyGraphData);
    }

    public void drawGraph(PeriodGraphView periodGraphV) {
        DebtValue highestDebtValue = dummyGraphDataSet.getHighestDebtValue();
        float[] dummyGraphData = dummyGraphDataSet.getDummyData();
        periodGraphV = new PeriodGraphView(this);
        Calendar cal = Calendar.getInstance();
        cal.set(2019, Calendar.JANUARY, 14);
        String period = getPeriod();
        periodGraphV.setXAxisScaleFactor(cal.getTime(), period);
        periodGraphV.setYAxisScaleFactor(highestDebtValue);
        periodGraphV.setDataSet(dummyGraphData);
    }

    */

    /**
     * Loads data from the Room database according to the current period setting.
     */
    public void loadDataFromRoom() {
        String period = getPeriod();
        switch(period) {
            case "WEEKLY":
                loadWeeklyDataFromRoom();
                break;
            case "MONTHLY":
                loadMonthlyDataFromRoom();
                break;
            case "YEARLY":
                loadYearlyDataFromRoom();
            default:
                break;

        }
    }

    public void loadWeeklyDataFromRoom(){
        ArrayList<float[]> datasets = new ArrayList<>();
        currentDate = new Date();
        Date startOfWeek = getStartOfWeek(currentDate);
        setStartOfPeriod(startOfWeek);
        Date endOfWeek = getEndOfCurrentWeek();
        setEndOfPeriod(endOfWeek);
        Date startOfPreviousWeek = getStartOfPreviousWeek(startOfWeek);
        Date endOfPreviousWeek = getEndOfPreviousWeek(startOfWeek);
        Date startOfFollowingWeek = getStartOfFollowingWeek(startOfWeek);
        Date endOfFollowingWeek = getEndOfFollowingWeek(startOfWeek);
        List<DebtValue> debtValueListCurrent = DebtValueDatabaseAccessor.
                getDebtValuesBetween(startOfWeek, currentDate, this);
        List<DebtValue> debtValueListPrior = DebtValueDatabaseAccessor.
                getDebtValuesBetween(startOfPreviousWeek, endOfPreviousWeek, this);
        List<DebtValue> debtValueListFollowing = DebtValueDatabaseAccessor.
                getDebtValuesBetween(startOfFollowingWeek, endOfFollowingWeek, this);
        float[] dataset = convertDebtValueListToFloatArray(
                debtValueListPrior,
                debtValueListCurrent,
                debtValueListFollowing);
        if (dataset != null) {
            datasets.add(dataset);
        }

        Date startOfNewCurrentWeek = getStartOfPreviousWeek(startOfWeek);
        Date endOfNewCurrentWeek = getEndOfPreviousWeek(startOfWeek);

        if (getFirstUseDate() == -1) {
            sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(FIRST_USE_DATE, currentDate.getTime());
            editor.apply();
        }
        Date firstUseDate = new Date(getFirstUseDate());

        while (DebtValueDatabaseAccessor.getDebtValuesBetween(firstUseDate, endOfNewCurrentWeek, this) != null
                && endOfNewCurrentWeek.getTime() > firstUseDate.getTime()) {
            List<DebtValue> debtValueListNewCurrent = DebtValueDatabaseAccessor.
                    getDebtValuesBetween(startOfNewCurrentWeek, endOfNewCurrentWeek, this);
            List<DebtValue> debtValueListNewPrior = DebtValueDatabaseAccessor.
                    getDebtValuesBetween(getStartOfPreviousWeek(startOfNewCurrentWeek), getEndOfPreviousWeek(startOfNewCurrentWeek), this);
            List<DebtValue> debtValueListNewFollowing = DebtValueDatabaseAccessor.
                    getDebtValuesBetween(getStartOfFollowingWeek(startOfNewCurrentWeek), getEndOfFollowingWeek(startOfNewCurrentWeek), this);
            float[] datasetNew = convertDebtValueListToFloatArray(
                    debtValueListNewPrior,
                    debtValueListNewCurrent,
                    debtValueListNewFollowing);
            if (datasetNew != null){
                datasets.add(datasetNew);
            }
            startOfNewCurrentWeek = getStartOfPreviousWeek(startOfNewCurrentWeek);
            endOfNewCurrentWeek = getEndOfPreviousWeek(startOfNewCurrentWeek);
        }
        mRecyclerView.setAdapter(new GraphRecyclerViewAdapter(datasets, getPeriod()));
    }

    public void loadMonthlyDataFromRoom(){
        ArrayList<float[]> datasets = new ArrayList<>();
        currentDate = new Date();
        Date startOfMonth = getStartOfMonth(currentDate);
        setStartOfPeriod(startOfMonth);
        Date endOfMonth = getEndOfCurrentMonth();
        setEndOfPeriod(endOfMonth);
        Date startOfPreviousMonth = getStartOfPreviousMonth(startOfMonth);
        Date endOfPreviousMonth = getEndOfPreviousMonth(startOfMonth);
        Date startOfFollowingMonth = getStartOfFollowingMonth(startOfMonth);
        Date endOfFollowingMonth = getEndOfFollowingMonth(startOfMonth);
        List<DebtValue> debtValueListCurrent = DebtValueDatabaseAccessor.
                getDebtValuesBetween(startOfMonth, currentDate, this);
        List<DebtValue> debtValueListPrior = DebtValueDatabaseAccessor.
                getDebtValuesBetween(startOfPreviousMonth, endOfPreviousMonth, this);
        List<DebtValue> debtValueListFollowing = DebtValueDatabaseAccessor.
                getDebtValuesBetween(startOfFollowingMonth, endOfFollowingMonth, this);
        float[] dataset = convertDebtValueListToFloatArray(
                debtValueListPrior,
                debtValueListCurrent,
                debtValueListFollowing);
        if (dataset != null) {
            datasets.add(dataset);
        }

        Date startOfNewCurrentMonth = getStartOfPreviousMonth(startOfMonth);
        Date endOfNewCurrentMonth = getEndOfPreviousMonth(startOfMonth);

        if (getFirstUseDate() == -1) {
            sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(FIRST_USE_DATE, currentDate.getTime());
        }
        Date firstUseDate = new Date(getFirstUseDate());

        while (DebtValueDatabaseAccessor.getDebtValuesBetween(firstUseDate, endOfNewCurrentMonth, this) != null
                && endOfNewCurrentMonth.getTime() > firstUseDate.getTime()) {
            List<DebtValue> debtValueListNewCurrent = DebtValueDatabaseAccessor.
                    getDebtValuesBetween(startOfNewCurrentMonth, endOfNewCurrentMonth, this);
            List<DebtValue> debtValueListNewPrior = DebtValueDatabaseAccessor.
                    getDebtValuesBetween(getStartOfPreviousMonth(startOfNewCurrentMonth), getEndOfPreviousMonth(startOfNewCurrentMonth), this);
            List<DebtValue> debtValueListNewFollowing = DebtValueDatabaseAccessor.
                    getDebtValuesBetween(getStartOfFollowingMonth(startOfNewCurrentMonth), getEndOfFollowingMonth(startOfNewCurrentMonth), this);
            float[] datasetNew = convertDebtValueListToFloatArray(
                    debtValueListNewPrior,
                    debtValueListNewCurrent,
                    debtValueListNewFollowing);
            if (datasetNew != null){
                datasets.add(datasetNew);
            }
            startOfNewCurrentMonth = getStartOfPreviousMonth(startOfNewCurrentMonth);
            endOfNewCurrentMonth = getEndOfPreviousMonth(startOfNewCurrentMonth);
        }
        mRecyclerView.setAdapter(new GraphRecyclerViewAdapter(datasets, getPeriod()));
    }

    public void loadYearlyDataFromRoom(){
        ArrayList<float[]> datasets = new ArrayList<>();
        currentDate = new Date();
        Date startOfYear = getStartOfYear(currentDate);
        setStartOfPeriod(startOfYear);
        Date endOfYear = getEndOfCurrentYear();
        setEndOfPeriod(endOfYear);
        Date startOfPreviousYear = getStartOfPreviousYear(startOfYear);
        Date endOfPreviousYear = getEndOfPreviousYear(startOfYear);
        Date startOfFollowingYear = getStartOfFollowingYear(startOfYear);
        Date endOfFollowingYear = getEndOfFollowingYear(startOfYear);
        List<DebtValue> debtValueListCurrent = DebtValueDatabaseAccessor.
                getDebtValuesBetween(startOfYear, currentDate, this);
        List<DebtValue> debtValueListPrior = DebtValueDatabaseAccessor.
                getDebtValuesBetween(startOfPreviousYear, endOfPreviousYear, this);
        List<DebtValue> debtValueListFollowing = DebtValueDatabaseAccessor.
                getDebtValuesBetween(startOfFollowingYear, endOfFollowingYear, this);
        float[] dataset = convertDebtValueListToFloatArray(
                debtValueListPrior,
                debtValueListCurrent,
                debtValueListFollowing);
        if (dataset != null) {
            datasets.add(dataset);
        }


        Date startOfNewCurrentYear = getStartOfPreviousYear(startOfYear);
        Date endOfNewCurrentYear = getEndOfPreviousYear(startOfYear);

        if (getFirstUseDate() == -1) {
            sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(FIRST_USE_DATE, currentDate.getTime());
        }
        Date firstUseDate = new Date(getFirstUseDate());

        while (DebtValueDatabaseAccessor.getDebtValuesBetween(firstUseDate, endOfNewCurrentYear, this) != null
        && endOfNewCurrentYear.getTime() > firstUseDate.getTime()) {
            List<DebtValue> debtValueListNewCurrent = DebtValueDatabaseAccessor.
                    getDebtValuesBetween(startOfNewCurrentYear, endOfNewCurrentYear, this);
            List<DebtValue> debtValueListNewPrior = DebtValueDatabaseAccessor.
                    getDebtValuesBetween(getStartOfPreviousYear(startOfNewCurrentYear), getEndOfPreviousYear(startOfNewCurrentYear), this);
            List<DebtValue> debtValueListNewFollowing = DebtValueDatabaseAccessor.
                    getDebtValuesBetween(getStartOfFollowingYear(startOfNewCurrentYear), getEndOfFollowingYear(startOfNewCurrentYear), this);
            float[] datasetNew = convertDebtValueListToFloatArray(
                    debtValueListNewPrior,
                    debtValueListNewCurrent,
                    debtValueListNewFollowing);
            if (datasetNew != null){
                datasets.add(datasetNew);
            }
            startOfNewCurrentYear = getStartOfPreviousYear(startOfNewCurrentYear);
            endOfNewCurrentYear = getEndOfPreviousYear(startOfNewCurrentYear);
        }
        mRecyclerView.setAdapter(new GraphRecyclerViewAdapter(datasets, getPeriod()));
    }
    
    public float[] convertDebtValueListToFloatArray(List<DebtValue> priorDebtValues,
                                                    List<DebtValue> currentDebtValues,
                                                    List<DebtValue> followingDebtValues) {
        int floatArraySize;
        int incrementer;

        if (currentDebtValues != null) {

        if (followingDebtValues != null) {
            floatArraySize = (currentDebtValues.size() * 4) + 4;
        } else {
            floatArraySize = currentDebtValues.size() * 4;
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

    public void setStartOfPeriod(Date date) {
        startOfPeriod = date.getTime();
    }

    public void setEndOfPeriod(Date date) {
        endOfPeriod = date.getTime();
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

    public Date getEndOfCurrentWeek() {
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

    public Date getEndOfCurrentMonth() {
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

    public Date getEndOfCurrentYear() {
        Date startOfYear = getStartOfYear(currentDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startOfYear);
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    private interface OnRoomDataChanged {
        void dataChanged();
    }

}
