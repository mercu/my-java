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
@Table(name = "BL_SET_INFO")
public class SetInfo {
    @Id
    private String id;
    private String categoryId;
    private String img;
    private String setNo;
    private String setName;
    private String setBrief;

}
