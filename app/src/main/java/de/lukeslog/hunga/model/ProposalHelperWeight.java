package de.lukeslog.hunga.model;

public class ProposalHelperWeight extends ProposalHelper{

    @Override
    protected double getRelevantValueFor100Grams(Ingredient ingredient) {
        return ingredient.getAmount();
    }
}
