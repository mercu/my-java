package com.mercu.lego.model.match;

import com.mercu.bricklink.model.CategoryType;
import com.mercu.bricklink.model.info.ColorInfo;
import com.mercu.bricklink.model.info.MinifigInfo;
import com.mercu.bricklink.model.info.PartInfo;
import com.mercu.lego.model.my.MyItem;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

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
    @Id
    private String colorId = "0"; // vchar(12)

    private String itemType; // vchar(1)
    private Integer qty = 0; // int
    private String setNo; // vchar(64)

    @Transient
    private ColorInfo colorInfo;
    @Transient
    private PartInfo partInfo;
    @Transient
    private MinifigInfo minifigInfo;
    @Transient
    private String imgUrl;
    @Transient
    private List<MyItem> myItems;
    @Transient
    private Integer partQty;
    @Transient
    private boolean matched;
    @Transient
    private Integer sortOrder = 0;
    @Transient
    private Boolean filtered = true;

    public boolean isPart() {
        return CategoryType.P.getCode().equals(this.getItemType());
    }

    public boolean isMinifig() {
        return CategoryType.M.getCode().equals(this.getItemType());
    }
}
