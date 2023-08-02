package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class defines methods for data validation
 */
public class DataValidator {

    static String HEX_PATTERN = "[0-9a-fA-F]+";
    static String NUM_PATTERN = "[0-9]+";

    /**
     * Check if an input data supplied is numeric
     * @param checkData Input data to be verified as numeric
     * @return Returns true, if input data is numeric
     */
    public static boolean isNumeric(String checkData){
        Pattern hexPattern = Pattern.compile(NUM_PATTERN);
        Matcher hexPatternMatcher = hexPattern.matcher(checkData);
        return hexPatternMatcher.matches();
    }

    /**
     * Verifies if an input data supplied contains valid hexadecimal characters only
     * @param checkData Input data to be verified as hexadecimal
     * @return Returns true, if input data contains valid hexadecimal characters
     */
    public static boolean isHexadecimal(String checkData){
        Pattern hexPattern = Pattern.compile(HEX_PATTERN);
        Matcher hexPatternMatcher = hexPattern.matcher(checkData);
        return hexPatternMatcher.matches();
    }

}