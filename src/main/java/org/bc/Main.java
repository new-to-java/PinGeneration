package org.bc;

import dto.PinRequest;
import dto.PinResponse;
import dto.PvvRequest;
import util.IBM3624Pin;
import util.VisaPvv;

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
        System.out.println("IBM 3624 PIN         : " + pinResponse.getPin());
        System.out.println("PIN Offset used      : " + pinResponse.getPinOffset());
        System.out.println("IBM 3624 Natural PIN : " + ibm3624Pin.deriveNaturalPin("432041891163", "123456789012"));
        System.out.println("IBM 3624 Offset      : " + ibm3624Pin.deriveOffset("432041891163", "319695112151"));
        VisaPvv visaPvv = new VisaPvv();
        PvvRequest pvvRequest = new PvvRequest();
        pvvRequest.setPan("1234567899876543");
        pvvRequest.setKey("0123456789ABCDEFFEDCBA9876543210");
        pvvRequest.setKeyIndex("1");
        pvvRequest.setPin("1111");
        System.out.println("VISA PVV             : " + visaPvv.calculateVisaPvv(pvvRequest));

    }

}