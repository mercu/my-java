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
@Table(name = "BL_SET_CATEGORY")
public class SetCategory implements AbstractCategory {
    @Id
    private String id;
    private Integer depth;
    private String type;
    private String name;
    private String parts;

}
