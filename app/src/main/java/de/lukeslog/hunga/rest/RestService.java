package de.lukeslog.hunga.rest;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.model.Ingredient;
import de.lukeslog.hunga.model.Proposal;
import de.lukeslog.hunga.model.ProposalHelper;
import de.lukeslog.hunga.model.Recipe;
import de.lukeslog.hunga.model.RecipeHelper;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.HungaUtils;
import de.lukeslog.hunga.support.Logger;

public class RestService extends Service {

    private static RestService context;
    public static final String TAG = HungaConstants.TAG;

    private static boolean loading = false;

    private static String urlForFoodRegistration = HungaConstants.urlForFoodRegistration;
    private static String urlforFoodLog = HungaConstants.urlforFoodLog;
    private static String urlForRecipeLog = HungaConstants.urlForRecipeLog;
    private static String addToFavUrl = HungaConstants.addToFavUrl;
    private static String removeFromFavUrl = HungaConstants.removeFromFavUrl;
    private static String correctScan = HungaConstants.correctScan;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "SERVICE On Start Command");
        context = this;
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "SERVICE on create");
    }

    public void stop()
    {
        Logger.d(TAG, "Service stop()");
        stopSelf();
    }

    public static boolean isLoading() {
        return loading;
    }

    public static void setLoading(boolean l) {
        loading = l;
    }

    public static void updateLists(final String userAcc) {
        if(context!=null) {
            setLoading(true);
            Downloader.downloadFoodInformation(userAcc, context);
        }
    }

    public static void submitNewFood(final Food food) {
        Logger.d(TAG, "submit new Food");
        new Thread(new Runnable() {
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlForFoodRegistration);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("barcode", food.getBarcode()));
                nameValuePairs.add(new BasicNameValuePair("name", food.getName()));
                nameValuePairs.add(new BasicNameValuePair("basismenge", ""+food.getBasisMenge()));
                nameValuePairs.add(new BasicNameValuePair("basiseinheit", ""+food.getBaseUnit()));
                nameValuePairs.add(new BasicNameValuePair("itemgood", ""+food.getIsItemGood()));
                nameValuePairs.add(new BasicNameValuePair("isSolid", ""+food.isSolid()));
                nameValuePairs.add(new BasicNameValuePair("weightPerItem", ""+food.getWeightPerServing()));
                nameValuePairs.add(new BasicNameValuePair("foodGroup", ""+food.getFoodGroup()));
                Logger.d(TAG, "FFOD"+food.getFoodGroup());
                nameValuePairs.add(new BasicNameValuePair("kcal", ""+food.getKcal100()));
                nameValuePairs.add(new BasicNameValuePair("fat", ""+food.getFat100()));
                nameValuePairs.add(new BasicNameValuePair("satfat", ""+food.getSaturatedFattyAcids100()));
                nameValuePairs.add(new BasicNameValuePair("carb", ""+food.getCarbohydrate100()));
                nameValuePairs.add(new BasicNameValuePair("sugar", ""+food.getSugarInCarbohydrate100()));
                nameValuePairs.add(new BasicNameValuePair("protein", ""+food.getProtein100()));
                nameValuePairs.add(new BasicNameValuePair("salt", ""+food.getSalt100()));
                nameValuePairs.add(new BasicNameValuePair("phe", ""+food.getPhe100()));
                nameValuePairs.add(new BasicNameValuePair("rennetAlt", ""+food.isLabaustauschstoff()));
                nameValuePairs.add(new BasicNameValuePair("addsugar", ""+food.isAdditionalSugar()));
                nameValuePairs.add(new BasicNameValuePair("unprocessed", ""+food.isUnproccessed()));
                nameValuePairs.add(new BasicNameValuePair("containsAlc", ""+food.isContainsAlcohol()));
                nameValuePairs.add(new BasicNameValuePair("containsCaffein", ""+food.isContainsCaffein()));
                nameValuePairs.add(new BasicNameValuePair("notDuringPreg", ""));
                nameValuePairs.add(new BasicNameValuePair("notDuringNurs", ""));
                try {
                    Logger.d(TAG, "SUBMIT");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d(TAG, "RESPONSE: "+response.toString());
                } catch (IOException e) {
                    Log.d(TAG, "e"+e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void submitEatenProposal(final Proposal proposal, final String userAcc) {
        new Thread(new Runnable() {
            public void run() {
                Logger.d(TAG, "submit proposal");
                List<Ingredient> listOfFoods = ProposalHelper.getListOfIngredients(proposal);
                Logger.d(TAG, ""+listOfFoods.size());
                for(int i=0; i<listOfFoods.size(); i++) {
                    Logger.d(TAG, listOfFoods.get(i).getFood().getName());
                    submitEatenFood(listOfFoods.get(i).getFood(), listOfFoods.get(i).getAmount(), proposal.getName(), userAcc);
                    try {
                        //Because google docs do not take kindly to fast requests...
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void submitEatenFood(final Food food, final double amount, final String recipe, final String userAcc) {
        new Thread(new Runnable() {
            public void run() {
                Log.d(TAG, "RUN");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlforFoodLog);
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                double a = amount;
                if(food.getIsItemGood()) {
                    double weightPerItem = food.getWeightPerServing();
                    a = a * weightPerItem;
                }
                //- is it item or weight
                //- if it is item, how big is the item size
                //- recalculate to get weight
                Logger.d(TAG, ""+a);
                nameValuePairs.add(new BasicNameValuePair("userAccountId", userAcc));
                nameValuePairs.add(new BasicNameValuePair("barcode", food.getBarcodeForUse()));
                nameValuePairs.add(new BasicNameValuePair("weight", ""+a));
                nameValuePairs.add(new BasicNameValuePair("baseunit", HungaUtils.getUnit(food)));
                nameValuePairs.add(new BasicNameValuePair("name", food.getName()));
                nameValuePairs.add(new BasicNameValuePair("recipe", recipe));
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d(TAG, "RESPONSE: "+response.toString());
                } catch (IOException e) {
                    Log.d(TAG, "e"+e.getLocalizedMessage());
                    e.printStackTrace();
                }
                //TODO: this is shit
                DecimalFormat df = new DecimalFormat("0.00");
                DateTime d = new DateTime();
                String kcalName = "kcal"+d.getYear()+""+d.getMonthOfYear()+""+d.getDayOfMonth();
                String liquidName = "liquid"+d.getYear()+""+d.getMonthOfYear()+""+d.getDayOfMonth();
                final SharedPreferences settings = context.getSharedPreferences("currentkcal", 0);
                float currentkcal = settings.getFloat(kcalName, 0.0f);
                float currentliquid = settings.getFloat(liquidName, 0.0f);
                SharedPreferences.Editor editable = settings.edit();
                editable.putFloat(kcalName, (float) (currentkcal + ((+a/100)*food.getKcal100())));
                if(!food.isSolid()) {
                    editable.putFloat(liquidName, (float) (currentliquid+a));
                }
                editable.commit();
            }
        }).start();
    }

    public static void submitRecipe(final Recipe recipe) {
        new Thread(new Runnable() {
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlForRecipeLog);
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("uid", recipe.getUid()));
                nameValuePairs.add(new BasicNameValuePair("name", recipe.getName()));
                nameValuePairs.add(new BasicNameValuePair("fixedPropostions", ""+false));
                Logger.d(TAG, "FOR PERSONS"+recipe.getForPersons());
                nameValuePairs.add(new BasicNameValuePair("howMany", ""+recipe.getForPersons()));
                JSONArray jIngredients = new JSONArray();
                for(Ingredient ingredient : RecipeHelper.getListOfIngredients(recipe)) {
                    JSONObject jIngredient = new JSONObject();
                    try {
                        jIngredient.put("barcode", ingredient.getFood().getBarcodeForUse());
                        jIngredient.put("amount", ingredient.getAmount());
                        jIngredients.put(jIngredient);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                nameValuePairs.add(new BasicNameValuePair("ingridients", jIngredients.toString()));
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d(TAG, "RESPONSE: "+response.toString());
                } catch (IOException e) {
                    Log.d(TAG, "e"+e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void addToFavorites(final Food food, final String accid) {
        new Thread(new Runnable() {
            public void run() {
                Logger.d(TAG, food.getBarcodeForUse());
                Logger.d(TAG, accid);
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(addToFavUrl);
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("userAccount", ""+accid));
                nameValuePairs.add(new BasicNameValuePair("barcodeForUse", ""+food.getBarcodeForUse()));
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d(TAG, "RESPONSE: "+response.toString());
                } catch (IOException e) {
                    Log.d(TAG, "e"+e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void removeFromFavorites(final Food food, final String accid) {
        new Thread(new Runnable() {
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(removeFromFavUrl);
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("userAccount", ""+accid));
                nameValuePairs.add(new BasicNameValuePair("barcodeForUse", ""+food.getBarcodeForUse()));
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d(TAG, "RESPONSE: "+response.toString());
                } catch (IOException e) {
                    Log.d(TAG, "e"+e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static void correctScan(final long timestamp, final String userAcc, final String barcodeForUse, final double newAmount) {
        new Thread(new Runnable() {
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(correctScan);
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                Logger.d(TAG, "TIMESTAMP"+timestamp);
                nameValuePairs.add(new BasicNameValuePair("timestamp", ""+timestamp));
                nameValuePairs.add(new BasicNameValuePair("userAccountId", userAcc));
                nameValuePairs.add(new BasicNameValuePair("barcode", barcodeForUse));
                nameValuePairs.add(new BasicNameValuePair("newAmount", ""+newAmount));
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d(TAG, "RESPONSE: "+response);
                } catch (IOException e) {
                    Log.d(TAG, "e"+e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
}