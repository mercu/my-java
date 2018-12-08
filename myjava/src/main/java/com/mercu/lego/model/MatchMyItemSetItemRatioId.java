package com.mercu.lego.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MatchMyItemSetItemRatioId implements Serializable {
    private String matchId; // vchar(12)
    private String setId; // vchar(12)

}
