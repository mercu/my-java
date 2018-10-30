package com.mercu.bricklink.model.map;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SetItemId implements Serializable {
    private String setId;
    private String itemNo;

}
