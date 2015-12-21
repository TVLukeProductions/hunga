package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.activeandroid.query.Select;

import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;

public class AllFoodsList extends Activity {

    public static final String TAG = HungaConstants.TAG;
    Context ctx;
    private List<Food> foodlist;
    FoodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allfoodslist);

        ctx = this;
        foodlist = new Select().from(Food.class).orderBy("favorit DESC, name COLLATE NOCASE ASC").execute();
        final ListView listViewWithFoods = (ListView) findViewById(R.id.listwithfoods);
        final Proposal proposal = new Proposal();
        adapter = new FoodAdapter(this, foodlist, proposal);

        listViewWithFoods.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listViewWithFoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ctx, FoodActivity.class);
                intent.putExtra("barcodeForUse", foodlist.get(position).getBarcodeForUse());
                startActivity(intent);
            }
        });

        EditText searchfield = (EditText) findViewById(R.id.searchForFood);
        searchfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Logger.d(TAG, "s="+s);
                if(s.equals("")) {
                    foodlist = new Select().from(Food.class).orderBy("name ASC").execute();
                } else {
                    foodlist = new Select().from(Food.class).where("name LIKE '%" + s + "%' OR equivalenceGroup LIKE '%" + s + "%'").orderBy("name ASC").execute();
                }
                final FoodAdapter adapter = new FoodAdapter((Activity) ctx, foodlist, proposal);

                listViewWithFoods.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.allfoodlist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_scan) {
            startScanActivity();
        }
        if (id == R.id.action_settings) {
            startSettingsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startScanActivity() {
        Intent i = new Intent(ctx, Scan.class);
        startActivity(i);
    }

    private void startSettingsActivity() {
        final Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
