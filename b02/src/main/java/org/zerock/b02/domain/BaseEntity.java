package org.zerock.b02.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 거의 모든 텥이블에서 작성시간, 수정시간 칼럼을 사용
 * @MappedSuperclass 을 이용해서 공통 칼럼을 지정하고, 해당 클래스를 상속해 사용하도록 지정
 */
@MappedSuperclass
@EntityListeners(value ={AuditingEntityListener.class}) // audit 감시하다 -> 엔티티가 데이터베이스에 추가,변경될 때 자동으로 시간 값을 지정, 활성하기 하기위해서 프로젝트 설정에도 추가해줘야함
@Getter
abstract class BaseEntity {

    @CreatedDate
    @Column(name="regdate", updatable = false) //updatable = true (default)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name="moddate")
    private LocalDateTime modDate;
}
