package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.FoodCombinationHelper;
import de.lukeslog.hunga.model.Ingredient;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.model.ProposalHelperWeight;
import de.lukeslog.hunga.rest.RestService;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;

public class FoodWeightActivity extends Activity {

    public static final String TAG = HungaConstants.TAG;
    Activity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailedfoodsettings);
        ctx = this;

        String barcodeForUse = getIntent().getStringExtra("barcodeForUse");
        final String accid = getIntent().getStringExtra("accid");
        final Food food = new Select().from(Food.class).where("barcodeForUse = ?", barcodeForUse).executeSingle();
        double weight = 100;
        if (food.getIsItemGood()) {
            weight = 1;
        }
        final EditText editText = (EditText) findViewById(R.id.foodweight);
        editText.setText("" + Math.round(weight));

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
                Double goalamount = Double.valueOf(editText.getEditableText().toString());

                Calendar calendar = new GregorianCalendar(datepicker.getYear(), datepicker.getMonth(), datepicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), timePicker.getCurrentMinute());
                long date = calendar.getTimeInMillis();

                Date aDate = new Date(date);
                Log.d(TAG, ""+aDate);

                RestService.submitEatenFood(food, goalamount, "", accid, date);
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

    @Override
    public void onStart() {
        super.onStart();
    }
}
