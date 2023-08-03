package util;

import constants.PINConstants;
import dto.PinRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class defines methods for data validation
 */
public class DataValidator {

    static String HEX_PATTERN = "[0-9a-fA-F]+";
    static String NUM_PATTERN = "[0-9]+";

    /**
     * Validate the PIN generation request object
     * @param pinRequest PIN generation request object
     * @return True if request object is valid, else return False
     */
    public static boolean validatePinRequest(PinRequest pinRequest){

        List<Boolean> validRequest = new ArrayList<>();
        validRequest.add(isNumeric(pinRequest.getPan()));
        validRequest.add(isNumeric(pinRequest.getPinOffset()));
        validRequest.add(isNumeric(pinRequest.getPinLength()));
        validRequest.add(isHexadecimal(pinRequest.getKey()));
        if (pinRequest.getDecimalisationTable() == null){
            System.out.println("WARN: DECE01: No decimalisation table supplied, using system default table.");
            System.out.println("Default Table: " + Arrays.toString(PINConstants.DEFAULT_DECIMALISATION_TABLE));
            pinRequest.setDecimalisationTable(PINConstants.DEFAULT_DECIMALISATION_TABLE);
        } else if ((pinRequest.getDecimalisationTable().length != 16)) {
            System.out.println("WARN: DECE02: Invalid decimalisation table supplied, using system default table.");
            System.out.println("Default Table: " + Arrays.toString(PINConstants.DEFAULT_DECIMALISATION_TABLE));
            pinRequest.setDecimalisationTable(PINConstants.DEFAULT_DECIMALISATION_TABLE);
        }
        validRequest.add(isHexadecimal(Arrays.toString(
                pinRequest.getDecimalisationTable()).replaceAll("[\\[\\]s :,]", "")));
        //Check if any of the validations have failed, if yes, return false, else at end of loop, return true
        for (Boolean aBoolean : validRequest) {
            if (!aBoolean) {
                return false;
            }
        }
        if (pinRequest.getPinLength().length() > 2){
            pinRequest.setPinLength(Integer.toString(PINConstants.MAX_PIN_LENGTH));
            System.out.println("WARN: PINL01: PIN length cannot exceed 16, resetting PIN length to 16.");
        }
        if (Integer.parseInt(pinRequest.getPinLength()) < PINConstants.MIN_PIN_LENGTH) {
            pinRequest.setPinLength(Integer.toString(PINConstants.MIN_PIN_LENGTH));
            System.out.println("WARN: PINL02: PIN length cannot be less than 4, resetting PIN length to 4.");
        } else if (Integer.parseInt(pinRequest.getPinLength()) > PINConstants.MAX_PIN_LENGTH) {
            pinRequest.setPinLength(Integer.toString(PINConstants.MAX_PIN_LENGTH));
            System.out.println("WARN: PINL03: PIN length cannot exceed 16, resetting PIN length to 16.");
        }
        // Basic validations passed, ensure that the assigned PIN length and PIN offset length match, else return error
        if (!pinRequest.isNaturalPin()
            && !(Integer.parseInt(pinRequest.getPinLength()) == pinRequest.getPinOffset().length())) {
            System.out.println("ERRR: PINOFF: PIN length and number of digits in offset must match.");
            System.exit(8);
        }
            return true;
    }

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