package org.bc;

import dto.PinRequest;
import util.IBM3624Pin;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args ) throws NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        IBM3624Pin ibm3624Pin = new IBM3624Pin();
        PinRequest pinRequest = new PinRequest();
        pinRequest.setPinLength("4");
        pinRequest.setPinOffset("0000");
        pinRequest.setPan("1234567899876543");
        pinRequest.setKey("0123456789ABCDEFFEDCBA9876543210");
        //pinRequest.setDecimalisationTable(PINConstants.DEFAULT_DECIMALISATION_TABLE);
        ibm3624Pin.generateIBM3624Pin(pinRequest);

    }

}