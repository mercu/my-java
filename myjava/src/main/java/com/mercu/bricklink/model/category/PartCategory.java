package com.mercu.bricklink.model.category;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "BL_PART_CATEGORY")
public class PartCategory implements AbstractCategory {
    @Id
    private Integer id;

    private String type;
    private String name;
    private String parts;
    private Integer depth;
    private Integer parentId;
    private String repImgs;
    private Integer setQty;

}
