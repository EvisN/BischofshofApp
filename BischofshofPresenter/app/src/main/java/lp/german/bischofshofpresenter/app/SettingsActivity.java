package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by paullichtenberger on 11.06.14.
 */
public class SettingsActivity extends Activity {

    public static final String KEY_PREF_MARKE = "pref_marke";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Preferences())
                .commit();
    }
}