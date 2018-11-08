package com.mercu.member.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "MEMBER_ROLE")
@IdClass(MemberRoleId.class)
public class MemberRole {
    @Id
    private String memberId; // vchar(24)
    @Id
    private String roleName; // vchar(24)

}
