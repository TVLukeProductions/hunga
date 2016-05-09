package de.lukeslog.hunga.model;

public class ProposalHelperSalt extends ProposalHelper {

    @Override
    protected double getRelevantValueFor100Grams(Ingredient ingredient) {
        return  ingredient.getFood().getSalt100();
    }
}
