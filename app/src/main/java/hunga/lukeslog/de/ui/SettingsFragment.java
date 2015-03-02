package hunga.lukeslog.de.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import hunga.lukeslog.de.R;
import hunga.lukeslog.de.rest.RestService;
import hunga.lukeslog.de.support.HungaConstants;
import hunga.lukeslog.de.support.Logger;
import hunga.lukeslog.de.support.SupportService;

public class SettingsFragment extends PreferenceFragment {

    public static final String TAG = HungaConstants.TAG;
    private SettingsFragment context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        context = this;

        final PreferenceCategory pref = (PreferenceCategory) getPreferenceManager().findPreference("pref_reload_title");
        final Preference reloadLists = new Preference(getActivity());
        reloadLists.setSummary("Listen aktualisieren");
        reloadLists.setIcon(R.drawable.synchronize1);
        reloadLists.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Logger.d(TAG, "CLICK");
                RestService.updateLists();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            while (RestService.isLoading()) {
                                Logger.d(TAG, ".");
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        if(reloadLists.getSummary().toString().length()<35) {
                                            reloadLists.setSummary(reloadLists.getSummary().toString() + ".");
                                        } else {
                                            reloadLists.setSummary("Listen aktualisieren");
                                        }
                                    }
                                });
                                Thread.sleep(100);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    reloadLists.setSummary("Listen aktualisieren");
                                }
                            });
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
                return true;
            }
        });
        pref.addPreference(reloadLists);
    }

    private void toast(String toastText) {
        Context context = getActivity().getApplicationContext();
        CharSequence text = toastText;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
