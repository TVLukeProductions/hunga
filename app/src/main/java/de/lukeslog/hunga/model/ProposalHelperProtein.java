package de.lukeslog.hunga.model;

public class ProposalHelperProtein extends ProposalHelper {

    @Override
    protected double getRelevantValueFor100Grams(Ingredient ingredient) {
        return ingredient.getFood().getProtein100();
    }
}
