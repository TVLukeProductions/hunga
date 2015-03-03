package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.model.ProposalHelper;
import de.lukeslog.hunga.model.ProposalHelperKcal;
import de.lukeslog.hunga.model.ProposalHelperPhe;
import de.lukeslog.hunga.model.Recipe;
import de.lukeslog.hunga.rest.RestService;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;
import de.lukeslog.hunga.support.SupportService;


public class Search extends Activity {

    public static final String TAG = HungaConstants.TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActiveAndroid.initialize(this);

        setContentView(R.layout.activity_search);

        startService(new Intent(this, SupportService.class));
        startService(new Intent(this, RestService.class));

        final TextView thetext = (TextView) findViewById(R.id.largetext);
        SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(this);
        final EditText search = (EditText) findViewById(R.id.editText2);
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
    }

    private void search(String s) {
        double limit = Double.parseDouble(s);
        Logger.d(TAG, "" + limit);
        new Delete().from(Proposal.class).execute();
        List<Recipe> recp = new Select().from(Recipe.class).execute();
        for(Recipe recipe : recp){
            Logger.d(TAG, "---->"+ recipe.getFood1().getkcal());
            //TODO: decide if its a factor based on phe or kcal
            double factor = calculateFactor(recipe, limit);
            ProposalHelper.fillProposalWithFactor(recipe, factor);
        }
        startActivity(new Intent(this, ProposalList.class));
    }

    private double calculateFactor(Recipe recipe, double limit) {
        SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(this);
        String searchtype = defsettings.getString("pref_typeselector", "");
        if(searchtype.equals("phe") || searchtype.equals("")) {
            return ProposalHelperPhe.getFactorForGoal(recipe, limit);
        } else {
            return ProposalHelperKcal.getFactorForGoal(recipe, limit);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();

        final TextView thetext = (TextView) findViewById(R.id.largetext);
        SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(this);
        String searchtype = defsettings.getString("pref_typeselector", "");
        if(!searchtype.equals("")) {
            thetext.setText("Wieviel "+searchtype+" soll dein Essen haben?");
        }
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
        return super.onOptionsItemSelected(item);
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