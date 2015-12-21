package de.lukeslog.hunga.model;

import java.util.List;

import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.HungaUtils;
import de.lukeslog.hunga.support.Logger;

public class RecipeHelper extends FoodCombinationHelper {

    public static final String TAG = HungaConstants.TAG;

    public static Recipe fillFromFoodCombination(FoodCombination foodCombination) {
        Recipe newRecipe = new Recipe();
        newRecipe.setUid(HungaUtils.randomString());
        newRecipe.setName(foodCombination.getName());
        newRecipe.setForPersons(foodCombination.getForPersons());
        Logger.d(TAG, "create Proposal for: "+newRecipe.getName());
        //TODO: add fixed proportions to recipe as db field...
        List<Ingredient> ingredients = RecipeHelper.getListOfIngredients(foodCombination);
        Logger.d(TAG, "ingredients recipe?"+ingredients.size());
        for(int i=0; i<ingredients.size(); i++) {
            Ingredient proposalIngredient = new Ingredient();
            double amount = ingredients.get(i).getAmount();
            proposalIngredient.setAmount(amount);
            proposalIngredient.setFood(ingredients.get(i).getFood());
            Logger.d(TAG, newRecipe.getUid());
            proposalIngredient.setFoodCombinationId(newRecipe.getUid());
            proposalIngredient.save();
        }
        newRecipe.save();
        return newRecipe;
    }
}
