package de.lukeslog.hunga.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "ProtokollItem")
public class ProtokollItem extends Model {

    @Column(name = "timestamp")
    private long timestamp;

    @Column(name = "barcodeForUse")
    private String barcodeForUse;

    @Column(name = "amount")
    private double amount;

    public ProtokollItem() {

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBarcodeForUse() {
        return barcodeForUse;
    }

    public void setBarcodeForUse(String barcodeForUse) {
        this.barcodeForUse = barcodeForUse;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
