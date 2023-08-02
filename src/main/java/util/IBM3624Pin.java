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
        } else {
            pinResponse.setResponseCode("INVDATA");
        }
        System.out.println(derivePinValidationData(pinRequest.getPan()));
        cryptoFunctions.setKey(pinRequest.getKey());
        cryptoFunctions.setInputData(derivePinValidationData(pinRequest.getPan()));
        System.out.println(calculateIntermediatePin(cryptoFunctions.tDEAEncrypt().toUpperCase(),
                pinRequest.getDecimalisationTable()));

        pinResponse.setResponseCode("SUCCESS");

        return pinResponse;
    }

    private boolean validatePinRequest(PinRequest pinRequest){

        List<Boolean> validRequest = new ArrayList<>();
        validRequest.add(DataValidator.isNumeric(pinRequest.getPan()));
        validRequest.add(DataValidator.isNumeric(pinRequest.getPinOffset()));
        validRequest.add(DataValidator.isNumeric(pinRequest.getPinLength()));
        validRequest.add(DataValidator.isHexadecimal(pinRequest.getKey()));
        if (pinRequest.getDecimalisationTable() == null){
                System.out.println("DECERR: No decimalisation table supplied, using system default table.");
                System.out.println("Default Table: " + Arrays.toString(PINConstants.DEFAULT_DECIMALISATION_TABLE));
                pinRequest.setDecimalisationTable(PINConstants.DEFAULT_DECIMALISATION_TABLE);
        } else if ((pinRequest.getDecimalisationTable().length != 16)) {
            System.out.println("DECERR: Invalid decimalisation table supplied, using system default table.");
            System.out.println("Default Table: " + Arrays.toString(PINConstants.DEFAULT_DECIMALISATION_TABLE));
            pinRequest.setDecimalisationTable(PINConstants.DEFAULT_DECIMALISATION_TABLE);
        }
        validRequest.add(DataValidator.isHexadecimal(Arrays.toString(pinRequest.getDecimalisationTable())));
        //Check if any of the validations have failed, if yes, return false, else at end of loop, return true
        for (boolean checkResult: validRequest){
            if (!checkResult){
                return false;
            }
        }
        return true;

    }

    /**pan.substring(panLength - 13, panLength -1)
     * Derives PIN validation data from PAN
     */
    private String derivePinValidationData(String pan){

        int panLength = pan.length();
        if(panLength > 12){
            return pan.substring(panLength - 13, panLength -1) + PINConstants.PAD_WITH_F.repeat(4);
        } else {

            return pan.substring(0,panLength - 1) + PINConstants.PAD_WITH_F
                    .repeat(16 - pan.substring(0,panLength - 1).length());
        }

    }

    /**
     * Derive  intermediate pin from encrypted PIN verification data.
     * @param encryptedPinVerificationData Rightmost 12 digits of PAN, excluding check-digit,
     *                                     right padded with F till 16 chars long
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

        return encryptedPinVerificationData;

    }

}