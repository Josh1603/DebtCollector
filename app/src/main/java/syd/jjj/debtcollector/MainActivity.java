package syd.jjj.debtcollector;

import android.content.SharedPreferences;
import android.os.Bundle;
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
        implements NavigationView.OnNavigationItemSelectedListener, MyDialogFragment.DataPass {

    private TextView currentDebtValue;
    private SharedPreferences prefs;
    private String prefName = "MyPref";

    private final static String TEXT_VALUE_KEY = "textvalue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentDebtValue = findViewById(R.id.current_debt_value);
        setCurrentDebtValue();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                MyDialogFragment dm = new MyDialogFragment();
                dm.show(fm, "chooser_fragment");
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void dialogFragmentNewDebtValue(String dollars, String cents) {
        setCurrentDebtValue(dollars, cents);
    }

    public void setCurrentDebtValue(String dollars, String cents) {
        if (dollars.length() < 1) {
            dollars = "0";
        }
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (cents.length() > 1) {
            editor.putString(TEXT_VALUE_KEY, "$" + dollars + "." + cents);
        } else {
            if (cents.length() > 0) {
                editor.putString(TEXT_VALUE_KEY, "$" + dollars + "." + cents + "0");

            } else {
                editor.putString(TEXT_VALUE_KEY, "$" + dollars + "." + cents + "00");
            }
        }
        editor.apply();
        setCurrentDebtValue();
    }

        public void dialogFragmentAddDebt (String dollars, String cents){

            if (cents.length() < 1) {
                cents = "00";
            } else {
                if (cents.length() < 2) {
                    cents = cents.concat("0");
                }
            }

            if (dollars.length() < 1) {
                dollars = "00";
            }

            String currentTotalStringDollar = currentDebtValue.getText().toString();
            String currentTotalString = currentTotalStringDollar.replaceAll("[^0-9]", "");
            int currentTotal = Integer.parseInt(currentTotalString);
            String additionalDebtString = dollars.concat(cents);
            int additionalDebt = Integer.parseInt(additionalDebtString);
            int totalDebt = currentTotal + additionalDebt;
            char[] totalDebtCharArray = (Integer.toString(totalDebt)).toCharArray();
            char[] totalDollarsCharArray = new char[totalDebtCharArray.length - 2];
            char[] totalCentsCharArray = new char[2];


            for (int i = 0; i <= (totalDebtCharArray.length - 3); i++) {
                totalDollarsCharArray[i] = totalDebtCharArray[i];
            }

            totalCentsCharArray[0] = totalDebtCharArray[totalDebtCharArray.length - 2];
            totalCentsCharArray[1] = totalDebtCharArray[totalDebtCharArray.length - 1];

            String totalDollars = String.valueOf(totalDollarsCharArray);
            String totalCents = String.valueOf(totalCentsCharArray);

            setCurrentDebtValue(totalDollars, totalCents);
        }

        public void dialogFragmentPayOffDebt (String dollars, String cents){

            if (cents.length() < 1) {
                cents = "00";
            } else {
                if (cents.length() < 2) {
                    cents = cents.concat("0");
                }
            }

            String currentTotalStringDollar = currentDebtValue.getText().toString();
            String currentTotalString = currentTotalStringDollar.replaceAll("[^0-9]", "");
            int currentTotal = Integer.parseInt(currentTotalString);
            String minusDebtString = dollars.concat(cents);
            int minusDebt = Integer.parseInt(minusDebtString);
            int totalDebt = currentTotal - minusDebt;
            int remainder;

            if (totalDebt <= 0) {
                remainder = -(totalDebt);
                totalDebt = 000;
                String remainderText = concatenatedIntToDollarCentString(remainder);

                if (remainder != 0) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main_view), "You paid off your debt and an additional " + remainderText + "!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main_view), "Woohoo! You've paid off all your debt.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            if (totalDebt >= 10) {
                char[] totalDebtCharArray = (Integer.toString(totalDebt)).toCharArray();
                char[] totalDollarsCharArray = new char[totalDebtCharArray.length - 2];
                char[] totalCentsCharArray = new char[2];


                for (int i = 0; i <= (totalDebtCharArray.length - 3); i++) {
                    totalDollarsCharArray[i] = totalDebtCharArray[i];
                }

                totalCentsCharArray[0] = totalDebtCharArray[totalDebtCharArray.length - 2];
                totalCentsCharArray[1] = totalDebtCharArray[totalDebtCharArray.length - 1];

                String totalDollars = String.valueOf(totalDollarsCharArray);
                String totalCents = String.valueOf(totalCentsCharArray);

                setCurrentDebtValue(totalDollars, totalCents);
            } else {

            if (totalDebt > 1) {
                setCurrentDebtValue("0", Integer.toString(totalDebt));
            } else {
                setCurrentDebtValue("0", "00");
            }
            }
        }


        public void setCurrentDebtValue () {
            prefs = getSharedPreferences(prefName, MODE_PRIVATE);
            currentDebtValue.setText(prefs.getString(TEXT_VALUE_KEY, "$0.00"));
        }

        public String concatenatedIntToDollarCentString (int concatenatedInt) {

        if (concatenatedInt >= 10) {

            char[] totalDebtCharArray = (Integer.toString(concatenatedInt)).toCharArray();
            char[] totalDollarsCharArray = new char[totalDebtCharArray.length - 2];
            char[] totalCentsCharArray = new char[2];


            for (int i = 0; i <= (totalDebtCharArray.length - 3); i++) {
                totalDollarsCharArray[i] = totalDebtCharArray[i];
            }

            totalCentsCharArray[0] = totalDebtCharArray[totalDebtCharArray.length - 2];
            totalCentsCharArray[1] = totalDebtCharArray[totalDebtCharArray.length - 1];

            String totalDollars = String.valueOf(totalDollarsCharArray);
            String totalCents = String.valueOf(totalCentsCharArray);
            return dollarsAndCentsString(totalDollars, totalCents);
        } else {
            if (concatenatedInt > 0) {
                return dollarsAndCentsString("0", Integer.toString(concatenatedInt));
            } else {
                return dollarsAndCentsString("0", "0");
            }
        }

        }

    public String dollarsAndCentsString(String dollars, String cents) {
        if (cents.length() > 1) {
            return "$" + dollars + "." + cents;
        } else {
            if (cents.length() > 0) {
                return "$" + dollars + "." + cents + "0";
            } else {
                return "$" + dollars + "." + cents + "00";
            }
        }
    }

}
