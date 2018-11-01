package com.mercu.bricklink.model.my;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "BL_MY_ITEM")
@NoArgsConstructor
public class MyItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String seq;

    private String itemType;
    private String itemNo;
    private String colorId;
    private Integer qty;
    private String whereType;
    private String whereCode;

}
