package usc.cs578.trojannow.manager.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import usc.cs578.com.trojannow.R;

/*
 * Created by Ekasit_Ja on 21-Apr-15.
 */
public class Settings extends ActionBarActivity {

    private static final String TAG = Settings.class.getSimpleName();
    private static final String PREF_NAME = "TrojanNow-settings";
    private static final String TEMPT_UNITS = "temptUnits";
    private static final int FAHRENHEIT = 0;
    private static final int CELSIUS = 1;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // initiate and customize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getResources().getString(R.string.settings_toolbar_title));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.this.finish();
            }
        });

        // initiate share shared preferences
        settings = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if(settings.getInt(TEMPT_UNITS, FAHRENHEIT) == FAHRENHEIT) {
            ImageButton fah_button = (ImageButton) findViewById(R.id.fahrenheit_button);
            fah_button.performClick();
        }
        else {
            ImageButton cel_button = (ImageButton) findViewById(R.id.celsius_button);
            cel_button.performClick();
        }
    }

    public void setTemptUnits(View v) {
        ImageButton fah_button = (ImageButton) findViewById(R.id.fahrenheit_button);
        ImageButton cel_button = (ImageButton) findViewById(R.id.celsius_button);
        SharedPreferences.Editor editor = settings.edit();

        switch(v.getId()) {
            case R.id.fahrenheit_button: {
                fah_button.setImageResource(R.mipmap.ic_fahrenheit_selected);
                cel_button.setImageResource(R.mipmap.ic_celsius);
                editor.putInt(TEMPT_UNITS, FAHRENHEIT);
                break;
            }
            case R.id.celsius_button: {
                fah_button.setImageResource(R.mipmap.ic_fahrenheit);
                cel_button.setImageResource(R.mipmap.ic_celsius_selected);
                editor.putInt(TEMPT_UNITS, CELSIUS);
                break;
            }
            default: {
                Log.w(TAG, "setTemptUnits falls default switch case");
                return;
            }
        }

        editor.apply();
    }

}
