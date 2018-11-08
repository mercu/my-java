package com.mercu.member.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MemberRoleId implements Serializable {
    private String memberId; // vchar(24)
    private String roleName; // vchar(24)

}
