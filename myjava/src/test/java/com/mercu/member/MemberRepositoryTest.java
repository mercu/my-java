package com.mercu.member;

import com.mercu.config.AppConfig;
import com.mercu.member.model.Member;
import com.mercu.member.model.MemberRole;
import com.mercu.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
@SpringBootTest
@Slf4j
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void insertTest() {
        Member member = new Member();
        member.setId("test");
        member.setPassword("test1");
        member.setNick("테스터");
        member.setRoles(Arrays.asList(new MemberRole(member.getId(), "TEST")));
        member.setRegDate(new Date());

        memberRepository.save(member);

        testMember();
    }

    @Test
    public void testMember() {
        Member result = memberRepository.findById("test").orElse(null);
        log.debug("result : " + result);
    }

    @Test
    @Ignore
    public void create() {
        Member member = new Member();
        member.setId("mercu");
//        member.setPassword(new BCryptPasswordEncoder().encode(""));
        member.setNick("머큐짱");
        member.setRoles(Arrays.asList(
                new MemberRole(member.getId(), "ADMIN"),
                new MemberRole(member.getId(), "USER")));
        member.setRegDate(new Date());

        memberRepository.save(member);
    }

}
