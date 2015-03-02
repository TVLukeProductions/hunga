package hunga.lukeslog.de.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import hunga.lukeslog.de.R;
import hunga.lukeslog.de.model.Recipie;
import hunga.lukeslog.de.model.SearchResult;
import hunga.lukeslog.de.rest.RestService;
import hunga.lukeslog.de.support.HungaConstants;
import hunga.lukeslog.de.support.Logger;
import hunga.lukeslog.de.support.SupportService;


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
        new Delete().from(SearchResult.class).execute();
        List<Recipie> recp = new ArrayList<Recipie>();
        recp = new Select().from(Recipie.class).execute();
        for(Recipie recipie : recp){
            Logger.d(TAG, "---->"+recipie.getFood1().getkcal());
            double factor = calculateFactor(recipie, limit);
            SearchResult result = new SearchResult();
            result.setFactor(factor);
            result.setRecipie(recipie);
            result.setPhe(calculatePhe(recipie, factor));
            result.setKcal(calculatekcal(recipie, factor));
            result.save();
        }
        startActivity(new Intent(this, RecipieList.class));
    }

    private double calculatekcal(Recipie recipie, double factor) {
        return 0;
    }

    private double calculatePhe(Recipie recipie, double factor) {
        return 0;
    }

    private double calculateFactor(Recipie recipie, double limit) {
        //TODO: calculate what the factor is
        return 1.0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return super.onCreateOptionsMenu(menu);
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
