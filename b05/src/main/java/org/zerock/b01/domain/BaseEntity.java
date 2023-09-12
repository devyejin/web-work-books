package org.zerock.b01.domain;


import lombok.Cleanup;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;


@MappedSuperclass //공통으로 사용되는 기능 정의해놓고 상속해주기
@Getter
@EntityListeners(value = {AuditingEntityListener.class}) //자동으로 시간 값 지정 가능하도록 하는 기능
public class BaseEntity {

    @Column(name="regdate", updatable = false)
    @CreatedDate
    private LocalDateTime regDate;

    @Column(name="moddate")
    @LastModifiedDate
    private LocalDateTime modDate;
}
