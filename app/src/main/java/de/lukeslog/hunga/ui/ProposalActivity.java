package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.FoodCombinationHelper;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;

public class ProposalActivity extends Activity{

    public static final String TAG = HungaConstants.TAG;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proposal);
        ctx = this;
        String name = getIntent().getStringExtra("name");
        Logger.d(TAG, "name.>" + name);
        Proposal proposal = new Select().from(Proposal.class).where("name = ?", name).executeSingle();
        if(proposal!=null) {
            TextView nameText = (TextView) findViewById(R.id.propname);
            nameText.setText(proposal.getName());
        }
        fillIngredients(proposal);
    }

    private void fillIngredients(Proposal proposal) {
        LinearLayout ingredientView = (LinearLayout) findViewById(R.id.ingredientlayout);
        for(Food ingredient : FoodCombinationHelper.getListOfIngredients(proposal)) {
            TextView ingredientText = new TextView(this);
            ingredientText.setText(ingredient.getName());
            ingredientView.addView(ingredientText);
        }
    }

}
