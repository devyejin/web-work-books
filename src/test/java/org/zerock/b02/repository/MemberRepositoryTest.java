package org.zerock.b02.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.b02.domain.Member;
import org.zerock.b02.domain.MemberRole;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void insertMembers() {

        IntStream.rangeClosed(1,100).forEach(i -> {

            Member member = Member.builder()
                    .mid("member" + i)
                    .mpw(passwordEncoder.encode("1111"))
                    .email("email" + i + "@aaa.bbb")
                    .build();

            member.addRole(MemberRole.USER);

            if( i >= 90) member.addRole(MemberRole.ADMIN); // 10명회원은 USER,ADMIN 두 가지의 권한

            memberRepository.save(member);
        });
    }

    @Test
    void testRead() {

        Optional<Member> findMember = memberRepository.getWithRoles("member100");
        Member member = findMember.orElseThrow();
        log.info("member={}",member);
        log.info("member.getRoleSet={}",member.getRoleSet());

        member.getRoleSet().forEach(memberRole -> log.info("memberRole.name={}",memberRole.name()));
    }
}