package de.lukeslog.hunga.model;

public class ProposalHelperCarbohydrates extends ProposalHelper {

    @Override
    protected double getRelevantValueFor100Grams(Ingredient ingredient) {
        return ingredient.getFood().getCarbohydrate100();
    }
}
