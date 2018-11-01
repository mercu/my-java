package com.mercu.bricklink.model.my;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
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
    @Id
    private String itemType;
    @Id
    private String itemNo;
    @Id
    private String colorId = "0";
    @Id
    private String whereCode;

    private Integer qty;

    public void setColorId(String colorId) {
        if (Objects.nonNull(colorId)) this.colorId = colorId;
    }
}
