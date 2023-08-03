package util;

import constants.PINConstants;
import dto.PinRequest;
import dto.PinResponse;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class generates an IBM 3624 compatible PIN and Offset
 * Functions:
 * 1. Generate an IBM 3624 Natural PIN(Offset zero).
 * 2. Generate an  IBM 3624 PIN based on a supplied PIN Offset.
 * 3. Option to supply an external decimalisation table, if none supplied, the class will use the default
 * decimalisation table, which will substite only alpha Hex characters as follows:
 * - Numbers 0 through 9 will be retained as is
 * - A substituted with 0
 * - B substituted with 1
 * - C substituted with 2
 * - D substituted with 3
 * - E substituted with 4
 * - F substituted with 5
 * This class supports methods and attributes for generating an IBM 3624 compatible PIN and Offset, with a minimum
 * length of 4, and supports a maximum PIN length of 16.
 */

public class IBM3624Pin {

    public PinResponse generateIBM3624Pin(PinRequest pinRequest) throws NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        PinResponse pinResponse = new PinResponse();
        CryptoFunctions cryptoFunctions = new CryptoFunctions();

        if (validatePinRequest(pinRequest)){
            System.out.println("Valid input data");
            System.out.println(derivePinValidationData(pinRequest.getPan()));
            cryptoFunctions.setKey(pinRequest.getKey());
            cryptoFunctions.setInputData(derivePinValidationData(pinRequest.getPan()));
            pinResponse.setPin(calculateIntermediatePin(cryptoFunctions.tDEAEncrypt().toUpperCase(),
                    pinRequest.getDecimalisationTable()));
            pinResponse.setPinLength(pinRequest.getPinLength());
            pinResponse.setPinOffset(pinRequest.getPinOffset());
            if (!pinRequest.isNaturalPin()){
                pinResponse.setPin(addOffset(pinResponse.getPin(), pinRequest.getPinOffset()));
            } else {
                pinResponse.setPin(pinResponse.getPin().substring(0, Integer.parseInt(pinRequest.getPinLength())));
            }
            pinResponse.setResponseCode("SUCCESS");
        } else {
            pinResponse.setResponseCode("INVDATA");
        }

        return pinResponse;

    }

    private boolean validatePinRequest(PinRequest pinRequest){

        List<Boolean> validRequest = new ArrayList<>();
        validRequest.add(DataValidator.isNumeric(pinRequest.getPan()));
        validRequest.add(DataValidator.isNumeric(pinRequest.getPinOffset()));
        validRequest.add(DataValidator.isNumeric(pinRequest.getPinLength()));
        validRequest.add(DataValidator.isHexadecimal(pinRequest.getKey()));
        if (pinRequest.getDecimalisationTable() == null){
                System.out.println("WARN: DECE01: No decimalisation table supplied, using system default table.");
                System.out.println("Default Table: " + Arrays.toString(PINConstants.DEFAULT_DECIMALISATION_TABLE));
                pinRequest.setDecimalisationTable(PINConstants.DEFAULT_DECIMALISATION_TABLE);
        } else if ((pinRequest.getDecimalisationTable().length != 16)) {
            System.out.println("WARN: DECE02: Invalid decimalisation table supplied, using system default table.");
            System.out.println("Default Table: " + Arrays.toString(PINConstants.DEFAULT_DECIMALISATION_TABLE));
            pinRequest.setDecimalisationTable(PINConstants.DEFAULT_DECIMALISATION_TABLE);
        }
        validRequest.add(DataValidator.isHexadecimal(Arrays.toString(
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
        if (!pinRequest.isNaturalPin()) {
            System.out.println("ERRR: PINOFF: PIN length and number of digits in offset must match.");
            return Integer.parseInt(pinRequest.getPinLength()) == pinRequest.getPinOffset().length();
        } else{
            return true;
        }

    }

    /**pan.substring(panLength - 13, panLength -1)
     * Derives PIN validation data from PAN
     */
    private String derivePinValidationData(String pan){

        int panLength = pan.length();
        if(panLength > 15) return pan + "0".repeat(16 - panLength);
        else return pan.substring(panLength - 16, panLength);

    }

    /**
     * Derive natural PIN from encrypted PIN verification data, by substituting digits based on decimalisation table.
     * @param encryptedPinVerificationData PIN validation data, usually PAN, encrypted under PVK, 16 chars long
     * @param decimalisationTable Decimalisation table to be used for substitution of encrypted pin verification data
     *                            , primarily used to convert hexadecimal alphabetic chars A through F. However, this
     *                            may be used to substitute numeric characters as well
     * @return Derived intermediate PIN
     */
    private String calculateIntermediatePin(String encryptedPinVerificationData, String [] decimalisationTable){
        System.out.println(encryptedPinVerificationData);
        for (int i = 0; i < encryptedPinVerificationData.length(); i++){
            for (int j = 0; j < encryptedPinVerificationData.length(); j++){
                if (decimalisationTable[j].contains(String.valueOf(encryptedPinVerificationData.charAt(i)))) {
                    encryptedPinVerificationData = encryptedPinVerificationData
                            .replace(encryptedPinVerificationData.charAt(i), decimalisationTable[j].charAt(2));
                    break;
                }
            }

        }
        System.out.println(encryptedPinVerificationData);
        return encryptedPinVerificationData;

    }

    /**
     * Calculate PIN based on an input offset
     * @param naturalPin Natural PIN associated with the PAN
     * @param offset Offset value to be added to the PIN
     * @return PIN based on input offset
     */
    private String addOffset(String naturalPin, String offset){

        StringBuilder offsetAdjustedPin = new StringBuilder();

        for(int i = 0; i < offset.length(); i++){
            int pinDigit = Integer.parseInt(String.valueOf(naturalPin.charAt(i)));
            int offsetDigit = Integer.parseInt(String.valueOf(offset.charAt(i)));
            offsetAdjustedPin.append((pinDigit + offsetDigit) % 10);
        }

        return offsetAdjustedPin.toString();

    }

}