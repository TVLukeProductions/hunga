package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.HungaError;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.support.HungaConstants;

public class ErrorActivity extends Activity {

    public static final String TAG = HungaConstants.TAG;
    Context ctx;
    private List<HungaError> hungaErrorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.erroractivity);
        ctx = this;
        hungaErrorList = new Select().from(HungaError.class).orderBy("timestamp ASC").execute();
        System.out.println(""+hungaErrorList.size());
        if(hungaErrorList.size()>0) {
            ListView listViewWithFoods = (ListView) findViewById(R.id.errorlist);
            ErrorAdapter adapter = new ErrorAdapter(this, hungaErrorList);
            listViewWithFoods.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
