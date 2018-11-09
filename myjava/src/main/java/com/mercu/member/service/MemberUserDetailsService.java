package com.mercu.member.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mercu.member.model.SecurityMember;
import com.mercu.member.repository.MemberRepository;

@Service
public class MemberUserDetailsService implements UserDetailsService {
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        return Optional.ofNullable(memberRepository.findById(memberId).orElse(null))
                .filter(member -> Objects.nonNull(member))
                .map(member -> new SecurityMember(member))
                .get();
    }
}
