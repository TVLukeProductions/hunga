package de.lukeslog.hunga.model;

public class ProposalHelperFat extends ProposalHelper {


    @Override
    protected double getRelevantValueFor100Grams(Ingredient ingredient) {
        return ingredient.getFood().getFat100();
    }
}
