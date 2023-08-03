package util;

import constants.PINConstants;
import dto.PvvRequest;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Generate a VISA PVV based on an input PAN, PIN Verification Key Index, PIN and PIN Verification Key
 * When generating PVV from encrypted Transformation Security Parameter, the following apply:
 * - During the initial scan from left to right, numbers 0 through 9 will be selected
 * - When PVV is less than 4, second scan from left to right is done,taking into account hexadecimal digits A through F
 * - A substituted with 0
 * - B substituted with 1
 * - C substituted with 2
 * - D substituted with 3
 * - E substituted with 4
 * - F substituted with 5
 */
public class VisaPvv {

    /**
     * Calculate Visa PIN Verification Value
     * @param pvvRequest PVV request object
     * @throws NoSuchPaddingException When invalid padding is supplied
     * @throws IllegalBlockSizeException When block size of the data or the key is invalid
     * @throws NoSuchAlgorithmException When and invalid algorithm is specified
     * @throws BadPaddingException When data padding is invalid
     * @throws InvalidKeyException When key passed is invalid
     */
    public String calculateVisaPvv(PvvRequest pvvRequest) throws NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        CryptoFunctions cryptoFunctions = new CryptoFunctions();
        StringBuilder pvv = new StringBuilder();
        cryptoFunctions.setInputData(deriveTsp(pvvRequest));
        cryptoFunctions.setKey(pvvRequest.getKey());
        String encryptedTsp = cryptoFunctions.tDEAEncrypt();
        // Extract numeric digits, if any from the encrypted TSP data
        for (int i = 0; i < encryptedTsp.length(); i++) {
            String pvvDigit = encryptedTsp.substring(i, i + 1);
            if (DataValidator.isNumeric(pvvDigit)) {
                pvv.append(pvvDigit);
            }
            if (pvv.length() == 4) {
                break;
            }
        }
        // If PVV length is less than 4 digits, convert A through F hex chars to numbers by substituting x'10'
        if (pvv.length() < 4) {
            for (int i = 0; i < encryptedTsp.length(); i++) {
                String pvvDigit = encryptedTsp.substring(i, i + 1);
                if (!DataValidator.isNumeric(pvvDigit)) {
                    pvv.append(convertHexToDigits(pvvDigit));
                }
                if (pvv.length() == 4) {
                    break;
                }
            }
        }

        return pvv.toString();

    }

    /**
     * Derive Transformation Security Parameter based on PAN, PIN and PIN Verification Key Index
     * @return Derived TSP
     */
    private String deriveTsp(PvvRequest pvvRequest){

       return pvvRequest.getPan().substring(((pvvRequest.getPan().length() - 1)
               - PINConstants.MAX_PVV_PAN_LEN), pvvRequest.getPan().length() - 1)
               + pvvRequest.getKeyIndex()
               + (pvvRequest.getPin().substring(0, PINConstants.MAX_PVV_PIN_LEN));

    }

    /**
     * Convert hexadecimal character to numeric digit by subtracting x"0" from hexadecimal char
     * @param hexChar Hexadecimal character
     * @return Converted numeric digit
     */
    private String convertHexToDigits(String hexChar){
        return switch (hexChar) {
            case "A" -> "0";
            case "B" -> "1";
            case "C" -> "2";
            case "D" -> "3";
            case "E" -> "4";
            case "F" -> "5";
            default -> null;
        };
    }

}
