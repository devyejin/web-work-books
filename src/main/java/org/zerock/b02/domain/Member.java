package org.zerock.b02.domain;


import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "roleSet")
public class Member extends BaseEntity{

    @Id
    private String mid; //memerid
    private String mpw;
    private String email;
    private boolean del; //탈퇴여부

    private boolean social;


    //여러개의 권한을 가질 수 있어서 Set으로
    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<MemberRole> roleSet = new HashSet<>(); //<-- 무슨 이유때문에 빈 객체로 초기화해놓는게 좋다고했는데, 찾아보기!

    public void changePassword(String mpw) {
        this.mpw = mpw;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changeDel(boolean del) {
        this.del = del;
    }

    public void addRole(MemberRole memberRole) {
        this.roleSet.add(memberRole);
    }

    public void changeSocial(boolean social) {
        this.social = social;
    }

}
