package com.mercu.bricklink.model;

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
public class PartCategory {
    @Id
    private String id;
    private String type;
    private String name;
    private String parts;

}
