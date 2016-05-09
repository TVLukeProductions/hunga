package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.FoodCombinationHelper;
import de.lukeslog.hunga.model.Ingredient;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.model.ProposalHelper;
import de.lukeslog.hunga.model.ProposalHelperWeight;
import de.lukeslog.hunga.rest.RestService;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;

public class ProposalWeightActivity extends Activity {

    public static final String TAG = HungaConstants.TAG;
    Activity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailedfoodsettings);
        ctx = this;

        String uid = getIntent().getStringExtra("proposaluid");
        final String accid = getIntent().getStringExtra("accid");
        final Proposal proposal = new Select().from(Proposal.class).where("uid = ?", uid).executeSingle();
        double weight = proposal.getWeight();
        final EditText editText = (EditText) findViewById(R.id.foodweight);
        editText.setText(""+Math.round(weight));

        final DatePicker datepicker = (DatePicker) findViewById(R.id.fooddate);

        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        final LinearLayout datailsspace = (LinearLayout) findViewById(R.id.datailsspace);
        datailsspace.setVisibility(View.GONE);

        final CheckBox detailsCheckBox = (CheckBox) findViewById(R.id.detailsCheckBox);
        detailsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    datailsspace.setVisibility(View.VISIBLE);
                } else {
                    datailsspace.setVisibility(View.GONE);
                }
            }
        });

        Button foodweightButton = (Button) findViewById(R.id.foodweightbutton);
        foodweightButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Logger.d(TAG, "CLICK...");
                Double goalweight = Double.valueOf(editText.getEditableText().toString());


                Calendar calendar = new GregorianCalendar(datepicker.getYear(), datepicker.getMonth(), datepicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), timePicker.getCurrentMinute());
                long date = calendar.getTimeInMillis();

                ProposalHelperWeight proposalHelper = new ProposalHelperWeight();
                double factorNew = proposalHelper.getFactorForGoal(proposal, goalweight, true);
                List<Ingredient> ingredients = FoodCombinationHelper.getListOfIngredients(proposal);
                for(int j=0; j<ingredients.size(); j++) {
                    ingredients.get(j).setAmount(Math.round(ingredients.get(j).getAmount()*factorNew*100.0)/100.0);
                    ingredients.get(j).save();
                }
                proposal.save();
                RestService.submitEatenProposal(proposal, accid, date);
                showToast();
                ctx.finish();
            }
        });
    }

    private void showToast() {
        Context context = getApplicationContext();
        CharSequence text = "Abgeschickt";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
