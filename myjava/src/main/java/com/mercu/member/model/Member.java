package com.mercu.member.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Data
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "MEMBER")
public class Member {
    @Id
    @Column(nullable = false, unique = true)
    private String id; // vchar(24)

    private String password; // vchar(256)
    private String nick; // vchar(64)

    private Date regDate; // timestamp
    private Date updDate; // timestamp

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "memberId")
    private List<MemberRole> roles;

}
