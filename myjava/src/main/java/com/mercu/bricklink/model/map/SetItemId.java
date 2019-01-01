package com.mercu.bricklink.model.map;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class SetItemId implements Serializable {
    private String setId;
    private String itemNo;
    private String colorId;

    public SetItemId(SetItem setItem) {
        this.setId = setItem.getSetId();
        this.itemNo = setItem.getItemNo();
        this.colorId = setItem.getColorId();
    }
}
