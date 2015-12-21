package de.lukeslog.hunga.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import de.lukeslog.hunga.support.HungaUtils;

@Table(name = "Proposal")
public class Proposal extends Model implements FoodCombination {

    @Column(name = "name")
    private String name;

    @Column(name = "uid")
    private String uid;

    @Column(name = "persons")
    private int forPersons;

    @Column(name = "phe")
    private double phe;

    @Column(name = "kcal")
    private double kcal;

    @Column(name = "weight")
    private double weight;

    @Column(name ="fat")
    private double fat;

    @Column(name = "saturatedFattyAcids")
    private double saturatedFattyAcids;

    @Column(name = "carbohydrate")
    private double carbohydrate;

    @Column(name = "sugarInCarbohydrate")
    private double sugarInCarbohydrate;

    @Column(name = "protein")
    private double protein;

    @Column(name = "salt")
    private double salt;

    @Column(name = "recipie")
    private Recipe recipe;

    @Column(name = "factor")
    private double factor;

    @Column(name = "fixedProportions")
    private boolean fixedproportions=false;

    public Proposal() {
        setUid(HungaUtils.randomString());
        save();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int getForPersons() {
        return forPersons;
    }

    @Override
    public void setForPersons(int persons) {
        this.forPersons = persons;
    }

    public double getPhe() {
        return phe;
    }

    public void setPhe(double phe){
        this.phe = phe;
    }

    public double getKcal() {
        return kcal;
    }

    public void setKcal(double kcal) {
        this.kcal = kcal;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean getFixedPropostions() {
        return fixedproportions;
    }

    public void setFixedproportions(boolean fixedproportions) {
        this.fixedproportions = fixedproportions;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getSaturatedFattyAcids() {
        return saturatedFattyAcids;
    }

    public void setSaturatedFattyAcids(double saturatedFattyAcids) {
        this.saturatedFattyAcids = saturatedFattyAcids;
    }

    public double getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(double carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public double getSugarInCarbohydrate() {
        return sugarInCarbohydrate;
    }

    public void setSugarInCarbohydrate(double sugarInCarbohydrate) {
        this.sugarInCarbohydrate = sugarInCarbohydrate;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getSalt() {
        return salt;
    }

    public void setSalt(double salt) {
        this.salt = salt;
    }

    public boolean isFixedproportions() {
        return fixedproportions;
    }
}