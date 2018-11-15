package com.mercu.lego.model;

import com.mercu.bricklink.model.category.AbstractCategory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "MY_PART_CATEGORY")
public class MyPartCategory implements AbstractCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // int

    private Integer blCategoryId; // int
    private String type; // vchar(12)
    private String name; // vchar(128)
    private Integer parts; // int
    private Integer depth; // int
    private Integer parentId; // int
    private String repImgs; //
    private Integer setQty; // int
    private Integer sortOrder; // int

}
