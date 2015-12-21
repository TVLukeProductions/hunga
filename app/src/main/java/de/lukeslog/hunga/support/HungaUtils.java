package de.lukeslog.hunga.support;

import de.lukeslog.hunga.model.Food;

public class HungaUtils {

    public static String randomString() {
        char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        int length = 20;
        String result = "";
        for (int i = length; i > 0; --i) {
            result += chars[(int)Math.round(Math.random() * (chars.length - 1))];
        }
        return result;
    }

    public static String getUnit(Food food) {
        String unit = "ml";
        if(food.isSolid()) {
            unit = "g";
        }
        return unit;
    }
}
