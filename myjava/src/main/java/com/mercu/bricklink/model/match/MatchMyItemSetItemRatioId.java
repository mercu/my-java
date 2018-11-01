package com.mercu.bricklink.model.match;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MatchMyItemSetItemRatioId implements Serializable {
    private String matchId; // vchar(12)
    private String setId; // vchar(12)

}
