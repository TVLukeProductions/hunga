package de.lukeslog.hunga.rest;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.model.Recipe;
import de.lukeslog.hunga.model.RecipeHelper;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;

public class Downloader {

    public static final String TAG = HungaConstants.TAG;

    private static final String foodURL = "https://script.google.com/macros/s/AKfycbzATUJ-COeLPFS-8ZpG-iVOe40iQunrzFcSK1ok8ZNIp1Q8Vqc/exec";
    private static final String recepieURL ="https://script.google.com/macros/s/AKfycbxoCFZEKBZvBy54YppAqto3u4w3Oi-I4HlRBDfMYuww5djszzU/exec";

    public static void downloadFoodInformation() {
        new Thread(new Runnable() {
            public void run() {
                readAndStoreFood();
                readAndStoreRecepies();
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
            Logger.d(TAG, "got recipies");
            try {
                new Delete().from(Recipe.class).execute();
            } catch(Exception e) {
                Logger.e(TAG, "could not delete old recipies");
            }
            JSONArray json = new JSONArray(tempf);
            for(int i=0; i<json.length(); i++) {
                JSONObject jrecipie = json.getJSONObject(i);
                String name = jrecipie.getString("Rezeptname");
                Logger.d(TAG, name);
                Recipe recipe = new Recipe();
                try {
                    recipe.setName(name);
                    addFoodItems(jrecipie, recipe);
                    Logger.d(TAG, "attept save");
                    recipe.save();
                } catch(Exception e) {

                }
            }

            List<Recipe> recp = new ArrayList<Recipe>();
            recp = new Select().from(Recipe.class).execute();
            Logger.d(TAG, "" + recp.size());
            Logger.d(TAG, ""+recp.get(0).getName());
            RestService.setLoading(false);
        } catch (Exception e) {
            e.printStackTrace();
            RestService.setLoading(false);
        }
    }

    private static void addFoodItems(JSONObject jrecipie, Recipe recipe) throws JSONException {
        Logger.d(TAG, "" + jrecipie.length());
        for(int i=1; i<jrecipie.length(); i++) {
            JSONObject jfood = jrecipie.getJSONObject("Zutat_"+i);
            String name = jfood.getString("Food");
            double amount = jfood.getDouble("Amount");
            if(!name.equals("")) {
                Food food = new Select().from(Food.class).where("name = ?", name).executeSingle();

                if (food == null) {
                    Logger.e(TAG, "This food does not exist");
                    throw new JSONException("bla");
                } else {
                    Logger.d(TAG, food.getName());
                    Logger.d(TAG, "" + food.getPhe());
                    Logger.d(TAG, "" + food.getkcal());
                    Logger.d(TAG, "" + food.getIsItemGood());
                    RecipeHelper.fillIngredient(recipe, food, amount, i);
                }
            } else {
                Logger.d(TAG, "end of recipie");
            }
        }
    }

    private static void readAndStoreFood() {
        try {
            URL oracle = new URL(foodURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

            String inputLine;
            String tempf="";
            while ((inputLine = in.readLine()) != null)
            {
                tempf = tempf + inputLine;
            }
            try {
                new Delete().from(Proposal.class).execute();
                new Delete().from(Recipe.class).execute();
                new Delete().from(Food.class).execute();
            } catch(Exception e) {
                Logger.e(TAG, "could not delete ");
            }
            JSONArray json = new JSONArray(tempf);
            for(int i=0; i<json.length(); i++) {
                JSONObject food = json.getJSONObject(i);
                String name = food.getString("Nahrungsmittel");
                double phe = food.getDouble("phe");
                double kcal = food.getDouble("kcal");
                String itemStr = food.getString("item");
                boolean itemGood = false;
                if(!itemStr.equals("")) {
                    itemGood = true;
                }

//                      Logger.d(TAG, name+" "+phe+" "+kcal+" "+itemGood);
                Food f = new Food();
                f.setName(name);
                f.setPhe(phe);
                f.setkcal(kcal);
                f.setIsItemGood(itemGood);
                f.save();
                List<Food> foods = new ArrayList<Food>();
                foods = new Select().from(Food.class).execute();
                Logger.d(TAG, "" + foods.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
