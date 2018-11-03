package syd.jjj.debtcollector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageButton;
import android.widget.TextView;

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

    private boolean decimalPointIncluded;

    /**
     * Displays the current debt value and provides ImageButtons which open fragments to modify the
     * debt value, or in the case of the undo button, immediately undoes the last action.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentDebtValue = findViewById(R.id.current_debt_value);
        displayCurrentDebt();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton addButton = findViewById(R.id.addButton);
        ImageButton payOffButton = findViewById(R.id.payOffButton);
        ImageButton newTotalButton = findViewById(R.id.newTotalButton);
        ImageButton undoButton = findViewById(R.id.undoButton);

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
        displayCurrentDebt();
    }

    /**
     * Adds UI debt value and displays the new total.
     */
    public void addDebt() {
        debtCalculations.addDebt();
        storeCurrentDollarValue();
        storeCurrentCentValue();
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
            debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), previousDollarValue, previousCentValue);
            newDebtValue();
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

}
