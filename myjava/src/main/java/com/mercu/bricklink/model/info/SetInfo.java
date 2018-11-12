package com.mercu.bricklink.model.info;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.mercu.bricklink.model.CategoryType;

@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "BL_SET_INFO")
public class SetInfo implements AbstractInfo, Serializable {
    @Id
    private String id;
    private String categoryId;
    private String img;
    private String setNo;
    private String blSetNo;
    private String setName;
    private String setBrief;

    @Override
    public String getItemType() {
        return CategoryType.S.getCode();
    }
}
