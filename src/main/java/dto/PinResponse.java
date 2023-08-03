package dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Defines the attributes for Pin generation response
 */

@Getter
@Setter
public class PinResponse {

    private String pin;
    private String pinLength;
    private String pinOffset;
    private String responseCode;

}