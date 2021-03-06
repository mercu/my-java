package com.mercu.lego.model.my;

import com.mercu.bricklink.model.info.ColorInfo;
import com.mercu.bricklink.model.info.PartInfo;
import com.mercu.lego.model.match.MatchMyItemSetItemRatio;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "BL_MY_ITEM")
@NoArgsConstructor
@IdClass(MyItemId.class)
public class MyItem {
    public static final String WHERE_CODE_STORAGE = "storage";
    public static final String WHERE_CODE_TEMPORARY = "temporary";
    public static final String WHERE_CODE_WANTED = "wanted";

    @Id
    private String itemNo;
    @Id
    private String colorId = "0";
    @Id
    private String whereCode;
    @Id
    private String whereMore;

    private String itemType;
    private Integer qty = 0;

    @Transient
    private ColorInfo colorInfo;
    @Transient
    private PartInfo partInfo;
    @Transient
    private String imgUrl;
    @Transient
    private String linkUrl;
    @Transient
    private MatchMyItemSetItemRatio matchMyItemSetItemRatio;

    public MyItem(String itemType, String itemNo, String colorId) {
        this.itemType = itemType;
        this.itemNo = itemNo;
        this.colorId = colorId;
    }

    public MyItem(String itemType, String itemNo, String colorId, String whereCode, String whereMore, Integer qty) {
        this.itemType = itemType;
        this.itemNo = itemNo;
        this.colorId = colorId;
        this.whereCode = whereCode;
        this.whereMore = whereMore;
        this.qty = qty;
    }

    public MyItem(String whereCode, String whereMore, Long qty) {
        this.whereCode = whereCode;
        this.whereMore = whereMore;
        this.qty = qty.intValue();
    }

    public void setColorId(String colorId) {
        if (Objects.nonNull(colorId)) this.colorId = colorId;
    }
}
