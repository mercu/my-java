package com.mercu.bricklink.model.info;

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
@Table(name = "BL_PART_INFO")
public class PartInfo implements AbstractInfo {
    @Id
    private String id;
    private Integer categoryId;
    private String img;
    private String partNo;
    private String partName;
    private Integer setQty;

}
