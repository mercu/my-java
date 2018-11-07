package com.mercu.bricklink.model.similar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "BL_SIMILAR_PART")
public class SimilarPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // int

    private Integer similarId; // int
    private String partNo; // vchar(24)
    private String partId; // vchar(24)

}
