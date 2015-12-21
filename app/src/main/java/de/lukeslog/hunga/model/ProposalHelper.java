package de.lukeslog.hunga.model;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.HungaUtils;
import de.lukeslog.hunga.support.Logger;

public abstract class ProposalHelper extends FoodCombinationHelper {

    public static final String TAG = HungaConstants.TAG;

    public void fillProposalWithFactor(Recipe recipe, double factor) {
        Proposal proposal = new Proposal();
        proposal.setName(recipe.getName());
        Logger.d(TAG, "create Proposal for: "+recipe.getName());
        Logger.d(TAG, "Factor:"+factor);
        proposal.setFactor(factor);
        proposal.setRecipe(recipe);
        proposal.setForPersons(recipe.getForPersons());
        //TODO: add fixed proportions to recipe as db field...
        List<Ingredient> ingredients = RecipeHelper.getListOfIngredients(recipe);
        Logger.d(TAG, "ingredients recipe?"+ingredients.size());
        for(int i=0; i<ingredients.size(); i++) {
            Ingredient proposalIngredient = new Ingredient();
            double amount = ingredients.get(i).getAmount();
            amount = amount*factor;
            proposalIngredient.setAmount(amount);
            proposalIngredient.setFood(ingredients.get(i).getFood());
            Logger.d(TAG, proposal.getUid());
            proposalIngredient.setFoodCombinationId(proposal.getUid());
            proposalIngredient.save();
        }
        proposal.save();
        proposal.setPhe(calculatePhe(proposal));
        proposal.setKcal(calculateKcal(proposal));
        proposal.setWeight(calculateWeight(proposal));
        proposal.setFat(calculateFat(proposal));
        proposal.setSaturatedFattyAcids(calculateSatFat(proposal));
        proposal.setCarbohydrate(calculateCarbo(proposal));
        proposal.setSugarInCarbohydrate(calculateSugar(proposal));
        proposal.setProtein(calculateProtein(proposal));
        proposal.setSalt(calculateSalt(proposal));
        proposal.save();
    }

    public double calculateWeight(Proposal proposal) {
        List<Ingredient> ingredients = RecipeHelper.getListOfIngredients(proposal);
        double weightSum=0.0;
        for(int i=0; i<ingredients.size(); i++){
            Food ingredient = ingredients.get(i).getFood();
            double amount = ingredients.get(i).getAmount();
            if(ingredient.getIsItemGood()) {
                weightSum = weightSum + 100.0;
            } else {
                weightSum = weightSum + amount;
            }
        }
        return weightSum;
    }

    public static double calculateKcal(Proposal proposal) {
        List<Ingredient> ingredients = RecipeHelper.getListOfIngredients(proposal);
        double kcalSum=0.0;
        for(int i=0; i<ingredients.size(); i++){
            Food ingredient = ingredients.get(i).getFood();
            double amount = ingredients.get(i).getAmount();
            double kcalForItem = ingredient.getKcal100();
            if(ingredient.getIsItemGood()) {

                kcalSum = kcalSum + (kcalForItem*amount/100.0*ingredient.getWeightPerServing());
            } else {
                kcalSum = kcalSum + (kcalForItem/100.0*amount);
            }
        }
        return kcalSum;
    }

    public static double calculateFat(Proposal proposal) {
        List<Ingredient> ingredients = RecipeHelper.getListOfIngredients(proposal);
        double fatSum=0.0;
        for(int i=0; i<ingredients.size(); i++){
            Food ingredient = ingredients.get(i).getFood();
            double amount = ingredients.get(i).getAmount();
            double fatForItem = ingredient.getFat100();
            if(ingredient.getIsItemGood()) {

                fatSum = fatSum + (fatForItem*amount/100.0*ingredient.getWeightPerServing());
            } else {
                fatSum = fatSum + (fatForItem/100.0*amount);
            }
        }
        return fatSum;
    }

    public static double calculateSatFat(Proposal proposal) {
        List<Ingredient> ingredients = RecipeHelper.getListOfIngredients(proposal);
        double fatSum=0.0;
        for(int i=0; i<ingredients.size(); i++){
            Food ingredient = ingredients.get(i).getFood();
            double amount = ingredients.get(i).getAmount();
            double satfatForItem = ingredient.getSaturatedFattyAcids100();
            if(ingredient.getIsItemGood()) {

                fatSum = fatSum + (satfatForItem*amount/100.0*ingredient.getWeightPerServing());
            } else {
                fatSum = fatSum + (satfatForItem/100.0*amount);
            }
        }
        return fatSum;
    }

    public static double calculateCarbo(Proposal proposal) {
        List<Ingredient> ingredients = RecipeHelper.getListOfIngredients(proposal);
        double fatSum=0.0;
        for(int i=0; i<ingredients.size(); i++){
            Food ingredient = ingredients.get(i).getFood();
            double amount = ingredients.get(i).getAmount();
            double satfatForItem = ingredient.getCarbohydrate100();
            if(ingredient.getIsItemGood()) {

                fatSum = fatSum + (satfatForItem*amount/100.0*ingredient.getWeightPerServing());
            } else {
                fatSum = fatSum + (satfatForItem/100.0*amount);
            }
        }
        return fatSum;
    }

