package com.mercu.bricklink.model.map;

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
@Table(name = "BL_SET_ITEM")
@IdClass(SetItemId.class)
public class SetItem {
    @Id
    private String setId;
    @Id
    private String itemNo;
    @Id
    private String colorId = "0";

    private String setNo;
    private String categoryType;
    private String image;
    private Integer qty;
    private String description;

    public SetItemId id() {
        return new SetItemId(this);
    }

}
