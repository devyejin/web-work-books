package org.zerock.b02.service;

import org.zerock.b02.dto.MemberJoinDTO;

public interface MemberService {


    static class MidExistException extends Exception {} //MidExistException 을 innerClass로 선언했는데, id중복체크는 여기서만 써서 그런걸까?

    //중복된 id가 존재하면 예외 던지도록 선언
    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;
}
