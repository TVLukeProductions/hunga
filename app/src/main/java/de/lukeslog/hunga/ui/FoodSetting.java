package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.query.Select;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.FoodCombination;
import de.lukeslog.hunga.model.FoodCombinationHelper;
import de.lukeslog.hunga.model.Ingredient;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;

public class FoodSetting extends Activity {

    public static final String TAG = HungaConstants.TAG;
    Activity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailedfoodsettings);
        ctx = this;

        String uid = getIntent().getStringExtra("proposaluid");
        final String foodbarcode = getIntent().getStringExtra("foodbarcode");
        final boolean fixed = (getIntent().getStringExtra("fixed")).equals("1");
        final Proposal proposal = new Select().from(Proposal.class).where("uid = ?", uid).executeSingle();
        final Ingredient ingredient = getSearchedIngredient(foodbarcode, proposal);
        if(ingredient.getFood().getIsItemGood()) {
            TextView header = (TextView) findViewById(R.id.header);
            header.setText(ingredient.getFood().getBaseUnit());
        }
        final EditText editText = (EditText) findViewById(R.id.foodweight);
        editText.setText(""+Math.round(ingredient.getAmount()));
        Button foodweightButton = (Button) findViewById(R.id.foodweightbutton);
        foodweightButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Logger.d(TAG, "CLICK...");
                double oldamount = ingredient.getAmount();
                double factorNew = Double.valueOf(editText.getEditableText().toString())/oldamount;
                ingredient.setAmount(Double.valueOf(editText.getEditableText().toString()));
                ingredient.save();
                if(fixed) {
                    List<Ingredient> ingredients = FoodCombinationHelper.getListOfIngredients(proposal);
                    for(int j=0; j<ingredients.size(); j++) {
                        if(ingredient.getFood().getBarcodeForUse() != ingredients.get(j).getFood().getBarcodeForUse()) {
                            double otheringredientOldAmount = ingredients.get(j).getAmount();
                            ingredients.get(j).setAmount(otheringredientOldAmount*factorNew);
                            if(ingredients.get(j).getFood().getIsItemGood()) {
                                double a = otheringredientOldAmount*factorNew;
                                double a2 = a * 2;
                                int a2i = (int) a2;
                                double anew = ((double) a2i)/2.0;
                                ingredients.get(j).setAmount(anew);
                            }
                            ingredients.get(j).save();
                        }
                    }
                }
                ingredient.save();
                proposal.save();
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                ctx.finish();
            }
        });
    }

    private Ingredient getSearchedIngredient(String foodbarcode, Proposal proposal) {
        for(int i=0; i< FoodCombinationHelper.getListOfIngredients(proposal).size(); i++) {
            Ingredient a = FoodCombinationHelper.getListOfIngredients(proposal).get(i);
            if(a.getFood().getBarcodeForUse().equals(foodbarcode)) {
                return a;
            }
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
