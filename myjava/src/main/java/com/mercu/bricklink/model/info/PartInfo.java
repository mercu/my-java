package com.mercu.bricklink.model.info;

import com.mercu.bricklink.model.my.MyItem;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

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
    @Column(name="partNo")
    private String partNo;
    private String partName;
    private Integer setQty;

    @OneToOne(mappedBy = "partInfo")
    private MyItem myItem;
}
