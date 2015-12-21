package de.lukeslog.hunga.model;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.support.Logger;

public abstract class FoodCombinationHelper {

    public static List<Ingredient> getListOfIngredients(FoodCombination fc) {
        List<Ingredient> ingredients = new Select().from(Ingredient.class).where("foodCombinationId = ?", fc.getUid()).execute();
        return ingredients;
    }

    public static boolean containsItemGood(FoodCombination fc) {
        List<Ingredient> ingredients = getListOfIngredients(fc);
        for(Ingredient ingredient : ingredients) {
            if(ingredient.getFood().getIsItemGood()) {
                return true;
            }
        }
        return false;
    }

    public static String getIngredientsAsString(FoodCombination fc) {
        String description="";
        List<Ingredient> ingredients = getListOfIngredients(fc);
        for(Ingredient ingredient : ingredients) {
            description = description +ingredient.getFood().getName()+", ";
        }
        description = description.substring(0, description.length()-2);
        return description;
    }

    public static String getIngredientsEquivGroupAsString(FoodCombination fc) {
        String description="";
        List<Ingredient> ingredients = getListOfIngredients(fc);
        for(Ingredient ingredient : ingredients) {
            description = description +ingredient.getFood().getEquivalenceGroup()+", ";
        }
        description = description.substring(0, description.length()-2);
        return description;
    }

    public static boolean containsFood(FoodCombination fc, Food food) {
        List<Ingredient> ingredients = getListOfIngredients(fc);
        for(Ingredient ingredient : ingredients) {
            if(ingredient.getFood().getBarcodeForUse().equals(food.getBarcodeForUse())) {
                return true;
            }
        }
        return false;
    }

    public static void addIngredient(FoodCombination fc, Food food) {
        Ingredient newIngredient = new Ingredient();
        newIngredient.setAmount(0);
        newIngredient.setFood(food);
        newIngredient.setFoodCombinationId(fc.getUid());
        newIngredient.save();
    }

    public static boolean containsAlcohol(FoodCombination fc) {
        List<Ingredient> ingredients = getListOfIngredients(fc);
        for(Ingredient ingredient : ingredients) {
            if(ingredient.getFood().isContainsAlcohol()) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsCaffein(FoodCombination fc) {
        List<Ingredient> ingredients = getListOfIngredients(fc);
        for(Ingredient ingredient : ingredients) {
            if(ingredient.getFood().isContainsCaffein()) {
                return true;
            }
        }
        return false;
    }
}