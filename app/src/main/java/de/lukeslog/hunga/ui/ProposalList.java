package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.SupportService;

public class ProposalList extends Activity {

    public static final String TAG = HungaConstants.TAG;
    Context ctx;

    private List<Proposal> proposals = new ArrayList<Proposal>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipielist);
        ctx = this;
        SharedPreferences sharedPref = SupportService.getDefaultSettings();
        //TODO:
        String minweight = sharedPref.getString("pref_minfood", "0");
        String maxweight = sharedPref.getString("pref_maxfood", "3000");

        proposals = new Select().from(Proposal.class).where("weight > ?", minweight).
                and("weight < ?", maxweight).orderBy("weight COLLATE NOCASE ASC").execute();
        ListView listViewWithRecipies = (ListView) findViewById(R.id.listView);
        ProposalAdapter adapter = new ProposalAdapter(this, proposals);
        listViewWithRecipies.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listViewWithRecipies.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ctx, ProposalActivity.class);
                i.putExtra("proposaluid", ""+proposals.get(position).getUid());
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.proplist, menu);
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
}