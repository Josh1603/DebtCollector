package syd.jjj.debtcollector;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * The main activity for this app. Current debt values are stored as shared preferences as a form of
 * basic data persistence. Calculations are delegated to the DebtCalculation class.
 */
public class DebtCollectorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DollarCentInputFragment.DCIF2DCA {

    private TextView currentDebtValue;
    private DebtCalculations debtCalculations;

    private SharedPreferences dollarPrefs;
    private SharedPreferences centPrefs;

    private String currentDollarTotal = "MyCurrentDollarTotal";
    private String currentCentTotal = "MyCurrentCentTotal";

    private final static String CURRENT_DOLLAR_TOTAL_KEY = "current_dollar_total";
    private final static String CURRENT_CENT_TOTAL_KEY = "current_cent_total";

    /**
     * Displays the current debt value and provides a FAB which opens the DollarCentInputFragment.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentDebtValue = findViewById(R.id.current_debt_value);
        displayCurrentDebt();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DollarCentInputFragment dm = new DollarCentInputFragment();
                dm.show(fm, "ui_dollar_cent_fragment");
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Called by the inner interface of DollarCentInputFragment. This method gets the new debt value
     * and displays it on the UI.
     */
    public void NewDebtValue(String uIDollars, String uICents) {

        debtCalculations = new DebtCalculations("0", "00", uIDollars, uICents);
        debtCalculations.newDebtValue();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        displayCurrentDebt();
    }

    /**
     * Called by the inner interface of DollarCentInputFragment. This method gets the summed debt value
     * and displays it on the UI.
     */
    public void AddDebt (String uIDollars, String uICents){

        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollars, uICents);
        debtCalculations.addDebt();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        displayCurrentDebt();
    }

    /**
     * Called by the inner interface of DollarCentInputFragment. This method gets the paid off debt value
     * and displays it on the UI.
     */
    public void PayOffDebt (String uIDollars, String uICents){

        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollars, uICents);
        debtCalculations.payOffDebt();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        displayCurrentDebt();
    }

    /**
     * Stores the current dollar value to shared preferences.
     */
    public void storeCurrentDollarValue() {
        dollarPrefs = getSharedPreferences(currentDollarTotal, MODE_PRIVATE);
        SharedPreferences.Editor editor = dollarPrefs.edit();
        editor.putString(CURRENT_DOLLAR_TOTAL_KEY, debtCalculations.getCurrentDollars());
        editor.apply();
    }

    /**
     * Stores the current cent value to shared preferences.
     */
    public void storeCurrentCentValue() {
        centPrefs = getSharedPreferences(currentCentTotal, MODE_PRIVATE);
        SharedPreferences.Editor editor = centPrefs.edit();
        editor.putString(CURRENT_CENT_TOTAL_KEY, debtCalculations.getCurrentCents());
        editor.apply();
    }

    /**
     * Gets the current dollar value from shared preferences.
     */
    public String getCurrentDollarValue() {
        dollarPrefs = getSharedPreferences(currentDollarTotal, MODE_PRIVATE);
        return dollarPrefs.getString(CURRENT_DOLLAR_TOTAL_KEY, "0");
    }

    /**
     * Gets the current cent value from shared preferences.
     */
    public String getCurrentCentValue() {
        centPrefs = getSharedPreferences(currentCentTotal, MODE_PRIVATE);
        return centPrefs.getString(CURRENT_CENT_TOTAL_KEY, "00");
    }

    /**
     * Displays the current debt value on the UI.
     */
    public void displayCurrentDebt () {
        dollarPrefs = getSharedPreferences(currentDollarTotal, MODE_PRIVATE);
        centPrefs = getSharedPreferences(currentCentTotal, MODE_PRIVATE);
        String total = "$" + dollarPrefs.getString(CURRENT_DOLLAR_TOTAL_KEY, "0")
                + "." + centPrefs.getString(CURRENT_CENT_TOTAL_KEY, "00");
        currentDebtValue.setText(total);
    }

}
