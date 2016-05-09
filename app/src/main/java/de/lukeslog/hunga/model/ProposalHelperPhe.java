package de.lukeslog.hunga.model;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.support.Logger;

public class ProposalHelperPhe extends ProposalHelper {

    @Override
    protected double getRelevantValueFor100Grams(Ingredient ingredient) {
        return ingredient.getFood().getPhe100();
    }
}