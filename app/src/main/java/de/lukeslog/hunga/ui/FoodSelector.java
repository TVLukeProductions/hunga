package de.lukeslog.hunga.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;

import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.support.HungaConstants;

public class FoodSelector extends Activity {

    Activity ctx;
    private String TAG = HungaConstants.TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productselector);
        ctx = this;
        String barcode = getIntent().getStringExtra("barcode");
        final List<Food> foods = new Select().from(Food.class).where("barcode = ?", barcode).orderBy("name COLLATE NOCASE ASC").execute();
        ListView listView = (ListView) findViewById(R.id.productList);
        FoodAdapter adapter = new FoodAdapter(ctx, foods, new Proposal());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "for result...");
                Food food = foods.get(position);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("barcode",food.getBarcodeForUse());
                setResult(RESULT_OK, returnIntent);
                ctx.finish();
            }
        });
    }

}
