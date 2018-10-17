package syd.jjj.debtcollector;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DollarCentInputFragment.DataPass {

    private TextView currentDebtValue;
    private DollarsAndCents dollarsAndCents;

    private SharedPreferences dollarPrefs;
    private SharedPreferences centPrefs;

    private String currentDollarTotal = "MyCurrentDollarTotal";
    private String currentCentTotal = "MyCurrentCentTotal";

    private final static String CURRENT_DOLLAR_TOTAL_KEY = "current_dollar_total";
    private final static String CURRENT_CENT_TOTAL_KEY = "current_cent_total";

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

    public void NewDebtValue(String uIDollars, String uICents) {

        dollarsAndCents = new DollarsAndCents(getCurrentDollarValue(), getCurrentCentValue(), uIDollars, uICents);
        dollarsAndCents.newDebtValue();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        displayCurrentDebt();
    }


    public void AddDebt (String uIDollars, String uICents){

        dollarsAndCents = new DollarsAndCents(getCurrentDollarValue(), getCurrentCentValue(), uIDollars, uICents);
        dollarsAndCents.addDebt();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        displayCurrentDebt();
    }

    public void PayOffDebt (String uIDollars, String uICents){

        dollarsAndCents = new DollarsAndCents(getCurrentDollarValue(), getCurrentCentValue(), uIDollars, uICents);
        dollarsAndCents.payOffDebt();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        displayCurrentDebt();
    }

    public void storeCurrentDollarValue() {
        dollarPrefs = getSharedPreferences(currentDollarTotal, MODE_PRIVATE);
        SharedPreferences.Editor editor = dollarPrefs.edit();
        editor.putString(CURRENT_DOLLAR_TOTAL_KEY, dollarsAndCents.getCurrentDollars());
        editor.apply();
    }

    public void storeCurrentCentValue() {
        centPrefs = getSharedPreferences(currentCentTotal, MODE_PRIVATE);
        SharedPreferences.Editor editor = centPrefs.edit();
        editor.putString(CURRENT_CENT_TOTAL_KEY, dollarsAndCents.getCurrentCents());
        editor.apply();
    }

    public String getCurrentDollarValue() {
        dollarPrefs = getSharedPreferences(currentDollarTotal, MODE_PRIVATE);
        return dollarPrefs.getString(CURRENT_DOLLAR_TOTAL_KEY, "0");
    }

    public String getCurrentCentValue() {
        centPrefs = getSharedPreferences(currentCentTotal, MODE_PRIVATE);
        return centPrefs.getString(CURRENT_CENT_TOTAL_KEY, "00");
    }

    public void displayCurrentDebt () {
        dollarPrefs = getSharedPreferences(currentDollarTotal, MODE_PRIVATE);
        centPrefs = getSharedPreferences(currentCentTotal, MODE_PRIVATE);
        String total = "$" + dollarPrefs.getString(CURRENT_DOLLAR_TOTAL_KEY, "0")
                + "." + centPrefs.getString(CURRENT_CENT_TOTAL_KEY, "00");
        currentDebtValue.setText(total);
    }

}
