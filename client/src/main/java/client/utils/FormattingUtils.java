package client.utils;

import java.text.DecimalFormat;

public class FormattingUtils {
    public static final String CURRENCY = "\u20ac";
    /***
     * Returns a representation of the given value in cents, formatted to X.XX (if there are digits behind the dot)
     * @param amount a price, in cents
     * @return a String representation of an int price in cents
     */
    public static String getFormattedPrice(int amount){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(amount / 100.0) + CURRENCY;
    }
}
