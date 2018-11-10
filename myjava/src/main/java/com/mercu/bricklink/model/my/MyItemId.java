package com.mercu.bricklink.model.my;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MyItemId implements Serializable {
    private String itemType;
    private String itemNo;
    private String colorId;
    private String whereCode;
    private String whereMore;

}
