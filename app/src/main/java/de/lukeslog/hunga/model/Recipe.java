package de.lukeslog.hunga.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Recipe")
public class Recipe extends Model implements FoodCombination {

    @Column(name = "name")
    private String name;

    @Column(name = "uid")
    private String uid;

    @Column(name = "forPersons")
    private int forPersons;

    @Column(name = "fixedProportions")
    private boolean fixedproportions=false;

    public Recipe() {

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

    public boolean isFixedproportions() {
        return fixedproportions;
    }

    public void setFixedproportions(boolean fixedproportions) {
        this.fixedproportions = fixedproportions;
    }
}
