package com.mercu.bricklink.model.match;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MatchMyItemSetItemId implements Serializable {
    private String itemNo;
    private String setId;

}
