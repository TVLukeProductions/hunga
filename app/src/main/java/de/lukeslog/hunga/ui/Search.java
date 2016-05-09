package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.List;

import de.lukeslog.hunga.Notification.NotificationService;
import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.model.ProposalHelper;
import de.lukeslog.hunga.model.ProposalHelperCarbohydrates;
import de.lukeslog.hunga.model.ProposalHelperFat;
import de.lukeslog.hunga.model.ProposalHelperKcal;
import de.lukeslog.hunga.model.ProposalHelperPhe;
import de.lukeslog.hunga.model.ProposalHelperProtein;
import de.lukeslog.hunga.model.ProposalHelperSalt;
import de.lukeslog.hunga.model.Recipe;
import de.lukeslog.hunga.rest.RestService;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;
import de.lukeslog.hunga.support.SupportService;

public class Search extends FragmentActivity {

    public static final String TAG = HungaConstants.TAG;
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 8855;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActiveAndroid.initialize(this);

        setContentView(R.layout.activity_search);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {

                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        setUpUi();
    }

    private void setUpUi() {
        SignInButton signinwithgoogle = (SignInButton) findViewById(R.id.sign_in_button);

        final LinearLayout loadingstuff = (LinearLayout) findViewById(R.id.loadingstuff);
        final TextView thetext = (TextView) findViewById(R.id.largetext);
        final EditText search = (EditText) findViewById(R.id.editText2);
        final ImageView searchb = (ImageView) findViewById(R.id.search);

        SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(this);
        final String accid = defsettings.getString("googleAccId", "");
        if(accid.equals("")) {

            loadingstuff.setVisibility(View.GONE);
            thetext.setVisibility(View.GONE);
            search.setVisibility(View.GONE);
            searchb.setVisibility(View.GONE);
            signinwithgoogle.setVisibility(View.VISIBLE);

            signinwithgoogle.setSize(SignInButton.SIZE_STANDARD);
            signinwithgoogle.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mGoogleApiClient.connect();
                    Logger.d(TAG, "CONNECTED(2)? " + mGoogleApiClient.isConnected());
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });
        } else {

            loadingstuff.setVisibility(View.VISIBLE);
            thetext.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
            searchb.setVisibility(View.VISIBLE);

            signinwithgoogle.setVisibility(View.GONE);
        }

        List<Food> foodlist = new Select().from(Food.class).orderBy("name ASC").execute();
        final Button download = (Button) findViewById(R.id.button);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        if(foodlist.size()>0) {
            if(download != null) {
                download.setVisibility(View.GONE);
            }
            if(progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        } else {
            download.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    RestService.updateLists(accid);
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                while (RestService.isLoading()) {
                                    Logger.d(TAG, ".");
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                           if(progressBar.getProgress()==100) {
                                               progressBar.setProgress(0);
                                           }
                                            if(RestService.isLoading()) {
                                                progressBar.setProgress(progressBar.getProgress() + 1);
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                    Thread.sleep(300);
                                }
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        download.setVisibility(View.GONE);
                                        loadingstuff.removeAllViews();
                                    }
                                });
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
        }

        String searchtype = defsettings.getString("pref_typeselector", "");
        if(!searchtype.equals("")) {
            thetext.setText("Wieviel "+searchtype+" soll dein Essen haben?");
        }
        final ImageView searchButton = (ImageView) findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!search.getEditableText().toString().equals("")) {
                    search(search.getEditableText().toString());
                }
            }
        });

        startService(new Intent(this, SupportService.class));
        startService(new Intent(this, RestService.class));
        startService(new Intent(this, NotificationService.class));

        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.d(TAG, "WHAT?"+requestCode);
        if (requestCode == RC_SIGN_IN) {
            Logger.d(TAG, "erfolg....");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Logger.d(TAG, acct.getId());
            SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = defsettings.edit();

            Logger.d(TAG, "------> "+acct.getIdToken());
            editor.putString("googleAccId", acct.getId());
            editor.putString("googleDisplayName", acct.getDisplayName());
            editor.commit();

        } else {
            // Signed out, show unauthenticated UI.
            Logger.d(TAG, result.getStatus().toString());
        }
        setUpUi();
    }


    private void search(String s) {
        double limit = Double.parseDouble(s);
        Logger.d(TAG, "LIMIT: " + limit);
        new Delete().from(Proposal.class).execute();
        List<Recipe> recp = new Select().from(Recipe.class).execute();
        for(Recipe recipe : recp){
            double factor = calculateFactor(recipe, limit);
            new ProposalHelperKcal().fillProposalWithFactor(recipe, factor);
        }
        startActivity(new Intent(this, ProposalList.class));
    }

    private double calculateFactor(Recipe recipe, double limit) {
        Logger.d(TAG, "calculate Factor...");
        SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(this);
        String searchtype = defsettings.getString("pref_typeselector", "");
        if(searchtype.equals("Fett")) {
            return new ProposalHelperFat().getFactorForGoal(recipe, limit, false);
        } else  if( searchtype.equals("Phe")){
            return new ProposalHelperPhe().getFactorForGoal(recipe, limit, false);
        } else  if( searchtype.equals("Eiweis")){
            return new ProposalHelperProtein().getFactorForGoal(recipe, limit, false);
        } else  if( searchtype.equals("Kohlenhydrate")){
            return new ProposalHelperCarbohydrates().getFactorForGoal(recipe, limit, false);
        } else  if( searchtype.equals("Salz")){
            return new ProposalHelperSalt().getFactorForGoal(recipe, limit, false);
        } else {
            return new ProposalHelperKcal().getFactorForGoal(recipe, limit, false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(this);
        String accid = defsettings.getString("googleAccId", "");
        if(accid.equals("")) {
            getMenuInflater().inflate(R.menu.searchnoacc, menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.search, menu);
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpUi();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startSettingsActivity();
            return true;
        }
        if(id == R.id.action_protocoll) {
            startProtokollActivity();
        }
        if(id == R.id.action_add) {
            startAddActivity();
        }
        if(id == R.id.action_foods) {
            startActivity(new Intent(this, AllFoodsList.class));
        }
        if( id == R.id.action_recepies) {
            new Delete().from(Proposal.class).execute();
            List<Recipe> recp = new Select().from(Recipe.class).execute();
            for(Recipe recipe : recp) {
                new ProposalHelperKcal().fillProposalWithFactor(recipe, 1.0);
            }
            startActivity(new Intent(this, ProposalList.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void startProtokollActivity() {
        Intent intent = new Intent(this, ProtokollActivity.class);
        startActivity(intent);
    }

    private void startAddActivity() {
        startActivity(new Intent(this, AddChoiceActivity.class));
    }

    private void startSettingsActivity() {
        final Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    private void toast(String toastText) {
        Context context = getApplicationContext();
        CharSequence text = toastText;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}