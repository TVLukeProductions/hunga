package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.FoodCombinationHelper;
import de.lukeslog.hunga.model.Ingredient;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.model.ProposalHelper;
import de.lukeslog.hunga.model.ProposalHelperKcal;
import de.lukeslog.hunga.model.ProposalHelperWeight;
import de.lukeslog.hunga.rest.Downloader;
import de.lukeslog.hunga.rest.RestService;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;
import de.lukeslog.hunga.support.SupportService;

public class ProposalActivity extends Activity {

    public static final String TAG = HungaConstants.TAG;
    Activity ctx;

    Proposal proposal;
    List<Ingredient> ingredients;
    boolean fixedProportions;
    double proportion;
    ArrayList<SeekBar> foodWeightBars = new ArrayList<SeekBar>();
    private TextToSpeech tts;
    private boolean showCompleteInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proposal);
        ctx = this;

        /*
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int arg0) {
                if(arg0 == TextToSpeech.SUCCESS)
                {
                    Logger.d(TAG, "Ok, trying now...");
                    sayShit();
                }
            }
        });
        */

        String uid = getIntent().getStringExtra("proposaluid");
        Logger.d(TAG, "udi.>" + uid);
        //TODO: should have a random uid
        proposal = new Select().from(Proposal.class).where("uid = ?", uid).executeSingle();
        if(proposal!=null) {
            updatebaseInfo();
        }

        CheckBox fixedProposalCheckBox = (CheckBox) findViewById(R.id.fixedProportionsCheckBox);

        if(proposal.getFixedPropostions()) {
            fixedProportions=true;
            proportion = proposal.getFactor();
            fixedProposalCheckBox.setChecked(true);
        }

        fixedProposalCheckBox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fixedProportions=isChecked;
            }
        });

        LinearLayout topOfTable = (LinearLayout) findViewById(R.id.topOfTable);
        topOfTable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideOrShowTable();
            }
        });
        hideTable();

        fillIngredients();

        if(!ProposalHelper.containsAlcohol(proposal)) {
            LinearLayout alcLinear = (LinearLayout) findViewById(R.id.alcoholic);
            alcLinear.setVisibility(View.GONE);
        }

        if(!ProposalHelper.containsCaffein(proposal)) {
            LinearLayout cafLinear = (LinearLayout) findViewById(R.id.caffein);
            cafLinear.setVisibility(View.GONE);
        }

        final TextView omnomnom = (TextView) findViewById(R.id.omnomnom);
        omnomnom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickedAnimation(omnomnom);
                proposal.save();
                SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(ctx);
                String accid = defsettings.getString("googleAccId", "");
                RestService.submitEatenProposal(proposal, accid, new Date().getTime());
                showToast();
            }
        });

        omnomnom.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                clickedAnimation(omnomnom);
                proposal.save();
                SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(ctx);
                String accid = defsettings.getString("googleAccId", "");
                Intent i = new Intent(ctx, ProposalWeightActivity.class);
                i.putExtra("proposaluid", proposal.getUid());
                i.putExtra("accid", accid);
                startActivity(i);
                return true;
            }
        });

        final TextView storeRec = (TextView) findViewById(R.id.storeRec);
        storeRec.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                proposal.save();
                Intent i = new Intent(ctx, StoreActivity.class);
                i.putExtra("proposaluid", proposal.getUid());
                startActivity(i);
                ctx.finish();
            }
        });
    }

    private void hideOrShowTable() {
        if(showCompleteInfo) {
            hideTable();
        } else {
            showTable();
        }
    }

    private void hideTable() {
        LinearLayout restOfTable = (LinearLayout) findViewById(R.id.restOfTable);
        restOfTable.setVisibility(View.GONE);
        showCompleteInfo = false;
    }

    private void showTable() {
        LinearLayout restOfTable = (LinearLayout) findViewById(R.id.restOfTable);
        restOfTable.setVisibility(View.VISIBLE);
        showCompleteInfo = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.propsal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_cancel) {
            ctx.finish();
        }
        /*
        if (id == R.id.action_cast) {
            //startService(new Intent(this, ChromecastService.class));
        }
        */
        return super.onOptionsItemSelected(item);
    }

    private void updatebaseInfo() {
        Logger.d(TAG, "updateBaseInfo...");
        proposal.save();
        proposal.setKcal(ProposalHelper.calculateKcal(proposal));
        proposal.setPhe(ProposalHelper.calculatePhe(proposal));
        proposal.setFat(ProposalHelper.calculateFat(proposal));
        proposal.setSaturatedFattyAcids(ProposalHelper.calculateSatFat(proposal));
        proposal.setCarbohydrate(ProposalHelper.calculateCarbo(proposal));
        proposal.setSugarInCarbohydrate(ProposalHelper.calculateSugar(proposal));
        proposal.setProtein(ProposalHelper.calculateProtein(proposal));
        proposal.setSalt(ProposalHelper.calculateSalt(proposal));

        proposal.setWeight(new ProposalHelperKcal().calculateWeight(proposal));
        proposal.save();
        TextView nameText = (TextView) findViewById(R.id.propname);
        //TODO: enable name changes...
        nameText.setText(proposal.getName());

        TextView kcalcontent = (TextView) findViewById(R.id.kcalvalueportion);
        kcalcontent.setText(""+Math.round(proposal.getKcal()));

        TextView kcalper100 = (TextView) findViewById(R.id.kcalvalue);
        kcalper100.setText(""+Math.round(proposal.getKcal()/proposal.getWeight()*100));

        TextView fatcontent = (TextView) findViewById(R.id.fatvaluePortion);
        fatcontent.setText(""+Math.round(proposal.getFat()));

        TextView fatper100 = (TextView) findViewById(R.id.fatvalue);
        fatper100.setText(""+Math.round(proposal.getFat()/proposal.getWeight()*100));

        TextView satfatcontent = (TextView) findViewById(R.id.satfatvaluePortion);
        satfatcontent.setText(""+Math.round(proposal.getSaturatedFattyAcids()));

        TextView satfatper100 = (TextView) findViewById(R.id.satFatvalue);
        satfatper100.setText(""+Math.round(proposal.getSaturatedFattyAcids()/proposal.getWeight()*100));

        TextView carbocontent = (TextView) findViewById(R.id.carbvaluePortion);
        carbocontent.setText(""+Math.round(proposal.getCarbohydrate()));

        TextView carboper100 = (TextView) findViewById(R.id.carbvalue);
        carboper100.setText(""+Math.round(proposal.getCarbohydrate()/proposal.getWeight()*100));

        TextView sugarcontent = (TextView) findViewById(R.id.sugarvaluePortion);
        sugarcontent.setText(""+Math.round(proposal.getSugarInCarbohydrate()));

        TextView sugarper100 = (TextView) findViewById(R.id.sugarvalue);
        sugarper100.setText(""+Math.round(proposal.getSugarInCarbohydrate()/proposal.getWeight()*100));

        TextView proteincontent = (TextView) findViewById(R.id.proteinvalueportion);
        proteincontent.setText(""+Math.round(proposal.getProtein()));

        TextView proteinper100 = (TextView) findViewById(R.id.proteinvalue);
        proteinper100.setText(""+Math.round(proposal.getProtein()/proposal.getWeight()*100));

        TextView saltcontent = (TextView) findViewById(R.id.saltvalueportion);
        saltcontent.setText(""+Math.round(proposal.getSalt()));

        TextView saltper100 = (TextView) findViewById(R.id.saltvalue);
        saltper100.setText(""+Math.round(proposal.getSalt()/proposal.getWeight()*100));

        TextView phecontent = (TextView) findViewById(R.id.phevalueportion);
        phecontent.setText(""+Math.round(proposal.getPhe()));

        TextView pheper100 = (TextView) findViewById(R.id.phevalue);
        pheper100.setText(""+Math.round(proposal.getPhe()/proposal.getWeight()*100));

        TextView weightcontent = (TextView) findViewById(R.id.portion);
        weightcontent.setText(""+Math.round(proposal.getWeight())+" g");

        TextView phelabel = (TextView) findViewById(R.id.phelabel);
        if(ProposalHelper.containsPheApprox(proposal)) {
            phelabel.setText(Html.fromHtml("phe<sup><small>1</small></sup>"));
            TextView footNote = (TextView) findViewById(R.id.footnote2);
            footNote.setText(Html.fromHtml("<sup><small>1</small></sup> Schätzwert"));
        }
    }

    private void fillIngredients() {
        foodWeightBars.clear();
        final List<SeekBar> itembars = new ArrayList<>();
        final LinearLayout ingredientView = (LinearLayout) findViewById(R.id.ingredientlayout);
        updateIngredientsAndAmounts(proposal);
        for(int i=0; i<ingredients.size(); i++) {
            final Ingredient ingredient = ingredients.get(i);
            final Food food = ingredients.get(i).getFood();
            final double amount = ingredients.get(i).getAmount();
            final TextView ingredientText = new TextView(this);
            ingredientText.setText(food.getName());
            ingredientText.setTextColor(Color.parseColor("#A8A8A8"));
            ingredientText.setTextSize(30);
            ingredientText.setGravity(Gravity.CENTER);

            final int x = i;
            ingredientText.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    Logger.d(TAG, "LONGCLICK ");
                    Intent intent = new Intent(ctx, FoodSetting.class);
                    intent.putExtra("proposaluid", proposal.getUid());
                    intent.putExtra("foodbarcode", food.getBarcodeForUse());
                    if(fixedProportions) {
                        intent.putExtra("fixed", "1");
                    } else {
                        intent.putExtra("fixed", "0");
                    }
                    startActivityForResult(intent, 12345);
                    return true;
                }
            });

            ingredientView.addView(ingredientText);

            final SeekBar weightbar = new SeekBar(this);
            foodWeightBars.add(weightbar);
            if(food.getIsItemGood()) {
                if(amount > 0.0) {
                    double amountc = amount * 2;
                    weightbar.setMax((int) amountc * 2);
                    weightbar.setProgress((int) amountc);
                } else {
                    weightbar.setMax(20);
                    weightbar.setProgress(0);
                }

            } else {
                if(amount > 0.0) {
                    weightbar.setMax((int) amount * 2);
                    weightbar.setProgress((int) amount);
                } else {
                    weightbar.setMax(100);
                    weightbar.setProgress(0);
                }
            }
            final int finalI = i;

            final TextView ingredientWeight = new TextView(this);
            ingredientWeight.setGravity(Gravity.CENTER);
            updateIngredientWeight(ingredient, amount, ingredientWeight);

            if(ingredient.getFood().getIsItemGood()) {
                itembars.add(weightbar);
            }
            weightbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double proposalAmountPrevious = ingredient.getAmount();
                    if (!ingredient.getFood().getIsItemGood()) {
                        setFoodData(progress, ingredient, finalI, ingredientWeight);
                    } else {

                        double amountprop = progress;
                        amountprop = amountprop / 2;
                        if (amountprop < 0.5) {
                            amountprop = 0.5;
                            weightbar.setProgress(1);
                        }
                        setFoodData(amountprop, ingredient, finalI, ingredientWeight);

                    }
                    if(fixedProportions) {
                        //TODO:

                        double currentamount = (double) progress;
                        if(ingredient.getFood().getIsItemGood()) {
                            currentamount = currentamount / 2.0;
                        }
                        double newFactor = (double)(currentamount)/(double)(proposalAmountPrevious);
                        Logger.d(TAG, "previous amount: "+proposalAmountPrevious);
                        Logger.d(TAG, "currentamount "+currentamount);
                        Logger.d(TAG, "proposal factor "+proposal.getFactor());
                        Logger.d(TAG, "newFactor"+newFactor);
                        fixedProportions=false;
                        for(SeekBar otherSeekBar : foodWeightBars) {
                            if(otherSeekBar != seekBar) {
                                Logger.d(TAG, "OTHER: "+otherSeekBar.getProgress());
                                Logger.d(TAG, "FACTOR"+(double)progress / proposalAmountPrevious);
                                double newprogress = (double)otherSeekBar.getProgress() * (newFactor);
                                if (newprogress > otherSeekBar.getMax()) {
                                    otherSeekBar.setMax((int)newprogress);
                                    newprogress = otherSeekBar.getMax();
                                }
                                otherSeekBar.setProgress((int)newprogress);
                            }
                        }
                        fixedProportions=true;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            ingredientView.addView(weightbar);

            ingredientView.addView(ingredientWeight);

            LinearLayout seperator = new LinearLayout(this);
            seperator.setMinimumHeight(1);
            seperator.setBackgroundColor(Color.parseColor("#99cc00"));
            ingredientView.addView(seperator);
        }
        TextView addFood = new TextView(this);
        addFood.setGravity(Gravity.CENTER);
        addFood.setBackgroundColor(Color.parseColor("#99cc00"));
        addFood.setTextColor(Color.parseColor("#FFFFFF"));
        addFood.setText("Zutat hinzufügen");
        addFood.setTextSize(30);
        addFood.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Logger.d(TAG, "CLICK");
                Intent intent = new Intent(ctx, FoodList.class);
                intent.putExtra("name", proposal.getName());
                startActivity(intent);
            }
        });
        ingredientView.addView(addFood);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 12345) {
            Logger.d(TAG, "onActivityResult....");
            updatebaseInfo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final LinearLayout ingredientView = (LinearLayout) findViewById(R.id.ingredientlayout);
        ingredientView.removeAllViews();
        fillIngredients();
        updatebaseInfo();
    }

    private void setFoodData(double progress, Ingredient ingredient, int finalI, TextView ingredientWeight) {
        ingredient.setAmount(progress);
        ingredient.save();
        Logger.d(TAG, "-->1");
        proposal.save();
        proposal.setPhe(ProposalHelper.calculatePhe(proposal));
        Logger.d(TAG, "-->2");
        proposal.setKcal(ProposalHelper.calculateKcal(proposal));
        proposal.setWeight(new ProposalHelperKcal().calculateWeight(proposal));
        Logger.d(TAG, "--->3");
        updateIngredientsAndAmounts(proposal);
        Logger.d(TAG, "--->4");
        updatebaseInfo();
        updateIngredientWeight(ingredient, progress, ingredientWeight);
        proposal.save();
    }

    private void updateIngredientWeight(Ingredient ingredient, double amount, TextView ingredientWeight) {
        if(ingredient.getFood().getIsItemGood()) {
            ingredientWeight.setText(amount + " s");
        } else {
            ingredientWeight.setText(Math.round(amount) + " g");
        }
        ingredientWeight.setTextColor(Color.parseColor("#A8A8A8"));
    }

    private void updateIngredientsAndAmounts(Proposal proposal) {
        ingredients = FoodCombinationHelper.getListOfIngredients(proposal);
    }

    private void sayShit() {
        int result = tts.setLanguage(Locale.GERMANY);

        if (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Logger.e("TTS", "This Language is not supported");
        } else {
            Logger.d(TAG, "speak...");
            tts.speak(proposal.getName(),TextToSpeech.QUEUE_FLUSH,null,"loltest");
            Logger.d(TAG, "spoke");
        }
    }

    private void clickedAnimation(final TextView textView) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            textView.setBackgroundColor(Color.parseColor("#ffff66"));
                        }
                    });
                    Thread.sleep(100);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            textView.setBackgroundColor(Color.parseColor("#99cc00"));
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showToast() {
        Context context = getApplicationContext();
        CharSequence text = "Abgeschickt";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}