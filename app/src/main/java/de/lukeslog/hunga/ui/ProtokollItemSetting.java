package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.FoodCombinationHelper;
import de.lukeslog.hunga.model.Ingredient;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.model.ProtokollItem;
import de.lukeslog.hunga.rest.RestService;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;

public class ProtokollItemSetting extends Activity {

    public static final String TAG = HungaConstants.TAG;
    Activity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailedfoodsettings);
        ctx = this;

        final String barcodeForUse = getIntent().getStringExtra("barcodeForUse");
        final String userAcc = getIntent().getStringExtra("userAcc");
        final double amount = getIntent().getDoubleExtra("amount", 0.0);
        final long timestamp = getIntent().getLongExtra("timestamp", 0l);

        final EditText editText = (EditText) findViewById(R.id.foodweight);
        editText.setText(""+amount);
        Button foodweightButton = (Button) findViewById(R.id.foodweightbutton);
        foodweightButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Logger.d(TAG, "-->");
                double newAmount = Double.parseDouble(editText.getEditableText().toString());
                RestService.correctScan(timestamp, userAcc, barcodeForUse, newAmount);
                ProtokollItem item = new Select().from(ProtokollItem.class).where("timestamp = ?", timestamp).executeSingle();
                Logger.d(TAG, item.getBarcodeForUse());
                item.setAmount(newAmount);
                item.save();
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
