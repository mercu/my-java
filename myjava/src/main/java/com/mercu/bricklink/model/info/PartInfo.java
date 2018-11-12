package com.mercu.bricklink.model.info;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.mercu.bricklink.model.CategoryType;
import com.mercu.bricklink.model.my.MyItem;

@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "BL_PART_INFO")
public class PartInfo implements AbstractInfo, Serializable {
    @Id
    @Column(name="id")
    private String id;
    private Integer categoryId;
    private String img;
//    @Column(name="partNo")
    private String partNo;
    private String partName;
    private Integer setQty;

    @Transient
    private List<MyItem> myItems;
    @Transient
    private Integer myItemsQty = 0;

    @Override
    public String getItemType() {
        return CategoryType.P.getCode();
    }

}
