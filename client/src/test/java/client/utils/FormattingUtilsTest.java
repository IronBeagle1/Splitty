package client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormattingUtilsTest {
    private final String currency = FormattingUtils.CURRENCY;
    @Test
    void getFormattedPrice2DP() {
        int amount = 4;
        String expected = "0.04" + currency;

        var result = FormattingUtils.getFormattedPrice(amount);
        assertEquals(expected, result);
    }

    @Test
    void getFormattedPrice1DP() {
        int amount = 20;
        String expected = "0.2" + currency;

        var result = FormattingUtils.getFormattedPrice(amount);
        assertEquals(expected, result);
    }
    @Test
    void getFormattedPrice0DP() {
        int amount = 600;
        String expected = "6" + currency;

        var result = FormattingUtils.getFormattedPrice(amount);
        assertEquals(expected, result);
    }

    @Test
    void getFormattedPrice2BP() {
        int amount = 2700;
        String expected = "27" + currency;

        var result = FormattingUtils.getFormattedPrice(amount);
        assertEquals(expected, result);
    }

    @Test
    void getFormattedPrice2DPFull() {
        int amount = 42;
        String expected = "0.42" + currency;

        var result = FormattingUtils.getFormattedPrice(amount);
        assertEquals(expected, result);
    }

    @Test
    void getFormattedPrice4SFFull() {
        int amount = 1929;
        String expected = "19.29" + currency;

        var result = FormattingUtils.getFormattedPrice(amount);
        assertEquals(expected, result);
    }
}