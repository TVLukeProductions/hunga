package de.lukeslog.hunga.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Ingredient")
public class Ingredient extends Model {

    @Column(name = "foodCombinationId")
    String foodCombinationId;

    @Column(name = "food")
    Food food;

    @Column(name = "amount")
    double amount;

    public Ingredient() {

    }

    public String getFoodCombinationId() {
        return foodCombinationId;
    }

    public void setFoodCombinationId(String foodCombinationId) {
        this.foodCombinationId = foodCombinationId;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
