package com.zerock.api02.repository;

import com.zerock.api02.domain.APIUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface APIUserRepository extends JpaRepository<APIUser,String> {
}
