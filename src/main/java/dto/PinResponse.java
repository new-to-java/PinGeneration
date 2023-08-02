package dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Defines the attributes for Pin generation response
 */

@Getter
@Setter
public class PinResponse {

    String pin;
    int pinLength;
    String pinOffset;
    String responseCode;

}