package hunga.lukeslog.de.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "SearchResult")
public class SearchResult extends Model {

    @Column(name = "phe")
    private double phe;

    @Column(name = "kcal")
    private double kcal;

    @Column(name = "recipie")
    private Recipie recipie;

    @Column(name = "factor")
    private double factor;

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

    public Recipie getRecipie() {
        return recipie;
    }

    public void setRecipie(Recipie recipie) {
        this.recipie = recipie;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }
}