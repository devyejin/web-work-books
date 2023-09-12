package org.zerock.b02.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.zerock.b02.domain.Member;
import org.zerock.b02.domain.MemberRole;
import org.zerock.b02.repository.MemberRepository;
import org.zerock.b02.security.dto.MemberSecurityDTO;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 스프링 시큐리티에서 사용자정보를 UserDetailService인터페이스를 구현해서 사용하듯
 * OAuth2UserService 인터페이스를 구현해서 사용해야함, 이 인터페이스를 상속받은 DefaultOAuth2UserService 이용
 *
 * loadUser() 리턴 타입은 OAuth2User, loadUser() 에서는 카카오 서비스와 연동된 결과를 OAuth2UserRequest로 처리하기 때문에 이를 이용해서 원하는 정보(이메일)을 추출해야함
 *
 * 카카오 서비스(resource server)로부터 얻어온 이메일을 통해 같은 이메일을 가진 사용자를 찾아보고, 없는 경우 회원가입을 하고 MemberSecurityDTO를 반환
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("userRequest..........");
        log.info("userRequest={}",userRequest);

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();

        log.info("clientName={}",clientName);

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> paramMap = oAuth2User.getAttributes(); //여기꺼내보면 id, connected_at, properties, kakao_account 등 정보들이 key,value로 들어가있음, 필요한 정보 꺼내서 이용 =>여기선 email

//        paramMap.forEach((k,v) -> {
//            log.info("----------------------------------");
//            log.info((k + " : " + v));
//        });

        String email = null;

        switch (clientName) {
            case "kakao":
                email = getKakaoEmail(paramMap);
                break;
        }

        return generateDTO(email,paramMap);
    }

    //카카오 서비스(resource server)로부터 얻어온 이메일을 통해 같은 이메일을 가진 사용자를 찾아보고, 없는 경우 회원가입을 하고 MemberSecurityDTO를 반환
    private OAuth2User generateDTO(String email, Map<String, Object> params) {

        Optional<Member> result = memberRepository.findByEmail(email);

        //db에 해당 이메일 사용자가 없다면
        if(result.isEmpty()) {
            //회원 추가하기 mid=이메일주소, 패스워드는 1111
            Member member = Member.builder()
                    .mid(email)
                    .mpw(passwordEncoder.encode("1111"))
                    .email(email)
                    .social(true)
                    .build();
            member.addRole(MemberRole.USER);
            memberRepository.save(member);

            //MemberSecurityDTO 구성 및 반환
            MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(email, "1111", email, false, true,
                                                                    Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            memberSecurityDTO.setProps(params); //소셜 로그인 정보 넣어주기

            return memberSecurityDTO; //입력한 이메일이 없는경우 회원가입시킨 후 리턴
        } else {

            //존재하는 이메일인 경우(회원존재) -> 기존정보를 MemberSecurityDTO 로 변환해서 반환
            Member member = result.get();
            MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(member.getMid(),
                    member.getMpw(),
                    member.getEmail(),
                    member.isDel(),
                    member.isSocial(),
                    member.getRoleSet().stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.name())).collect(Collectors.toList()));

            return memberSecurityDTO;
        }

        //기존에 존재하는 회원이든 아니든 MemberSecurityDTO를 반환하기 때문에 추가작업 없이 서비스 이용 가능!


    }

    private String getKakaoEmail(Map<String, Object> paramMap) {
        log.info("KAKAO----------------------------------");

        Object value = paramMap.get("kakao_account");
        log.info("value={}",value);

        LinkedHashMap accountMap = (LinkedHashMap) value;

        String email = (String) accountMap.get("email");
        log.info("email={}",email);
        return email; //우리가 필요한건 email주소니까!

    }
}
