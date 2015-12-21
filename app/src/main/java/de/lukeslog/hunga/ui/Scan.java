package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.FoodGroup;
import de.lukeslog.hunga.rest.RestService;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.HungaUtils;
import de.lukeslog.hunga.support.Logger;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scan extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    private String TAG = HungaConstants.TAG;
    private Activity ctx;
    String proposalName = null;

    private int RESULT_FOR_PRODUCT_SELECTION = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        proposalName = getIntent().getStringExtra("proposal");
        boolean useScan = getIntent().getBooleanExtra("useScan", true);
        Logger.d(TAG, "PROPOSAL NAME?"+proposalName);
        if(useScan) {
            setContentView(mScannerView);                // Set the scanner view as the content view
            startScanner();
        } else {
            setContentView(R.layout.activity_scan);
            String randombarcode = HungaUtils.randomString();
            handleBarcode(randombarcode);
        }
    }

    private void startScanner() {
        setContentView(mScannerView);                // Set the scanner view as the content view
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        try {
            mScannerView.startCamera();          // Start camera on resume
        } catch(RuntimeException rte) {

        }
    }

    @Override
    public void handleResult(Result rawResult) {
        setContentView(R.layout.activity_scan);
        final String barcode = rawResult.getText();

        playBeep();
        
        prefillDataIfPossible();

        handleBarcode(barcode);
    }

    private void handleBarcode(String barcode) {
        if (isProductWithBarcodeInDatabase(barcode)) {
            Logger.d(TAG, "is already in the db!");
            selectProduct(barcode);
        } else {
            Logger.d(TAG, "not yet in the db!");
            registerFood(barcode);
        }
    }

    private void prefillDataIfPossible() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            handleActivityResult(requestCode, data);
        }
    }

    private void handleActivityResult(int requestCode, Intent data) {
        Log.d(TAG, "------->" + requestCode);
        if (requestCode == RESULT_FOR_PRODUCT_SELECTION) {
            String barcode = data.getStringExtra("barcode");
            Log.d(TAG, barcode);
            openFoodActivity(barcode);
        }
    }

    private void openFoodActivity(String barcode) {
        if(proposalName != null) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("barcode", barcode);
            setResult(RESULT_OK, returnIntent);
        } else {
            Food food = getFoodFromBarcodeForUse(barcode);
            Intent intent = new Intent(ctx, FoodActivity.class);
            intent.putExtra("barcodeForUse", food.getBarcodeForUse());
            startActivity(intent);
        }
        this.finish();
    }

    private Food getFoodFromBarcodeForUse(String barcode) {
        Food product = new Select().from(Food.class).where("barcodeForUse = ?", barcode).executeSingle();
        return product;
    }

    private void selectProduct(String barcode) {
        Log.d(TAG, "selectProduct...");
        if(multipleResultsForBarcode(barcode)) {
            Log.d(TAG, "multiple times in db...");
            startActivityToSelectOneProduct(barcode);
        } else {
            Log.d(TAG, "just one in db...");
            openFoodActivity(barcode);
        }

    }

    private void registerFood(String barcode) {

        initializeFoodGroupSpinner();
        initializeItemTypeSpinner();

        addaptIfItem();
        adaptIfSolid();

        adaptBasedOnType();

        final EditText productName = (EditText) findViewById(R.id.productname);
        final CheckBox isItemCheckBox = (CheckBox) findViewById(R.id.itemGoodCheckBox);
        final CheckBox isSolidCheckBox = (CheckBox) findViewById(R.id.solidFoodCheckBox);
        final EditText weightperItemEditText = (EditText) findViewById(R.id.weightperItem);
        final Spinner foodGroupSpinner = (Spinner) findViewById(R.id.foodGroup);

        final Spinner baseUnitSpinner = (Spinner) findViewById(R.id.itemTypeSpinner);
        final EditText weightPerItemEditText = (EditText) findViewById(R.id.weightperItem);

        final EditText kcal100E = (EditText) findViewById(R.id.kcalper100g);
        final EditText fat100E = (EditText) findViewById(R.id.fatper100g);
        final EditText satfat100E = (EditText) findViewById(R.id.satfatper100g);
        final EditText carbo100E = (EditText) findViewById(R.id.carboper100g);
        final EditText sugar100E = (EditText) findViewById(R.id.sugarper100g);
        final EditText protein100E = (EditText) findViewById(R.id.proteinper100g);
        final EditText salt100E = (EditText) findViewById(R.id.saltper100g);

        final CheckBox addSugar = (CheckBox) findViewById(R.id.additionalSugar);
        final CheckBox unprocessed = (CheckBox) findViewById(R.id.unprocessedFood);
        final CheckBox continsCaffein = (CheckBox) findViewById(R.id.containsCaffein);
        final CheckBox contaisnAlc = (CheckBox) findViewById(R.id.containsAlcohol);
        final CheckBox rennetAlt = (CheckBox) findViewById(R.id.rennetAlt);

        String barcodeForUse = getIntent().getStringExtra("barcodeForUse");
        if(barcodeForUse == null || barcodeForUse.equals("")) {
            barcodeForUse = barcode;
        }
        String actualbarcode = getIntent().getStringExtra("actBarcode");
        if(actualbarcode != null && !actualbarcode.equals("")) {
            barcode = actualbarcode;
        }
        productName.setText(getIntent().getStringExtra("productName"));
        isItemCheckBox.setChecked(getIntent().getBooleanExtra("isItemGood", false));
        isSolidCheckBox.setChecked(getIntent().getBooleanExtra("solidFood", false));
        kcal100E.setText(getIntent().getStringExtra("kcalper100"));
        fat100E.setText(getIntent().getStringExtra("fatper100"));
        satfat100E.setText(getIntent().getStringExtra("satfatper100"));
        carbo100E.setText(getIntent().getStringExtra("carboper100"));
        sugar100E.setText(getIntent().getStringExtra("sugarper100"));
        protein100E.setText(getIntent().getStringExtra("proteinper100"));
        salt100E.setText(getIntent().getStringExtra("saltper100"));

        if(getIntent().getStringExtra("foodGroup") != null && !getIntent().getStringExtra("foodGroup").equals("")) {
            for (int i = 0; i < foodGroupSpinner.getAdapter().getCount(); i++) {
                String fname = FoodGroup.getFoodGroupByFoodGroupName(getIntent().getStringExtra("foodGroup")).getFoodGroupPrintName();
                if (foodGroupSpinner.getAdapter().getItem(i).toString().equals(fname)) {
                    foodGroupSpinner.setSelection(i);
                }
            }
        }

        if(getIntent().getStringExtra("baseUnit")!=  null && !getIntent().getStringExtra("baseUnit").equals("")) {
            for (int i = 0; i < baseUnitSpinner.getAdapter().getCount(); i++) {
                if (baseUnitSpinner.getAdapter().getItem(i).toString().equals(getIntent().getStringExtra("baseUnit"))) {
                    baseUnitSpinner.setSelection(i);
                }
            }
        }

        weightPerItemEditText.setText(getIntent().getStringExtra("weightPerItem"));

        setBarcodeLabel(barcode);

        final TextView addToDatabase = (TextView) findViewById(R.id.addToDatabase);
        final String finalBarcodeForUse = barcodeForUse;
        final String finalBarcode = barcode;
        addToDatabase.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                String name = productName.getEditableText().toString();
                boolean isItem = isItemCheckBox.isChecked();
                String weightperItem = weightperItemEditText.getEditableText().toString();

                if(!name.equals("") && (!isItem || !weightperItem.equals(""))) {
                    Food f = new Food();
                    f.setBarcode(finalBarcode);
                    f.setBarcodeForUse(finalBarcodeForUse);
                    f.setName(name);
                    final FoodGroup foodGroup = FoodGroup.getFoodGroupByFoodGroupPrintName(foodGroupSpinner.getSelectedItem().toString());
                    f.setFoodGroup(foodGroup.getFoodGroupName());
                    f.setIsItemGood(isItemCheckBox.isChecked());
                    if(isItemCheckBox.isChecked()) {
                        f.setBaseUnit(baseUnitSpinner.getSelectedItem().toString());
                        f.setBasisMenge(1.0);
                        f.setWeightPerServing(Double.parseDouble(weightPerItemEditText.getEditableText().toString()));
                    } else {
                        if(!isSolidCheckBox.isChecked()) {
                            f.setBaseUnit("g");
                        } else {
                            f.setBaseUnit("ml");
                        }
                        f.setBasisMenge(100.0);
                        f.setWeightPerServing(100.0);
                    }
                    f.setSolid(!isSolidCheckBox.isChecked());
                    if(!kcal100E.getEditableText().toString().equals("")) {
                        f.setKcal100(Double.parseDouble(kcal100E.getEditableText().toString()));
                    }
                    if(!fat100E.getEditableText().toString().equals("")) {
                        f.setFat100(Double.parseDouble(fat100E.getEditableText().toString()));
                    }
                    if(!satfat100E.getEditableText().toString().equals("")) {
                        f.setSaturatedFattyAcids100(Double.parseDouble(satfat100E.getEditableText().toString()));
                    }
                    if(!carbo100E.getEditableText().toString().equals("")) {
                        f.setCarbohydrate100(Double.parseDouble(carbo100E.getEditableText().toString()));
                    }
                    if(!sugar100E.getEditableText().toString().equals("")) {
                        f.setSugarInCarbohydrate100(Double.parseDouble(sugar100E.getEditableText().toString()));
                    }
                    if(!protein100E.getEditableText().toString().equals("")) {
                        f.setProtein100(Double.parseDouble(protein100E.getEditableText().toString()));
                    }
                    if(!salt100E.getEditableText().toString().equals("")) {
                        f.setSalt100(Double.parseDouble(salt100E.getEditableText().toString()));
                    }
                    f.setAdditionalSugar(addSugar.isChecked());
                    f.setUnproccessed(unprocessed.isChecked());
                    f.setContainsCaffein(continsCaffein.isChecked());
                    f.setContainsAlcohol(contaisnAlc.isChecked());
                    f.setLabaustauschstoff(rennetAlt.isChecked());
                    double calculatedPheValue = -1;
                    if(protein100E.getEditableText().toString() != null && !protein100E.getEditableText().toString().equals("")) {
                        double phefactor = 50;
                        if (foodGroup == FoodGroup.VEGETABLES) {
                            phefactor = 30;
                        } else if (foodGroup == FoodGroup.FRUIT) {
                            phefactor = 40;
                        }
                        calculatedPheValue = phefactor * Double.parseDouble(protein100E.getEditableText().toString());
                    }
                    f.setPhe100(calculatedPheValue);
                    f.save();
                    RestService.submitNewFood(f);
                    openFoodActivity(finalBarcode);
                } else {
                    Toast.makeText(ctx, "Angaben unvollständig", Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void adaptBasedOnType() {
        final Spinner itemTypeSpinner = (Spinner) findViewById(R.id.itemTypeSpinner);
        final TextView weightPerItemLabel = (TextView) findViewById(R.id.weightPerItemLabel);
        String itemType = itemTypeSpinner.getSelectedItem().toString();
        weightPerItemLabel.setText("Gewicht pro "+itemType);

        itemTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemType = itemTypeSpinner.getSelectedItem().toString();
                weightPerItemLabel.setText("Gewicht pro "+itemType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void adaptIfSolid() {
        final TextView auf100 = (TextView) findViewById(R.id.hundertg);
        auf100.setText("Auf 100 g");
        CheckBox isItemCheckBox = (CheckBox) findViewById(R.id.solidFoodCheckBox);
        isItemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    auf100.setText("Auf 100 ml");
                } else {
                    auf100.setText("Auf 100 g");
                }
            }
        });
    }

    private boolean multipleResultsForBarcode(String barcode) {
        return getProductsFromBarcode(barcode).size()>1;
    }

    private boolean isProductWithBarcodeInDatabase(String barcode) {
        return !getProductsFromBarcode(barcode).isEmpty();
    }

    private void startActivityToSelectOneProduct(String barcode) {
        Intent i = new Intent(ctx, FoodSelector.class);
        i.putExtra("barcode", ""+barcode);
        Log.d(TAG, "start Activity for result...");
        startActivityForResult(i, RESULT_FOR_PRODUCT_SELECTION);
    }

    private List<Food> getProductsFromBarcode(String barcode) {
        Log.d(TAG, "already in the system?"+barcode);
        List<Food> products = new Select().from(Food.class).where("barcode = ?", barcode).execute();
        Logger.d(TAG, ""+products.size());
        return products;
    }

    private void playBeep() {
        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        switch( audio.getRingerMode() ){
            case AudioManager.RINGER_MODE_NORMAL:
                playBeepViaSound();
                break;
            case AudioManager.RINGER_MODE_SILENT:
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                playBeepViwVibrate();
                break;
        }
    }

    private void playBeepViwVibrate() {
        final Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        new Thread(new Runnable() {
            public void run() {
                v.vibrate(500);
            }
        }).start();
    }

    private void playBeepViaSound() {
        new Thread(new Runnable() {
            public void run() {
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
            }
        }).start();
    }

    private void addaptIfItem() {
        final TextView itemTypeLabel = (TextView) findViewById(R.id.itemTypeLabel);
        final Spinner itemTypeSpinner = (Spinner) findViewById(R.id.itemTypeSpinner);
        final TextView weightPerItemLabel = (TextView) findViewById(R.id.weightPerItemLabel);
        final TextView compulsory2 = (TextView) findViewById(R.id.compulsory2);
        final EditText weightperItem = (EditText) findViewById(R.id.weightperItem);

        itemTypeLabel.setVisibility(View.GONE);
        itemTypeSpinner.setVisibility(View.GONE);
        weightPerItemLabel.setVisibility(View.GONE);
        compulsory2.setVisibility(View.GONE);
        weightperItem.setVisibility(View.GONE);

        CheckBox isItemCheckBox = (CheckBox) findViewById(R.id.itemGoodCheckBox);
        isItemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    itemTypeLabel.setVisibility(View.VISIBLE);
                    itemTypeSpinner.setVisibility(View.VISIBLE);
                    weightPerItemLabel.setVisibility(View.VISIBLE);
                    compulsory2.setVisibility(View.VISIBLE);
                    weightperItem.setVisibility(View.VISIBLE);
                } else {
                    itemTypeLabel.setVisibility(View.GONE);
                    itemTypeSpinner.setVisibility(View.GONE);
                    weightPerItemLabel.setVisibility(View.GONE);
                    compulsory2.setVisibility(View.GONE);
                    weightperItem.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initializeItemTypeSpinner() {
        Logger.d(TAG, "INITIALIZE SPINNER");
        Spinner itemTypeSpinner = (Spinner) findViewById(R.id.itemTypeSpinner);
        List<Food> foods = new Select()
                .from(Food.class)
                .orderBy("baseUnit ASC")
                .execute();
        List<String> itemTypes = new ArrayList<>();
        Logger.d(TAG, ""+foods.size());
        for(int i=0; i<foods.size(); i++) {
            if(foods.get(i).getBaseUnit() != null
                    && !itemTypes.contains(foods.get(i).getBaseUnit())) {
                itemTypes.add(foods.get(i).getBaseUnit());
                Logger.d(TAG, "->" + foods.get(i).getBaseUnit());
            }
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemTypes);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemTypeSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void setBarcodeLabel(String barcode) {
        TextView barcodelabel = (TextView) findViewById(R.id.barcodecontetlabel);
        barcodelabel.setText(barcode);
    }

    private void initializeFoodGroupSpinner() {
        Spinner foodGroupsSpinner = (Spinner) findViewById(R.id.foodGroup);
        String[] foodGroupNames = FoodGroup.getFoodGroupPrintNameArray();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, foodGroupNames);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodGroupsSpinner.setAdapter(spinnerArrayAdapter);
    }
}