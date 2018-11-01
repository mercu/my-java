package com.mercu.bricklink.model.map;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FindItemSetId implements Serializable {
    private String itemNo;
    private String setId;

}
