package de.lukeslog.hunga.model;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.support.Logger;

public class ProposalHelperKcal extends ProposalHelper{

    @Override
    protected double getRelevantValueFor100Grams(Food ingredient) {
        return ingredient.getKcal100();
    }
}