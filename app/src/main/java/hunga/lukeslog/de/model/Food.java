package hunga.lukeslog.de.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Food")
public class Food extends Model {

    @Column(name = "name")
    private String name;

    @Column(name = "phe")
    private double phe;

    @Column(name = "kcal")
    private double kcal;

    @Column(name = "isItemGood")
    private boolean isItemGood;

    public Food() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPhe(){
        return phe;
    }

    public void setPhe(double phe) {
        this.phe = phe;
    }

    public double getkcal() {
        return kcal;
    }

    public void setkcal(double kcal) {
        this.kcal = kcal;
    }

    public boolean getIsItemGood() {
        return isItemGood;
    }

    public void setIsItemGood(boolean isItemGood) {
        this.isItemGood = isItemGood;
    }
}
