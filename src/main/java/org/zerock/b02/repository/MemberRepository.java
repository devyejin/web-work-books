package org.zerock.b02.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.b02.domain.Member;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member,String> {

    @EntityGraph(attributePaths = "roleSet") //join -> so, role정보 가져올 떄 트랜잭션 처리 or eager 로딩 필요없음
    @Query("select m from Member m where m.mid = :mid and m.social = false") //jpql
    Optional<Member> getWithRoles(@Param("mid") String mid);
}
