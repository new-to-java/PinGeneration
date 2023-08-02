package dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Defines the attributes for Pin generation request
 */

@Getter
@Setter
public class PinRequest {

    String key;
    String pan;
    String [] decimalisationTable = null;
    String pinOffset;
    String pinLength;

}