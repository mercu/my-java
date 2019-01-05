package com.mercu.bricklink.model.similar;

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
@Table(name = "BR_SIMILAR_COLOR")
public class SimilarColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // int

    private Integer similarId; // int
    private String colorId; // vchar(12)

}
