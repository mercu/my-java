package com.mercu.lego.model.match;

import com.mercu.bricklink.model.info.ColorInfo;
import com.mercu.bricklink.model.info.PartInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "MATCH_MY_ITEM_SET_ITEM")
@IdClass(MatchMyItemSetItemId.class)
public class MatchMyItemSetItem {
    @Id
    private String itemNo; // vchar(24)
    @Id
    private String setId; // vchar(12)
    @Id
    private String matchId; // vchar(12)

    private String itemType; // vchar(1)
    private String colorId; // vchar(12)
    private Integer qty; // int
    private String setNo; // vchar(64)

    @Transient
    private ColorInfo colorInfo;
    @Transient
    private PartInfo partInfo;
    @Transient
    private String imgUrl;

}
