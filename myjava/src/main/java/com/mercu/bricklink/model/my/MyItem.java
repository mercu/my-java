package com.mercu.bricklink.model.my;

import com.mercu.bricklink.model.info.PartInfo;
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
    public static final String WHERE_CODE_WANTED = "wanted";

    @Id
    private String itemType;
    @Id
    private String itemNo;
    @Id
    private String colorId = "0";
    @Id
    private String whereCode;
    @Id
    private String whereMore;

    private Integer qty;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itemNo", referencedColumnName = "partNo",
            insertable = false, updatable = false)
    private PartInfo partInfo;

    public MyItem(String itemType, String itemNo, String colorId) {
        this.itemType = itemType;
        this.itemNo = itemNo;
        this.colorId = colorId;
    }

    public void setColorId(String colorId) {
        if (Objects.nonNull(colorId)) this.colorId = colorId;
    }
}
