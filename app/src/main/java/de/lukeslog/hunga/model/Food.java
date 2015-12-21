package de.lukeslog.hunga.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Food")
public class Food extends Model {

    @Column(name = "name")
    private String name;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "barcodeForUse")
    private String barcodeForUse;

    @Column(name = "baseAmount")
    private double basisMenge;

    @Column(name = "baseUnit")
    private String baseUnit;

    @Column(name = "phe")
    private double phe;

    @Column(name = "kcal")
    private double kcal;

    @Column(name = "equivalenceGroup")
    private String equivalenceGroup;

    @Column(name = "isItemGood")
    private boolean isItemGood;

    @Column(name = "isSolid")
    private boolean isSolid;

    @Column(name = "weightPerServing")
    private double weightPerServing;

    @Column(name = "foodGroup")
    private String foodGroup;

    @Column(name = "kcal100")
    private double kcal100;

    @Column(name = "phe100")
    private double phe100;

    @Column(name = "fat100")
    private double fat100;

    @Column(name = "saturatedFattyAcids100")
    private double saturatedFattyAcids100;

    @Column(name = "carbohydrate100")
    private double carbohydrate100;

    @Column(name = "sugarInCarbohydrate100")
    private double sugarInCarbohydrate100;

    @Column(name = "protein100")
    private double protein100;

    @Column(name = "salt100")
    private double salt100;

    @Column(name = "pheValueApprox")
    private boolean pheValueApprox;

    @Column(name = "additionalSugar")
    private boolean additionalSugar;

    @Column(name = "labaustauschstoff")
    private boolean labaustauschstoff;

    @Column(name ="unproccessed")
    private boolean unproccessed;

    @Column(name = "containsAlcohol")
    private boolean containsAlcohol;

    @Column(name = "containsCaffein")
    private boolean containsCaffein;

    @Column(name = "comment")
    private String comment;

    @Column(name = "favorit")
    private boolean fav=false;

    public Food() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Deprecated
    public double getPhe(){
        return phe;
    }

    @Deprecated
    public void setPhe(double phe) {
        this.phe = phe;
    }

    @Deprecated
    public double getkcal() {
        return kcal;
    }

    @Deprecated
    public void setkcal(double kcal) {
        this.kcal = kcal;
    }

    public boolean getIsItemGood() {
        return isItemGood;
    }

    public void setIsItemGood(boolean isItemGood) {
        this.isItemGood = isItemGood;
    }

    public String getBarcodeForUse() {
        return barcodeForUse;
    }

    public void setBarcodeForUse(String barcodeForUse) {
        this.barcodeForUse = barcodeForUse;
    }

    public double getBasisMenge() {
        return basisMenge;
    }

    public void setBasisMenge(double basisMenge) {
        this.basisMenge = basisMenge;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public boolean isSolid() {
        return isSolid;
    }

    public void setSolid(boolean solid) {
        isSolid = solid;
    }

    public double getWeightPerServing() {
        return weightPerServing;
    }

    public void setWeightPerServing(double weightPerServing) {
        this.weightPerServing = weightPerServing;
    }

    public double getKcal100() {
        return kcal100;
    }

    public void setKcal100(double kcal100) {
        this.kcal100 = kcal100;
    }

    public double getPhe100() {
        return phe100;
    }

    public void setPhe100(double phe100) {
        this.phe100 = phe100;
    }

    public double getFat100() {
        return fat100;
    }

    public void setFat100(double fat100) {
        this.fat100 = fat100;
    }

    public double getSaturatedFattyAcids100() {
        return saturatedFattyAcids100;
    }

    public void setSaturatedFattyAcids100(double saturatedFattyAcids100) {
        this.saturatedFattyAcids100 = saturatedFattyAcids100;
    }

    public double getCarbohydrate100() {
        return carbohydrate100;
    }

    public void setCarbohydrate100(double carbohydrate100) {
        this.carbohydrate100 = carbohydrate100;
    }

    public double getSugarInCarbohydrate100() {
        return sugarInCarbohydrate100;
    }

    public void setSugarInCarbohydrate100(double sugarInCarbohydrate100) {
        this.sugarInCarbohydrate100 = sugarInCarbohydrate100;
    }

    public double getProtein100() {
        return protein100;
    }

    public void setProtein100(double protein100) {
        this.protein100 = protein100;
    }

    public double getSalt100() {
        return salt100;
    }

    public void setSalt100(double salt100) {
        this.salt100 = salt100;
    }

    public boolean isPheValueApprox() {
        return pheValueApprox;
    }

    public void setPheValueApprox(boolean pheValueApprox) {
        this.pheValueApprox = pheValueApprox;
    }

    public boolean isAdditionalSugar() {
        return additionalSugar;
    }

    public void setAdditionalSugar(boolean additionalSugar) {
        this.additionalSugar = additionalSugar;
    }

    public boolean isLabaustauschstoff() {
        return labaustauschstoff;
    }

    public void setLabaustauschstoff(boolean labaustauschstoff) {
        this.labaustauschstoff = labaustauschstoff;
    }

    public boolean isUnproccessed() {
        return unproccessed;
    }

    public void setUnproccessed(boolean unproccessed) {
        this.unproccessed = unproccessed;
    }

    public boolean isContainsAlcohol() {
        return containsAlcohol;
    }

    public void setContainsAlcohol(boolean containsAlcohol) {
        this.containsAlcohol = containsAlcohol;
    }

    public boolean isContainsCaffein() {
        return containsCaffein;
    }

    public void setContainsCaffein(boolean containsCaffein) {
        this.containsCaffein = containsCaffein;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFoodGroup() {
        return foodGroup;
    }

    public void setFoodGroup(String foodGroup) {
        this.foodGroup = foodGroup;
    }

    public String getEquivalenceGroup() {
        return equivalenceGroup;
    }

    public void setEquivalenceGroup(String equivalenceGroup) {
        this.equivalenceGroup = equivalenceGroup;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }
}
