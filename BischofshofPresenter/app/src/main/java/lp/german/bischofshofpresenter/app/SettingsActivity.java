package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by paullichtenberger on 11.06.14.
 */
public class SettingsActivity extends Activity {

    public static final String KEY_PREF_MARKE = "pref_marke";
    public static final String KEY_PREF_PATH = "pref_path";
    public static final String KEY_PREF_PROJECT = "pref_project";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Preferences())
                .commit();
    }

    public void setupFolder(){

    }
}
