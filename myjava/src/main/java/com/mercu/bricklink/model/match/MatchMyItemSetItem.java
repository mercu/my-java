package com.mercu.bricklink.model.match;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "MATCH_MY_ITEM_SET_ITEM")
@IdClass(MatchMyItemSetItemId.class)
public class MatchMyItemSetItem {
    @Id
    private String itemNo;
    @Id
    private String setId;
    @Id
    private String matchId;

    private String itemType;

    private String colorId;
    private Integer qty;
    private String setNo;

}
