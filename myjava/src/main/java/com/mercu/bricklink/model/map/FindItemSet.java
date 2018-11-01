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
@Table(name = "MAP_FIND_ITEM_SET")
@IdClass(FindItemSetId.class)
public class FindItemSet {
    @Id
    private String itemNo;
    @Id
    private String setId;

    private String mapId;
    private String itemType;

    private String colorId;
    private Integer qty;
    private String setNo;

}
