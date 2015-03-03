package de.lukeslog.hunga.model;

import java.util.ArrayList;

import de.lukeslog.hunga.support.Logger;

public class ProposalHelperKcal extends ProposalHelper{

    public static double getFactorForGoal(FoodCombination fc, double goal) {
        if(containsItemGood(fc)) {
            return getFactorForGoalWithItemGood(fc, goal);
        } else {
            return getFactorForGoalWithoutItemGood(fc, goal);
        }
    }

    protected static double getFactorForGoalWithoutItemGood(FoodCombination recipe, double goal) {
        Logger.d(TAG, "getFactorForGoalWithoutItemGood " + goal);
        ArrayList<Double> amounts = getListOfAmounts(recipe);
        ArrayList<Food> ingredients = getListOfIngredients(recipe);
        ArrayList<Double> kcalPerItem = new ArrayList<Double>();

        for(int i=0; i<ingredients.size(); i++) {
            Food ingredient = ingredients.get(i);
            Double amount = amounts.get(i);
            double kcal = ingredient.getkcal();
            kcalPerItem.add(kcal * (amount / 100.0));
        }

        double sumOfKcal = 0.0;
        for (double pheItem : kcalPerItem) {
            sumOfKcal  = sumOfKcal  + pheItem;
        }

        return goal/sumOfKcal;
    }

    protected static double getFactorForGoalWithItemGood(FoodCombination recipe, double goal){
        double factor = 0.0;

        ArrayList<Double> amounts = getListOfAmounts(recipe);
        ArrayList<Food> ingredients = getListOfIngredients(recipe);
        ArrayList<Double> kcalPerItem = new ArrayList<Double>();

        for(int i=0; i<ingredients.size(); i++) {
            Food ingredient = ingredients.get(i);
            Double amount = amounts.get(i);
            double kcal = ingredient.getkcal();
            if(ingredient.getIsItemGood()) {
                kcalPerItem.add(kcal * amount);
            } else {
                kcalPerItem.add(kcal * (amount / 100.0));
            }
        }

        double result = 0.0;
        while(result<goal) {
            result = 0.0;
            factor = factor + 0.5;
            for (int i = 0; i < ingredients.size(); i++) {
                result = result + kcalPerItem.get(i)*factor;
            }
        }
        factor = factor - 0.5;
        return factor;
    }
}