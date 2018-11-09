package com.mercu.member.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityMember extends User {
    public SecurityMember(Member member) {
        super(member.getId(), member.getPassword(), makeGrantedAuthority(member.getRoles()));
    }

    private static List<GrantedAuthority> makeGrantedAuthority(List<MemberRole> memberRoles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        memberRoles.forEach(memberRole -> grantedAuthorities.add(new SimpleGrantedAuthority(memberRole.getRoleName())));
        return grantedAuthorities;
    }

}
