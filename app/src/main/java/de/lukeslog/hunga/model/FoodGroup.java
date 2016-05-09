package de.lukeslog.hunga.model;

import android.graphics.drawable.Drawable;

import de.lukeslog.hunga.R;

public enum FoodGroup {

    GRAIN("Grains", "Getreideerzeugniss", R.drawable.bread),
    DAIRY("Dairy", "Milch- und Eiprodukte", R.drawable.dairy),
    CONFECTIONS_SWEET("Confections - Sweet", "Süßigkeiten", R.drawable.ic_action_food),
    CONFECTIONS_SALTY("Confections - Salty", "Salzigkeiten", R.drawable.ic_action_food),
    DRINK("Drink", "Getränke", R.drawable.ic_action_coffee),
    FRUIT("Fruit", "Obst", R.drawable.ic_action_food),
    PROC_FRUIT("Proc. Fruit", "Verarbeitetes Obst", R.drawable.ic_action_food),
    VEGETABLES("Vegetables", "Gemüse", R.drawable.vegetables),
    PROC_VEGETABLES("Proc. Vegetables", "Verarbeitetes Gemüse", R.drawable.vegetables),
    MEAT("Meat", "Fleisch- und Fischgerichte", R.drawable.meat),
    INGREDIENT("Ingredient", "Grundzutat", R.drawable.ingredient);

    private String foodGroupName;
    private String foodGroupPrintName;
    private int icon;

    FoodGroup(String foodGroupName, String foodGroupPrintName, int icon) {
        this.foodGroupName = foodGroupName;
        this.foodGroupPrintName = foodGroupPrintName;
        this.icon = icon;
    }

    public String getFoodGroupPrintName() {
        return foodGroupPrintName;
    }

    public String getFoodGroupName() {
        return foodGroupName;
    }

    public int getIcon() {
        return icon;
    }

    public static FoodGroup getFoodGroupByFoodGroupPrintName(String printName) {
        FoodGroup[] foodGroups = FoodGroup.values();
        for(FoodGroup foodGroup : foodGroups) {
            if (foodGroup.getFoodGroupPrintName().equals(printName)) {
                return foodGroup;
            }
        }
        return null;
    }

    public static FoodGroup getFoodGroupByFoodGroupName(String name) {
        FoodGroup[] foodGroups = FoodGroup.values();
        for(FoodGroup foodGroup : foodGroups) {
            if (foodGroup.getFoodGroupName().equals(name)) {
                return foodGroup;
            }
        }
        return null;
    }

    public static String[] getFoodGroupPrintNameArray() {
        FoodGroup[] foodGroups = FoodGroup.values();
        String[] foodGroupNames = new String[foodGroups.length];
        for (int i = 0; i < foodGroups.length; i++) {
            foodGroupNames[i] = foodGroups[i].getFoodGroupPrintName();
        }
        return foodGroupNames;
    }
}
