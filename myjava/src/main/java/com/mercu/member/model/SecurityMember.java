package com.mercu.member.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityMember { //extends User {
//    public SecurityMember(Member member) {
//        super(member.getId(), member.getPassword(), makeGrantedAuthority(member.getRoles()));
//    }
//
//    private static List<GrantedAuthority> makeGrantedAuthority(List<MemberRole> memberRoles) {
//        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        memberRoles.forEach(memberRole -> grantedAuthorities.add(new SimpleGrantedAuthority(memberRole.getRoleName())));
//        return grantedAuthorities;
//    }


}
