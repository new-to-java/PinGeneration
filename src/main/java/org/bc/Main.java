package org.bc;

import dto.PinRequest;
import dto.PinResponse;
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
        pinRequest.setPinLength("12");
        pinRequest.setPinOffset("123456789012");
        pinRequest.setPan("1234567899876543");
        pinRequest.setKey("0123456789ABCDEFFEDCBA9876543210");
        pinRequest.setNaturalPin(false);
        //pinRequest.setDecimalisationTable(PINConstants.DEFAULT_DECIMALISATION_TABLE);
        PinResponse pinResponse = ibm3624Pin.generateIBM3624Pin(pinRequest);
        System.out.println(pinResponse.getResponseCode());
        System.out.println(pinResponse.getPin());
        System.out.println(pinResponse.getPinLength());
        System.out.println(pinResponse.getPinOffset());

    }

}