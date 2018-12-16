package com.mercu.lego.model.match;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MatchMyItemSetItemId implements Serializable {
    private String itemNo; // vchar(24)
    private String setId; // vchar(12)
    private String matchId; // vchar(12)
    private String colorId; // vchar(12)

}
