package client.utils;

import commons.Participant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class RoundUtils {
    /***
     * Rounds all BigDecimals in the given Participant-BigDecimal map
     * @param decimalMap the map containing BigDecimals
     * @param roundingMode the RoundingMode to use for balances
     * @return an Integer map, rounded half-up
     */
    public static HashMap<Participant, Integer> roundMap(HashMap<Participant, BigDecimal> decimalMap,
                                                  RoundingMode roundingMode) {
        HashMap<Participant, Integer> roundedMap = new HashMap<>();
        for(Map.Entry<Participant, BigDecimal> entry:
                decimalMap.entrySet()){
            BigDecimal decimalEntry = entry.getValue().setScale(0, roundingMode);
            Integer roundedValue = decimalEntry.intValue();
            roundedMap.put(entry.getKey(), roundedValue);
        }
        return roundedMap;
    }
}
