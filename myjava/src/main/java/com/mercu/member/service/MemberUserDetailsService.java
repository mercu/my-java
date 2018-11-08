package com.mercu.member.service;

import org.springframework.stereotype.Service;

@Service
public class MemberUserDetailsService { // implements UserDetailsService {
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
//        return Optional.ofNullable(memberRepository.findById(memberId).orElse(null))
//                .filter(member -> Objects.nonNull(member))

//                .map(member -> new SecurityMember(member))
//                .get();
//    }
}
