package org.zerock.b02.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.zerock.b02.domain.Member;
import org.zerock.b02.domain.MemberRole;
import org.zerock.b02.dto.MemberJoinDTO;
import org.zerock.b02.repository.MemberRepository;


@Service
@Log4j2
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {

        //먼저, id가 존재하는지 확인
        boolean exist = memberRepository.existsById(memberJoinDTO.getMid());

        if(exist) throw new MidExistException(); //존재하면 예외 던지기(Controller로)

        //존재하지 않는다면, 가입시켜야지
        Member member = modelMapper.map(memberJoinDTO, Member.class);
        member.changePassword(passwordEncoder.encode(memberJoinDTO.getMpw())); //pwd암호화 어디서하나 했는데 서비스에서 암호화
        member.addRole(MemberRole.USER);

        log.info("member={}",member);
        log.info("member.Role={}",member.getRoleSet());

        memberRepository.save(member);

    }
}
