package com.zerock.api02.security;

import com.zerock.api02.domain.APIUser;
import com.zerock.api02.dto.APIUserDTO;
import com.zerock.api02.repository.APIUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 인증 자체는 스프링 시큐리티 기능을 활용
 */

@Service
@Log4j2
@RequiredArgsConstructor
public class APIUserDetailService implements UserDetailsService {

    private final APIUserRepository apiUserRepository;

    //username에 해당하는 사용작 있는경우 APIUserDTO반환하도록
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<APIUser> result = apiUserRepository.findById(username);
        APIUser apiUser = result.orElseThrow(() -> new UsernameNotFoundException("Cannot find mid"));

        log.info("APIUserDetailService apiUser............");

        APIUserDTO dto = new APIUserDTO(apiUser.getMid(),
                apiUser.getMpw(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))); //사용자를 반환할 때 권한 부여도!

        log.info(dto);

        return dto;
    }
}
