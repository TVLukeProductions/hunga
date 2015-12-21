package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.chromecast.ChromecastService;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.FoodGroup;
import de.lukeslog.hunga.model.Ingredient;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.rest.RestService;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.HungaUtils;
import de.lukeslog.hunga.support.Logger;

public class FoodActivity extends Activity {
    public static final String TAG = HungaConstants.TAG;
    Activity ctx;
    Food food;
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foodactivity);
        ctx = this;
        String barcodeForUse = getIntent().getStringExtra("barcodeForUse");
        food = new Select().from(Food.class).where("barcodeForUse = ?", barcodeForUse).executeSingle();
        TextView nameText = (TextView) findViewById(R.id.foodname);
        nameText.setText(food.getName());

        ImageView iv = (ImageView) findViewById(R.id.foodGroupImage);
        FoodGroup foodGroup = FoodGroup.getFoodGroupByFoodGroupName(food.getFoodGroup());
        iv.setImageResource(foodGroup.getIcon());

        if(!food.isContainsAlcohol()) {
            LinearLayout alcLinear = (LinearLayout) findViewById(R.id.alcoholic);
            alcLinear.setVisibility(View.GONE);
        }

        if(!food.isContainsCaffein()) {
            LinearLayout cafLinear = (LinearLayout) findViewById(R.id.caffein);
            cafLinear.setVisibility(View.GONE);
        }

        setPortionDetails(food);

        fillTable(food);

        final TextView omnomnom = (TextView) findViewById(R.id.omnomnom);
        omnomnom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(ctx);
                String accid = defsettings.getString("googleAccId", "");
                if(food.getIsItemGood()) {
                    RestService.submitEatenFood(food, 1, "", accid);
                } else {
                    RestService.submitEatenFood(food, 100, "", accid);
                }
                ctx.finish();
            }
        });

        final TextView startRec = (TextView) findViewById(R.id.startRec);
        startRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, ProposalActivity.class);
                String proposalName = food.getName();
                Proposal newProposal = new Proposal();
                newProposal.setName(proposalName);

                Ingredient newIngredient = new Ingredient();
                newIngredient.setFood(food);
                if(food.getIsItemGood()) {
                    newIngredient.setAmount(1);
                } else {
                    newIngredient.setAmount(100);
                }
                newIngredient.setFoodCombinationId(newProposal.getUid());
                newIngredient.save();

                newProposal.save();
                intent.putExtra("proposaluid", newProposal.getUid());
                startActivity(intent);
                ctx.finish();
            }
        });
    }

    private void fillTable(Food food) {


        TextView kcalValue = (TextView) findViewById(R.id.kcalvalue);
        TextView kcalValueportion = (TextView) findViewById(R.id.kcalvalueportion);
        //TODO: check for negative
        kcalValue.setText(""+df.format(food.getKcal100()));
        kcalValueportion.setText(""+df.format((food.getKcal100()/100*food.getWeightPerServing())));

        TextView fatValue = (TextView) findViewById(R.id.fatvalue);
        TextView fatValuePortion = (TextView) findViewById(R.id.fatvaluePortion);
        //TODO: check for negative
        fatValue.setText(""+df.format(food.getFat100()));
        fatValuePortion.setText(""+df.format((food.getFat100()/100*food.getWeightPerServing())));

        TextView satFatvalue = (TextView) findViewById(R.id.satFatvalue);
        TextView satFatvaluePortion = (TextView) findViewById(R.id.satfatvaluePortion);
        if(food.getSaturatedFattyAcids100()>-0.1) {
            satFatvalue.setText("" + df.format(food.getSaturatedFattyAcids100()));
            satFatvaluePortion.setText("" + df.format((food.getSaturatedFattyAcids100() / 100 * food.getWeightPerServing())));
        } else {
            satFatvalue.setText("-");
            satFatvaluePortion.setText("-");
        }

        TextView carbvalue = (TextView) findViewById(R.id.carbvalue);
        TextView carbvaluePortion = (TextView) findViewById(R.id.carbvaluePortion);
        if(food.getCarbohydrate100()>-0.1) {
            carbvalue.setText("" + df.format(food.getCarbohydrate100()));
            carbvaluePortion.setText("" + df.format((food.getCarbohydrate100() / 100 * food.getWeightPerServing())));
        } else {
            carbvalue.setText("-");
            carbvaluePortion.setText("-");
        }

        TextView sugarvalue = (TextView) findViewById(R.id.sugarvalue);
        TextView sugarvaluePortion = (TextView) findViewById(R.id.sugarvaluePortion);
        if(food.getSugarInCarbohydrate100()>-0.1) {
            sugarvalue.setText("" + df.format(food.getSugarInCarbohydrate100()));
            sugarvaluePortion.setText("" + df.format((food.getSugarInCarbohydrate100() / 100 * food.getWeightPerServing())));
        } else {
            sugarvalue.setText("-");
            sugarvaluePortion.setText("-");
        }

        TextView proteinvalue = (TextView) findViewById(R.id.proteinvalue);
        TextView proteinvalueProtion = (TextView) findViewById(R.id.proteinvalueportion);
        //TODO: check for negative
        proteinvalue.setText(""+df.format(food.getProtein100()));
        proteinvalueProtion.setText(""+df.format((food.getProtein100()/100*food.getWeightPerServing())));

        TextView saltvalue = (TextView) findViewById(R.id.saltvalue);
        TextView saltvaluePortion = (TextView) findViewById(R.id.saltvalueportion);
        //TODO: check for negative
        saltvalue.setText(""+df.format(food.getSalt100()));
        saltvaluePortion.setText(""+df.format((food.getSalt100()/100*food.getWeightPerServing())));

        TextView phelabel = (TextView) findViewById(R.id.phelabel);
        TextView phevalue = (TextView) findViewById(R.id.phevalue);
        TextView phevaluePortion = (TextView) findViewById(R.id.phevalueportion);
        //TODO: check for negative
        phevalue.setText(""+df.format(food.getPhe100()));
        phevaluePortion.setText(""+df.format((food.getPhe100()/100*food.getWeightPerServing())));
        if(food.isPheValueApprox()) {
            phelabel.setText(Html.fromHtml("phe<sup><small>2</small></sup>"));
            TextView footNote = (TextView) findViewById(R.id.footnote2);
            footNote.setText(Html.fromHtml("<sup><small>2</small></sup> Schätzwert"));
        }
    }

    private void setPortionDetails(Food food) {
        String unit = HungaUtils.getUnit(food);
        TextView portion = (TextView) findViewById(R.id.portion);
        TextView hundertg = (TextView) findViewById(R.id.hundertg);
        portion.setText(Html.fromHtml(((int)food.getBasisMenge())+" "+food.getBaseUnit()+"<sup><small>1</small></sup>"));
        hundertg.setText("100 "+unit);
        TextView footNote = (TextView) findViewById(R.id.footnote);
        footNote.setText(Html.fromHtml("<sup><small>1</small></sup>"+food.getWeightPerServing()+ " "+unit));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.foodactivitymenu, menu);
        if(food.isFav()) {
            MenuItem action_star = menu.findItem(R.id.action_star);
            action_star.setIcon(R.drawable.ic_action_star_10);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_star) {
            if(!food.isFav()) {
                food.setFav(true);
                food.save();
                SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(ctx);
                String accid = defsettings.getString("googleAccId", "");
                RestService.addToFavorites(food, accid);
            } else {
                food.setFav(false);
                food.save();
                SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(ctx);
                String accid = defsettings.getString("googleAccId", "");
                RestService.removeFromFavorites(food, accid);
            }
            invalidateOptionsMenu();
        }
        if (id == R.id.new_food_with_same_barcode) {
            Intent intent = new Intent(this, Scan.class);
            intent.putExtra("useScan", false);
            intent.putExtra("productName", food.getName());
            intent.putExtra("actBarcode", food.getBarcode());
            intent.putExtra("barcodeForUse", food.getBarcodeForUse()+"_"+new Date().getTime());
            intent.putExtra("isItemGood", food.getIsItemGood());
            intent.putExtra("solidFood", !food.isSolid());
            intent.putExtra("kcalper100", ""+df.format(food.getKcal100()));
            intent.putExtra("fatper100", ""+df.format(food.getFat100()));
            intent.putExtra("satfatper100", ""+df.format(food.getSaturatedFattyAcids100()));
            intent.putExtra("carboper100", ""+df.format(food.getCarbohydrate100()));
            intent.putExtra("sugarper100", ""+df.format(food.getSugarInCarbohydrate100()));
            intent.putExtra("proteinper100", ""+df.format(food.getProtein100()));
            intent.putExtra("saltper100", ""+df.format(food.getSalt100()));
            intent.putExtra("foodGroup", food.getFoodGroup());
            intent.putExtra("baseUnit", food.getBaseUnit());
            intent.putExtra("weightPerItem", ""+df.format(food.getWeightPerServing()));

            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
