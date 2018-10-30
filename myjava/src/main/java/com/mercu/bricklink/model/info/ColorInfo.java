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
@Table(name = "BL_COLOR_INFO")
public class ColorInfo {
    @Id
    private String id;
    private String name;
    private String colorCode;

}
