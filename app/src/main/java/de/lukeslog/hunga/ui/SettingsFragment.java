package de.lukeslog.hunga.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.rest.RestService;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;

public class SettingsFragment extends PreferenceFragment {

    public static final String TAG = HungaConstants.TAG;
    private SettingsFragment context;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        context = this;

        final PreferenceCategory pref = (PreferenceCategory) getPreferenceManager().findPreference("pref_reload_title");
        final Preference reloadLists = new Preference(getActivity());
        reloadLists.setSummary("Listen aktualisieren");
        reloadLists.setIcon(R.drawable.ic_action_reload);
        reloadLists.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Logger.d(TAG, "CLICK");
                SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String accid = defsettings.getString("googleAccId", "");
                RestService.updateLists(accid);
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

        final PreferenceCategory pref2 = (PreferenceCategory) getPreferenceManager().findPreference("pref_errors");
        final Preference errorAc = new Preference(getActivity());
        errorAc.setSummary("Errors anzeigen");
        errorAc.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), ErrorActivity.class));
                return true;
            }
        });
        pref2.addPreference(errorAc);

        final SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String accName = defsettings.getString("googleDisplayName", "");
        final PreferenceCategory pref3 = (PreferenceCategory) getPreferenceManager().findPreference("pref_acount");
        final Preference logedinas = new Preference(getActivity());
        logedinas.setSummary("Eingeloged als "+accName);

        final Preference logout = new Preference(getActivity());
        logout.setSummary("Log Out");
        logout.setIcon(R.drawable.ic_action_exit);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage((FragmentActivity) getActivity(), new GoogleApiClient.OnConnectionFailedListener() {

                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                SharedPreferences.Editor editor = defsettings.edit();
                                editor.putString("googleAccId", "");
                                editor.putString("googleDisplayName", "");
                                editor.commit();
                                getActivity().finish();
                            }
                        });
                return true;
            }
        });

        pref3.addPreference(logedinas);
        pref3.addPreference(logout);

    }

    private void toast(String toastText) {
        Context context = getActivity().getApplicationContext();
        CharSequence text = toastText;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
