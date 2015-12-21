package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.query.Select;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.model.Recipe;
import de.lukeslog.hunga.model.RecipeHelper;
import de.lukeslog.hunga.rest.RestService;
import de.lukeslog.hunga.support.HungaConstants;

public class StoreActivity extends Activity {

    public static final String TAG = HungaConstants.TAG;
    Activity ctx;

    Proposal proposal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storerecipe);
        ctx = this;
        String uid = getIntent().getStringExtra("proposaluid");
        proposal = new Select().from(Proposal.class).where("uid = ?", uid).executeSingle();

        final EditText recipeNameLabel = (EditText) findViewById(R.id.recipeName);
        final EditText howMany = (EditText) findViewById(R.id.howMany);

        TextView addToDatabase = (TextView) findViewById(R.id.addToDatabase);
        addToDatabase.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String recipeName = recipeNameLabel.getEditableText().toString();
                int hm = Integer.parseInt(howMany.getEditableText().toString());

                proposal.setName(recipeName);
                proposal.setForPersons(hm);
                proposal.save();

                Recipe recipe = RecipeHelper.fillFromFoodCombination(proposal);
                RestService.submitRecipe(recipe);
                ctx.finish();
            }
        });
    }
}
