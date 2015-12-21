package de.lukeslog.hunga.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "HungaError")
public class HungaError extends Model {

    @Column(name = "timestamp")
    private long timestamp;

    @Column(name = "message")
    private String message;

    public HungaError() {

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
