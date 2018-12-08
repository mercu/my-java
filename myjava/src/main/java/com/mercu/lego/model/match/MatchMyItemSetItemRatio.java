package com.mercu.lego.model.match;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "MATCH_MY_ITEM_SET_ITEM_RATIO")
@IdClass(MatchMyItemSetItemRatioId.class)
public class MatchMyItemSetItemRatio {
    @Id
    private String matchId; // vchar(12)
    @Id
    private String setId; // vchar(12)

    private String setNo; // vchar(64)
    private Integer matched; // int
    private Integer total; // int
    private Float ratio; // float

}
