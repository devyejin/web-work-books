package org.zerock.b02.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.b02.domain.Member;

import javax.transaction.Transactional;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member,String> {

    @EntityGraph(attributePaths = "roleSet") //join -> so, role정보 가져올 떄 트랜잭션 처리 or eager 로딩 필요없음
    @Query("select m from Member m where m.mid = :mid and m.social = false") //jpql
    Optional<Member> getWithRoles(@Param("mid") String mid);

    @EntityGraph(attributePaths = "roleSet") //join
    Optional<Member> findByEmail(String email);

    @Modifying // @Query는 select문일 때 사용 가능, @Modifying 어노테이션을 통해 DML(insert, update, delete)도  처리 가능
    @Transactional //join안걸었으니까 session유지되도록
    @Query("update Member m set m.mpw = :mpw where m.mid = :mid")
    void updatePassword(@Param("mpw") String password, @Param("mid") String mid);
}
