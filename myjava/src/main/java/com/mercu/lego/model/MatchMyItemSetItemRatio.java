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
@Table(name = "MATCH_MY_ITEM_SET_ITEM_RATIO")
public class MatchMyItemSetItemRatio implements AbstractCategory {
    @Id
    private String matchId; // vchar(12)
    @Id
    private String setId; // vchar(12)

    private String setNo; // vchar(64)
    private Integer matched; // int
    private Integer total; // int
    private Float ratio; // float

}
