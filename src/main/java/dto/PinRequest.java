package dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Defines the attributes for Pin generation request
 */

@Getter
@Setter
public class PinRequest {

    private String key;
    private String pan;
    private String [] decimalisationTable = null;
    private String pinOffset;
    private String pinLength;
    private boolean naturalPin;

}