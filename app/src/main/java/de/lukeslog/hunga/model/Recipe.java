package de.lukeslog.hunga.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Recipe")
public class Recipe extends Model implements FoodCombination {

    @Column(name = "name")
    private String name;

    @Column(name = "food1")
    private Food food1;

    @Column(name = "amount1")
    private double amount1;

    @Column(name = "food2")
    private Food food2;

    @Column(name = "amount2")
    private double amount2;

    @Column(name = "food3")
    private Food food3;

    @Column(name = "amount3")
    private double amount3;

    @Column(name = "food4")
    private Food food4;

    @Column(name = "amount4")
    double amount4;

    @Column(name = "food5")
    private Food food5;

    @Column(name = "amount5")
    private double amount5;

    @Column(name = "food6")
    private Food food6;

    @Column(name = "amount6")
    private double amount6;

    @Column(name = "food7")
    private Food food7;

    @Column(name = "amount7")
    private double amount7;

    @Column(name = "food8")
    private Food food8;

    @Column(name = "amount8")
    private double amount8;

    @Column(name = "food9")
    private Food food9;

    @Column(name = "amount9")
    private double amount9;

    @Column(name = "food10")
    private Food food10;

    @Column(name = "amount10")
    private double amount10;

    @Column(name = "food11")
    private Food food11;

    @Column(name = "amount11")
    private double amount11;

    @Column(name = "food12")
    private Food food12;

    @Column(name = "amount12")
    private double amount12;

    @Column(name = "food13")
    private Food food13;

    @Column(name = "amount13")
    private double amount13;

    @Column(name = "food14")
    private Food food14;

    @Column(name = "amount14")
    private double amount14;

    @Column(name = "food15")
    private Food food15;

    @Column(name = "amount15")
    private double amount15;

    public Recipe() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Food getFood1() {
        return food1;
    }

    public void setFood1(Food food) {
        this.food1 = food;
    }

    public Food getFood2() {
        return food2;
    }

    public void setFood2(Food food) {
        this.food2 = food;
    }

    public Food getFood3() {
        return food3;
    }

    public void setFood3(Food food) {
        this.food3 = food;
    }

    public Food getFood4() {
        return food4;
    }

    public void setFood4(Food food) {
        this.food4 = food;
    }

    public Food getFood5() {
        return food5;
    }

    public void setFood5(Food food) {
        this.food5 = food;
    }

    public Food getFood6() {
        return food6;
    }

    public void setFood6(Food food) {
        this.food6 = food;
    }

    public Food getFood7() {
        return food7;
    }

    public void setFood7(Food food) {
        this.food7 = food;
    }

    public Food getFood8() {
        return food8;
    }

    public void setFood8(Food food) {
        this.food8 = food;
    }

    public Food getFood9() {
        return food9;
    }

    public void setFood9(Food food) {
        this.food9 = food;
    }

    public Food getFood10() {
        return food10;
    }

    public void setFood10(Food food) {
        this.food10 = food;
    }

    public Food getFood11() {
        return food11;
    }

    public void setFood11(Food food) {
        this.food11 = food;
    }

    public Food getFood12() {
        return food12;
    }

    public void setFood12(Food food) {
        this.food1 = food;
    }

    public void setFood13(Food food) {
        this.food13 = food;
    }

    public Food getFood13() {
        return food13;
    }

    public void setFood14(Food food) {
        this.food14 = food;
    }

    public Food getFood14() {
        return food14;
    }

    public void setFood15(Food food) {
        this.food15 = food;
    }

    public Food getFood15() {
        return food15;
    }

    public double getAmount1() {
        return amount1;
    }

    public void setAmount1(double amount) {
        this.amount1 = amount;
    }

    public double getAmount2() {
        return amount2;
    }

    public void setAmount2(double amount) {
        this.amount2 = amount;
    }

    public double getAmount3() {
        return amount3;
    }

    public void setAmount3(double amount) {
        this.amount3 = amount;
    }

    public double getAmount4() {
        return amount4;
    }

    public void setAmount4(double amount) {
        this.amount4 = amount;
    }

    public double getAmount5() {
        return amount5;
    }

    public void setAmount5(double amount) {
        this.amount5 = amount;
    }

    public double getAmount6() {
        return amount6;
    }

    public void setAmount6(double amount) {
        this.amount6 = amount;
    }

    public double getAmount7() {
        return amount7;
    }

    public void setAmount7(double amount) {
        this.amount7 = amount;
    }

    public double getAmount8() {
        return amount8;
    }

    public void setAmount8(double amount) {
        this.amount8 = amount;
    }

    public double getAmount9() {
        return amount9;
    }

    public void setAmount9(double amount) {
        this.amount9 = amount;
    }

    public double getAmount10() {
        return amount10;
    }

    public void setAmount10(double amount) {
        this.amount10 = amount;
    }

    public double getAmount11() {
        return amount11;
    }

    public void setAmount11(double amount) {
        this.amount11 = amount;
    }

    public double getAmount12() {
        return amount12;
    }

    public void setAmount12(double amount) {
        this.amount12 = amount;
    }

    public double getAmount13() {
        return amount13;
    }

    public void setAmount13(double amount) {
        this.amount13 = amount;
    }

    public double getAmount14() {
        return amount14;
    }

    public void setAmount14(double amount) {
        this.amount14 = amount;
    }

    public double getAmount15() {
        return amount15;
    }

    public void setAmount15(double amount) {
        this.amount15 = amount;
    }
}