    public static double calculateSugar(Proposal proposal) {
        List<Ingredient> ingredients = RecipeHelper.getListOfIngredients(proposal);
        double fatSum=0.0;
        for(int i=0; i<ingredients.size(); i++){
            Food ingredient = ingredients.get(i).getFood();
            double amount = ingredients.get(i).getAmount();
            double satfatForItem = ingredient.getSugarInCarbohydrate100();
            if(ingredient.getIsItemGood()) {

                fatSum = fatSum + (satfatForItem*amount/100.0*ingredient.getWeightPerServing());
            } else {
                fatSum = fatSum + (satfatForItem/100.0*amount);
            }
        }
        return fatSum;
    }

    public static double calculateProtein(Proposal proposal) {
        List<Ingredient> ingredients = RecipeHelper.getListOfIngredients(proposal);
        double fatSum=0.0;
        for(int i=0; i<ingredients.size(); i++){
            Food ingredient = ingredients.get(i).getFood();
            double amount = ingredients.get(i).getAmount();
            double satfatForItem = ingredient.getProtein100();
            if(ingredient.getIsItemGood()) {

                fatSum = fatSum + (satfatForItem*amount/100.0*ingredient.getWeightPerServing());
            } else {
                fatSum = fatSum + (satfatForItem/100.0*amount);
            }
        }
        return fatSum;
    }

    public static double calculateSalt(Proposal proposal) {
        List<Ingredient> ingredients = RecipeHelper.getListOfIngredients(proposal);
        double fatSum=0.0;
        for(int i=0; i<ingredients.size(); i++){
            Food ingredient = ingredients.get(i).getFood();
            double amount = ingredients.get(i).getAmount();
            double satfatForItem = ingredient.getSalt100();
            if(ingredient.getIsItemGood()) {

                fatSum = fatSum + (satfatForItem*amount/100.0*ingredient.getWeightPerServing());
            } else {
                fatSum = fatSum + (satfatForItem/100.0*amount);
            }
        }
        return fatSum;
    }

    public static double calculatePhe(Proposal proposal) {
        List<Ingredient> ingredients = RecipeHelper.getListOfIngredients(proposal);
        double pheSum=0.0;
        for(int i=0; i<ingredients.size(); i++){
            Food ingredient = ingredients.get(i).getFood();
            double ammount = ingredients.get(i).getAmount();
            double pheForItem = ingredient.getPhe100();
            if(ingredient.getIsItemGood()) {
                pheSum = pheSum + (pheForItem*ammount/100.0*ingredient.getWeightPerServing());
            } else {
                pheSum = pheSum + (pheForItem/100.0*(ammount));
            }
        }
        return pheSum;
    }

    public double getFactorForGoal(FoodCombination fc, double goal) {
        if(containsItemGood(fc)) {
            return getFactorForGoalWithItemGood(fc, goal);
        } else {
            return getFactorForGoalWithoutItemGood(fc, goal);
        }
    }

    protected double getFactorForGoalWithoutItemGood(FoodCombination recipe, double goal) {
        Logger.d(TAG, "getFactorForGoalWithoutItemGood " + goal);
        List<Ingredient> ingredients = getListOfIngredients(recipe);
        ArrayList<Double> valuePerItem = new ArrayList<Double>();

        for(int i=0; i<ingredients.size(); i++) {
            Food ingredient = ingredients.get(i).getFood();
            Double amount = ingredients.get(i).getAmount();
            double value100 = getRelevantValueFor100Grams(ingredient);
            valuePerItem.add(value100*(amount/100.0));
        }

        double sumOfValue = 0.0;
        for (double pheItem : valuePerItem) {
            sumOfValue  = sumOfValue  + pheItem;
        }

        return goal/sumOfValue;
    }

    protected double getFactorForGoalWithItemGood(FoodCombination recipe, double goal){
        double factor = 0.0;
        Logger.d(TAG, "->"+recipe.getName());
        List<Ingredient> ingredients = getListOfIngredients(recipe);
        ArrayList<Double> valuePerItem = new ArrayList<Double>();

        for(int i=0; i<ingredients.size(); i++) {
            Food ingredient = ingredients.get(i).getFood();
            Double amount = ingredients.get(i).getAmount();
            double value100 = getRelevantValueFor100Grams(ingredient);
            if(ingredient.getIsItemGood()) {
                valuePerItem.add((value100/100*ingredient.getWeightPerServing())*amount);
            } else {
                valuePerItem.add(value100 * (amount / 100.0));
            }
        }

        double result = 0.0;
        while(result<goal) {
            result = 0.0;
            factor = factor + 0.5;
            for (int i = 0; i < ingredients.size(); i++) {
                result = result + valuePerItem.get(i)*factor;
            }
        }
        factor = factor - 0.5;
        Logger.d(TAG, "--->"+factor);
        return factor;
    }

    protected abstract double getRelevantValueFor100Grams(Food ingredient);

}