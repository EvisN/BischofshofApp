package lp.german.bischofshofpresenter.app;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by paullichtenberger on 11.06.14.
 */

public class Preferences extends PreferenceFragment {

    public static String m_chosenDir;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference pref_marke = findPreference(SettingsActivity.KEY_PREF_MARKE);
        pref_marke.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getActivity().finish();
                return true;
            }
        });

        /*
        Preference pref_path = findPreference(SettingsActivity.KEY_PREF_PATH);

        pref_path.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
            return true;
            }
        });
        */


    }
}