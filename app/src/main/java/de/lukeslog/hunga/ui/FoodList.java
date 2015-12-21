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

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.FoodCombinationHelper;
import de.lukeslog.hunga.model.Ingredient;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.model.ProposalHelper;
import de.lukeslog.hunga.model.Recipe;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;

public class FoodList extends Activity {

    public static final String TAG = HungaConstants.TAG;

    private Activity ctx;
    private List<Food> foodlist;
    private Proposal proposal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foodlist);
        ctx = this;
        foodlist = new Select().from(Food.class).orderBy("name COLLATE NOCASE ASC").execute();
        String name = getIntent().getStringExtra("name");
        Logger.d(TAG, name);
        proposal = new Select().from(Proposal.class).where("name = ?", name).executeSingle();
        final ListView listViewWithFoods = (ListView) findViewById(R.id.listViewfood);
        FoodAdapter adapter = new FoodAdapter(this, foodlist, proposal);
        listViewWithFoods.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listViewWithFoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Ingredient> ingredients = FoodCombinationHelper.getListOfIngredients(proposal);
                Food food = foodlist.get(position);
                if(!ProposalHelper.containsFood(proposal, food)) {
                    int nofingredients = ingredients.size();
                    FoodCombinationHelper.addIngredient(proposal, food);
                    ctx.finish();
                }
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
                    foodlist = new Select().from(Food.class).where("name LIKE '%" + s + "%'").orderBy("name ASC").execute();
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
        getMenuInflater().inflate(R.menu.foodlist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_add) {
            startAddActivity();
        }
        if (id == R.id.action_settings) {
            startSettingsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAddActivity() {
        Intent i = new Intent(ctx, AddChoiceActivity.class);
        i.putExtra("proposal", ""+proposal.getName());
        Log.d(TAG, "start Activity for result...");
        startActivityForResult(i, 61161);
    }

    private void startSettingsActivity() {
        final Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 61161) {
                String barcode = data.getStringExtra("barcode");
                Food food = getFoodFromBarcodeForUse(barcode);
                List<Ingredient> ingredients = FoodCombinationHelper.getListOfIngredients(proposal);
                if (!ProposalHelper.containsFood(proposal, food)) {
                    int nofingredients = ingredients.size();
                    ProposalHelper.addIngredient(proposal, food);
                }
                ctx.finish();
            }
        }
    }

    private Food getFoodFromBarcodeForUse(String barcode) {
        Food product = new Select().from(Food.class).where("barcodeForUse = ?", barcode).executeSingle();
        return product;
    }


}
