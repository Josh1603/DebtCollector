package syd.jjj.debtcollector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private String currentTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        currentTheme = sharedPreferences.getString("theme_settings_list", "defaultTheme");

        if (currentTheme.equals("defaultTheme")) {
            setTheme(R.style.DefaultAppTheme);
        }

        if (currentTheme.equals("halloweenTheme")) {
            setTheme(R.style.Halloween);
        }

        if(currentTheme.equals("rosesTheme")){
            setTheme(R.style.RosesTheme);
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("theme_settings_list")) {
                    Intent intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                }
    }

}
