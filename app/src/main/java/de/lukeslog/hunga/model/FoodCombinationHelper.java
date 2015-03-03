package de.lukeslog.hunga.model;

import java.util.ArrayList;

public abstract class FoodCombinationHelper {

    public static ArrayList<Double> getListOfAmounts(FoodCombination fc) {
        ArrayList<Double> amounts = new ArrayList<Double>();
        for(int i=1; i<16; i++) {
            Double amount = getAmount(fc, i);
            if(amount != null && amount > 0.0) {
                amounts.add(amount);
            }
        }
        return amounts;
    }

    public static double getAmount(FoodCombination fc, int number) {
        if(number == 1) {
            return fc.getAmount1();
        }
        if(number == 2){
            return fc.getAmount2();
        }
        if(number == 3) {
            return fc.getAmount3();
        }
        if(number == 4){
            return fc.getAmount4();
        }
        if(number == 5) {
            return fc.getAmount5();
        }
        if(number == 6){
            return fc.getAmount6();
        }
        if(number == 7) {
            return fc.getAmount7();
        }
        if(number == 8){
            return fc.getAmount8();
        }
        if(number == 9) {
            return fc.getAmount9();
        }
        if(number == 10){
            return fc.getAmount10();
        }
        if(number == 11) {
            return fc.getAmount11();
        }
        if(number == 12){
            return fc.getAmount12();
        }
        if(number == 13) {
            return fc.getAmount13();
        }
        if(number == 14){
            return fc.getAmount14();
        }
        if(number == 15) {
            return fc.getAmount15();
        }
        return 0.0;
    }

    public static boolean containsItemGood(FoodCombination fc) {
        ArrayList<Food> ingredients = getListOfIngredients(fc);
        for(Food food : ingredients) {
            if(food.getIsItemGood()) {
                return true;
            }
        }
        return false;
    }

    public static void fillIngredient(FoodCombination foodCombination, Food food, double amount, int number) {
        if(number == 1) {
            foodCombination.setFood1(food);
            foodCombination.setAmount1(amount);
        }
        if(number == 2){
            foodCombination.setFood2(food);
            foodCombination.setAmount2(amount);
        }
        if(number == 3) {
            foodCombination.setFood3(food);
            foodCombination.setAmount3(amount);
        }
        if(number == 4){
            foodCombination.setFood4(food);
            foodCombination.setAmount4(amount);
        }
        if(number == 5) {
            foodCombination.setFood5(food);
            foodCombination.setAmount5(amount);
        }
        if(number == 6){
            foodCombination.setFood6(food);
            foodCombination.setAmount6(amount);
        }
        if(number == 7) {
            foodCombination.setFood7(food);
            foodCombination.setAmount7(amount);
        }
        if(number == 8){
            foodCombination.setFood8(food);
            foodCombination.setAmount8(amount);
        }
        if(number == 9) {
            foodCombination.setFood9(food);
            foodCombination.setAmount9(amount);
        }
        if(number == 10){
            foodCombination.setFood10(food);
            foodCombination.setAmount10(amount);
        }
        if(number == 11) {
            foodCombination.setFood11(food);
            foodCombination.setAmount11(amount);
        }
        if(number == 12){
            foodCombination.setFood12(food);
            foodCombination.setAmount12(amount);
        }
        if(number == 13) {
            foodCombination.setFood13(food);
            foodCombination.setAmount13(amount);
        }
        if(number == 14){
            foodCombination.setFood14(food);
            foodCombination.setAmount14(amount);
        }
        if(number == 15) {
            foodCombination.setFood15(food);
            foodCombination.setAmount15(amount);
        }
    }

    public static String getIngredientsAsString(FoodCombination fc) {
        String description="";
        ArrayList<Food> ingredients = getListOfIngredients(fc);
        for(Food food : ingredients) {
            description = description +food.getName()+", ";
        }
        description = description.substring(0, description.length()-2);
        return description;
    }

    public static ArrayList<Food> getListOfIngredients(FoodCombination fc) {
        ArrayList<Food> ingredients = new ArrayList<Food>();
        for(int i=1; i<16; i++) {
            Food food = getIngredient(fc, i);
            if(food != null) {
                ingredients.add(food);
            }
        }
        return ingredients;
    }

    public static Food getIngredient(FoodCombination fc, int number) {
        if(number == 1) {
            return fc.getFood1();
        }
        if(number == 2){
            return fc.getFood2();
        }
        if(number == 3) {
            return fc.getFood3();
        }
        if(number == 4){
            return fc.getFood4();
        }
        if(number == 5) {
            return fc.getFood5();
        }
        if(number == 6){
            return fc.getFood6();
        }
        if(number == 7) {
            return fc.getFood7();
        }
        if(number == 8){
            return fc.getFood8();
        }
        if(number == 9) {
            return fc.getFood9();
        }
        if(number == 10){
            return fc.getFood10();
        }
        if(number == 11) {
            return fc.getFood11();
        }
        if(number == 12){
            return fc.getFood12();
        }
        if(number == 13) {
            return fc.getFood13();
        }
        if(number == 14){
            return fc.getFood14();
        }
        if(number == 15) {
            return fc.getFood15();
        }
        return null;
    }
}