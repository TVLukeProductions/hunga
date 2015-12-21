package de.lukeslog.hunga.rest;

import android.content.Context;
import android.content.SharedPreferences;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.util.Log;

import org.joda.time.DateTime;
import org.joda.time.chrono.StrictChronology;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.FoodCombination;
import de.lukeslog.hunga.model.HungaError;
import de.lukeslog.hunga.model.Ingredient;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.model.ProtokollItem;
import de.lukeslog.hunga.model.Recipe;
import de.lukeslog.hunga.model.RecipeHelper;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;

public class Downloader {

    public static final String TAG = HungaConstants.TAG;

    private static final String foodURL = HungaConstants.foodURL;
    private static final String recepieURL = HungaConstants.recepieURL;
    private static final String getFavorites = HungaConstants.getFavorites;
    private static final String getConsumtionUrl = HungaConstants.getConsumtionUrl;

    public static void downloadFoodInformation(final String userAcc, final Context context) {
        new Thread(new Runnable() {
            public void run() {
                readAndStoreFood(userAcc);
                readAndStoreRecepies();
                readAndStoreConsumtion(userAcc, context);
            }
        }).start();

    }

    private static void readAndStoreRecepies() {
        try {
            Logger.d(TAG, "getRecipies");
            URL oracle = new URL(recepieURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

            String inputLine;
            String tempf="";
            while ((inputLine = in.readLine()) != null)
            {
                tempf = tempf + inputLine;
            }
            Logger.d(TAG, "got recipies"+tempf);
            try {
                new Delete().from(Recipe.class).execute();
            } catch(Exception e) {
                Logger.e(TAG, "could not delete old recipies");
            }
            JSONArray json = new JSONArray(tempf);
            for(int i=0; i<json.length(); i++) {
                JSONObject jrecipie = json.getJSONObject(i);
                String uid = jrecipie.getString("UID");
                String name = jrecipie.getString("Rezeptname");
                int howMany = jrecipie.getInt("Personen");
                Logger.d(TAG, name+howMany);
                Recipe recipe = new Recipe();
                try {
                    recipe.setName(name);
                    recipe.setUid(uid);
                    recipe.setForPersons(howMany);
                    //TODO: fixed Proportions?
                    addFoodItems(jrecipie, recipe);
                    Logger.d(TAG, "attept save");
                    recipe.save();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            List<Recipe> recp;
            recp = new Select().from(Recipe.class).execute();
            Logger.d(TAG, "" + recp.size());
            Logger.d(TAG, ""+recp.get(0).getName());
            RestService.setLoading(false);
        } catch (Exception e) {
            e.printStackTrace();
            RestService.setLoading(false);
        }
    }

    private static void addFoodItems(JSONObject jrecipie, FoodCombination recipe) throws Exception {
        JSONArray ingredients = jrecipie.getJSONArray("ingredients");
        for(int i=0; i<ingredients.length(); i++) {
            JSONObject jingredient = ingredients.getJSONObject(i);
            Ingredient ingredient = new Ingredient();
            Food food = getFoodFromBarcodeForUse(jingredient.getString("barcode"));
            if(food == null) {
                throw new Exception("Food Not Found");
            }
            ingredient.setFood(getFoodFromBarcodeForUse(jingredient.getString("barcode")));
            ingredient.setAmount(jingredient.getDouble("amount"));
            ingredient.setFoodCombinationId(recipe.getUid());
            ingredient.save();
        }
    }

    private static Food getFoodFromBarcodeForUse(String barcode) {
        Food product = new Select().from(Food.class).where("barcodeForUse = ?", barcode).executeSingle();
        return product;
    }

    private static void readAndStoreFood(final String userAcc) {
        Logger.d(TAG, "get foods....");
        try {
            Logger.d(TAG, "1. Favorites");
            URL favurl = new URL(getFavorites+"?userAccount="+userAcc);
            BufferedReader infav = new BufferedReader(new InputStreamReader(favurl.openStream()));

            String inputLine;
            String tempf="";
            while ((inputLine = infav.readLine()) != null)
            {
                tempf = tempf + inputLine;
            }
            Logger.d(TAG, tempf);
            JSONObject favjson = new JSONObject(tempf);
            JSONArray jfavs = favjson.getJSONArray("favorites");
            List<String> favs = new ArrayList<>();
            for(int i=0; i<jfavs.length(); i++) {
                Logger.d(TAG, jfavs.get(i).toString());
                favs.add(jfavs.get(i).toString());
            }

            Logger.d(TAG, "get Food");
            URL oracle = new URL(foodURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

            tempf="";
            while ((inputLine = in.readLine()) != null)
            {
                tempf = tempf + inputLine;
            }
            try {
                new Delete().from(Proposal.class).execute();
                new Delete().from(Recipe.class).execute();
                new Delete().from(Ingredient.class).execute();
                new Delete().from(Food.class).execute();
            } catch(Exception e) {
                Logger.e(TAG, "could not delete ");
            }
            JSONArray json = new JSONArray(tempf);
            for(int i=0; i<json.length(); i++) {
                try {
                    JSONObject food = json.getJSONObject(i);
                    Logger.d(TAG, food.toString());
                    String name = food.getString("Name");
                    boolean itemGood = food.getBoolean("Stueckgut");
                    // Logger.d(TAG, name+" "+phe+" "+kcal+" "+itemGood);
                    Food f = new Food();
                    f.setName(name);
                    f.setBarcode(food.getString("Barcode"));
                    f.setBarcodeForUse(food.getString("Inderect Barcode"));
                    if(favs.contains(food.getString("Inderect Barcode"))) {
                        f.setFav(true);
                    } else {
                        f.setFav(false);
                    }
                    f.setPhe100(food.getDouble("phe100"));
                    f.setKcal100(food.getDouble("kcal100"));
                    f.setFat100(food.getDouble("Fat100g"));
                    f.setCarbohydrate100(food.getDouble("Kohlenhydrate100g"));
                    f.setSugarInCarbohydrate100(food.getDouble("ZuckerInKohlenhydrate100g"));
                    f.setProtein100(food.getDouble("Eiweis100"));
                    f.setSalt100(food.getDouble("Salz100g"));
                    Logger.d(TAG, ""+food.getDouble("gewicht"));
                    f.setWeightPerServing(food.getDouble("gewicht"));
                    f.setPheValueApprox(food.getBoolean("istPheValueCalculated"));
                    f.setIsItemGood(itemGood);
                    f.setBasisMenge(food.getDouble("Basismenge"));
                    f.setBaseUnit(food.getString("Basiseinheit"));
                    f.setSolid(food.getBoolean("fest"));
                    f.setFoodGroup(food.getString("Basic Food Group"));
                    f.setEquivalenceGroup(food.getString("Equivalencegroup"));
                    f.setContainsAlcohol(food.getBoolean("containsAlcohol"));
                    f.setContainsCaffein(food.getBoolean("containsCaffine"));
                    f.save();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readAndStoreConsumtion(final String userAcc, final Context ctx) {
        try {
            new Delete().from(ProtokollItem.class).execute();
        } catch(Exception e) {
            Logger.e(TAG, "could not delete ");
        }
                try {
                    URL favurl = new URL(getConsumtionUrl + "?userAccount=" + userAcc);
                    BufferedReader infav = new BufferedReader(new InputStreamReader(favurl.openStream()));

                    String inputLine;
                    String tempf = "";
                    while ((inputLine = infav.readLine()) != null) {
                        tempf = tempf + inputLine;
                    }
                    Logger.d(TAG, tempf);
                    JSONArray eaten = new JSONArray(tempf);
                    Float kcalSum = 0.0f;
                    Float liquidSum = 0.0f;
                    for(int i=0; i<eaten.length(); i++) {
                        JSONObject mealItem = eaten.getJSONObject(i);
                        String barcodeForUse = mealItem.getString("barcodeForUse");
                        double ammount = mealItem.getDouble("amount");
                        Food food = new Select().from(Food.class).where("barcodeForUse = ?", barcodeForUse).executeSingle();
                        float kcal = (float) (food.getKcal100() / 100.0f * ammount);
                        kcalSum = kcalSum + kcal;
                        if(!food.isSolid()) {
                            liquidSum = (float) (liquidSum + ammount);
                        }
                        ProtokollItem protokollItem = new ProtokollItem();
                        protokollItem.setTimestamp(mealItem.getLong("timestamp"));
                        protokollItem.setBarcodeForUse(barcodeForUse);
                        protokollItem.setAmount(mealItem.getDouble("amount"));
                        protokollItem.save();
                    }
                    final SharedPreferences settings = ctx.getSharedPreferences("currentkcal", 0);
                    DateTime d = new DateTime();
                    String liquidName = "liquid"+d.getYear()+""+d.getMonthOfYear()+""+d.getDayOfMonth();
                    String kcalName = "kcal"+d.getYear()+""+d.getMonthOfYear()+""+d.getDayOfMonth();
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putFloat(kcalName, kcalSum);
                    editor.putFloat(liquidName, liquidSum);
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
    }
}
