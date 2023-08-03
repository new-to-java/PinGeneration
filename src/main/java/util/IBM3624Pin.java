package util;

import constants.PINConstants;
import dto.PinRequest;
import dto.PinResponse;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

        if (DataValidator.validatePinRequest(pinRequest)){
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
        }

        return pinResponse;

    }

    /**pan.substring(panLength - 13, panLength -1)
     * Derives PIN validation data from PAN
     */
    private String derivePinValidationData(String pan){

        int panLength = pan.length();
        if(panLength > 15) return pan + PINConstants.PAD_CHAR.repeat(16 - panLength);
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

    /**
     * Derive an IBM PIN offset from a natural PIN and customer selected PIN
     * @param customerPin Customer selected PIN
     * @param naturalPin Natural PIN
     * @return Derived PIN Offset value
     */
    public String deriveOffset(String customerPin, String naturalPin){

        StringBuilder pinOffset = new StringBuilder();

        if (customerPin.length() != naturalPin.length()){
            System.out.println("ERRR: NCPI01: Natural PIN and Customer PIN length must match.");
            System.exit(8);
        }

        for(int i = 0; i < naturalPin.length(); i++){
            int cpinDigit = Integer.parseInt(customerPin.substring(i, i + 1));
            int nPinDigit = Integer.parseInt(naturalPin.substring(i, i + 1));
            if (cpinDigit < nPinDigit) {
                cpinDigit += 10;
            }
            pinOffset.append(cpinDigit - nPinDigit);
        }

        return pinOffset.toString();

    }

    /** offset
     * Derive a Natural PIN from a customer selected PIN and PIN
     * @param customerPin Customer selected PIN
     * @param pinOffset PIN Offset
     * @return Natural PIN
     */

    public String deriveNaturalPin(String customerPin, String pinOffset){

        StringBuilder naturalPin = new StringBuilder();

        if (customerPin.length() != pinOffset.length()){
            System.out.println("ERRR: OFFC01: Customer PIN and PIN offset length must match.");
            System.exit(8);
        }

        for(int i = 0; i < pinOffset.length(); i++){
            int cpinDigit = Integer.parseInt(customerPin.substring(i, i + 1));
            int pinOffsetDigit = Integer.parseInt(pinOffset.substring(i, i + 1));
            if (cpinDigit < pinOffsetDigit) {
                cpinDigit += 10;
            }
            naturalPin.append(cpinDigit - pinOffsetDigit);
        }

        return naturalPin.toString();

    }

}