package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
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

        pupulateStatistics();

        new LongOperation().execute("");
    }

    private void pupulateStatistics() {
        protkollItems = new Select().from(ProtokollItem.class).orderBy("timestamp DESC").execute();
        Double[] kcalsevendays = new Double[7];
        for(int i=0; i< kcalsevendays.length; i++) {
            kcalsevendays[i]=0.0;
        }
        DateTime now = new DateTime();
        now = now.withHourOfDay(0);
        now = now.withMinuteOfHour(0);
        now = now.withSecondOfMinute(0);
        for(ProtokollItem item : protkollItems) {
            DateTime then = new DateTime(item.getTimestamp());
            then = then.withHourOfDay(0);
            then = then.withMinuteOfHour(0);
            then = then.withSecondOfMinute(0);
            int daysbetween = 6 - Days.daysBetween(then, now).getDays();
            if(daysbetween >= 0 && daysbetween < 7) {
                Food food = new Select().from(Food.class).where("barcodeForUse = ?", item.getBarcodeForUse()).executeSingle();
                float kcal = (float) (food.getKcal100() / 100.0f * item.getAmount());
                kcalsevendays[daysbetween] = kcalsevendays[daysbetween] + kcal;
            }
        }

        GraphView.GraphViewData[] gvd1 = new GraphView.GraphViewData[kcalsevendays.length];
        String[] xLabel = new String[kcalsevendays.length];
        for(int i=0; i<kcalsevendays.length; i++) {
            gvd1[i]= new GraphView.GraphViewData((i+1), kcalsevendays[i]);
            xLabel[i]="";
        }
        GraphViewSeries exampleSeries = new GraphViewSeries(gvd1);

        GraphView graphView = new BarGraphView(
                this // context
                , "7 Tage" // heading
        );
        graphView.setManualYAxisBounds((double) 4000, (double) 0);
        graphView.addSeries(exampleSeries); // data
        graphView.setHorizontalLabels(xLabel);

        LinearLayout layout = (LinearLayout) findViewById(R.id.statistics);
        layout.addView(graphView);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null) {
            protkollItems = new Select().from(ProtokollItem.class).orderBy("timestamp DESC").execute();
            adapter.notifyDataSetChanged();
            pupulateStatistics();
        }
    }

    private void populateList() {
        protkollItems = new Select().from(ProtokollItem.class).orderBy("timestamp DESC").execute();
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
            pupulateStatistics();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
}
