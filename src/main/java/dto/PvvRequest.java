package dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Defines the attributes for Pin generation request
 */

@Getter
@Setter
public class PvvRequest {

    private String key;
    private String keyIndex;
    private String pan;
    private String pin;

}