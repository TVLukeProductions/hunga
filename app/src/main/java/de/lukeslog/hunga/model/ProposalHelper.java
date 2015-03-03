package de.lukeslog.hunga.model;

import java.util.ArrayList;

import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;

public abstract class ProposalHelper extends FoodCombinationHelper {

    public static final String TAG = HungaConstants.TAG;

    public static void fillProposalWithFactor(Recipe recipe, double factor) {
        Proposal proposal = new Proposal();
        proposal.setName(recipe.getName());
        Logger.d(TAG, "create Proposal for: "+recipe.getName());
        Logger.d(TAG, "Factor:"+factor);
        proposal.setFactor(factor);
        proposal.setRecipe(recipe);
        ArrayList<Food> ingredients = RecipeHelper.getListOfIngredients(recipe);
        ArrayList<Double> amounts = RecipeHelper.getListOfAmounts(recipe);
        for(int i=1; i<ingredients.size()+1; i++) {
            Food ingredient = ingredients.get(i-1);
            double amount = amounts.get(i-1);
            amount = amount*factor;
            fillIngredient(proposal, ingredient, amount, i);
        }
        proposal.setPhe(calculatePhe(proposal));
        proposal.setKcal(calculateKcal(proposal));
        proposal.setWeight(calculateWeight(proposal));
        proposal.save();
    }

    private static double calculateWeight(Proposal proposal) {
        ArrayList<Double> amounts = RecipeHelper.getListOfAmounts(proposal);
        ArrayList<Food> ingredients = RecipeHelper.getListOfIngredients(proposal);
        double weightSum=0.0;
        for(int i=0; i<ingredients.size(); i++){
            Food ingredient = ingredients.get(i);
            if(ingredient.getIsItemGood()) {
                weightSum = weightSum + 100;
            } else {
                weightSum = weightSum + amounts.get(i);
            }
        }
        return weightSum;
    }

    private static double calculateKcal(Proposal proposal) {
        ArrayList<Double> amounts = RecipeHelper.getListOfAmounts(proposal);
        ArrayList<Food> ingredients = RecipeHelper.getListOfIngredients(proposal);
        double kcalSum=0.0;
        for(int i=0; i<ingredients.size(); i++){
            Food ingredient = ingredients.get(i);
            double kcalForItem = ingredient.getkcal();
            if(ingredient.getIsItemGood()) {
                kcalSum = kcalSum + (kcalForItem*amounts.get(i));
            } else {
                kcalSum = kcalSum + (kcalForItem*(amounts.get(i)/100.0));
            }
        }
        return kcalSum;
    }

    private static double calculatePhe(Proposal proposal) {
        Logger.d(TAG, "calulatephe");
        ArrayList<Double> amounts = RecipeHelper.getListOfAmounts(proposal);
        ArrayList<Food> ingredients = RecipeHelper.getListOfIngredients(proposal);
        double pheSum=0.0;
        Logger.d(TAG, ""+pheSum);
        for(int i=0; i<ingredients.size(); i++){
            Food ingredient = ingredients.get(i);
            Logger.d(TAG, ingredient.getName()+":");
            double pheForItem = ingredient.getPhe();
            if(ingredient.getIsItemGood()) {
                pheSum = pheSum + (pheForItem*amounts.get(i));
                Logger.d(TAG, ""+pheSum);
            } else {
                Logger.d(TAG, "pheForItem:  "+pheForItem);
                Logger.d(TAG, "ag: "+amounts.get(i));
                pheSum = pheSum + (pheForItem *(amounts.get(i)/100.0));
                Logger.d(TAG, ""+pheSum);
            }
        }
        Logger.d(TAG, ""+pheSum);
        return pheSum;
    }
}