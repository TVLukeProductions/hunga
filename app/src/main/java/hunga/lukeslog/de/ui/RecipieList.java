package hunga.lukeslog.de.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import hunga.lukeslog.de.R;
import hunga.lukeslog.de.model.Recipie;
import hunga.lukeslog.de.model.SearchResult;
import hunga.lukeslog.de.support.HungaConstants;
import hunga.lukeslog.de.support.Logger;

public class RecipieList extends Activity {

    public static final String TAG = HungaConstants.TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipielist);

        List<SearchResult> searchresults = new ArrayList<SearchResult>();
        searchresults = new Select().from(SearchResult.class).orderBy("phe DESC").execute();
        ListView listViewWithRecipies = (ListView) findViewById(R.id.listView);
        RecipieAdapter adapter = new RecipieAdapter(this);
        listViewWithRecipies.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listViewWithRecipies.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
