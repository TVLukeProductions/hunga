package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.model.ProtokollItem;
import de.lukeslog.hunga.rest.Downloader;
import de.lukeslog.hunga.support.HungaConstants;

public class ProtokollActivity extends Activity {

    public static final String TAG = HungaConstants.TAG;

    Context ctx;
    List<ProtokollItem> protkollItems;
    ProtokollItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protokoll);
        ctx = this;

        populateList();

        new LongOperation().execute("");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null) {
            protkollItems = new Select().from(ProtokollItem.class).orderBy("timestamp ASC").execute();
            adapter.notifyDataSetChanged();
        }
    }

    private void populateList() {
        protkollItems = new Select().from(ProtokollItem.class).orderBy("timestamp ASC").execute();
        ListView listViewWithRecipies = (ListView) findViewById(R.id.protokolllist);
        ProtokollItemAdapter adapter = new ProtokollItemAdapter(this, protkollItems);
        listViewWithRecipies.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(ctx);
            String accid = defsettings.getString("googleAccId", "");
            Downloader.readAndStoreConsumtion(accid, ctx);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            populateList();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
}
