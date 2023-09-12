package org.zerock.b02.dto;

import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * 응답 내려줄 때, 요청 할 때의 page, size 등 기존 정보 유지해야되니까
 */
@Getter
@ToString
@Log4j2 //메서드 단위에 @builder 적용해놓고 클래스단위에 적용하니 상위 클래스에 적용되고 메서드단위가 무시되버림
public class PageResponseDTO<E> {
    private int page;
    private int size;
    private int total;

    //페이지의 시작,끝 번호
    private int start;
    private int end;

    private boolean prev;
    private boolean next;

    //내려줄 데이터
    private List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO,
                           List<E> dtoList,
                           int total) {

        log.info("동작중입니당"); //<--위에 @Builder달아놨을 때 위에 @Builder어노테이션 인식에서 여기도 안잡힘
        log.info("pageRequestDTO={}",pageRequestDTO);
        System.out.println("pageRequestDTO : " +pageRequestDTO);

//        if (total <= 0) return;

        this.dtoList = dtoList;

        this.page = pageRequestDTO.getPage(); //요청에서 뽑아내기
        this.size = pageRequestDTO.getSize();
        log.info("this.page={}",this.page);
        log.info("this.size={}",this.size);

        this.total = total;


        this.end = (int)(Math.ceil(this.page / 10.0)) * 10;
        this.start = this.end - 9;

        int last = (int)Math.ceil(total / (double)this.size);

//        this.end = (end > last) ? last : end;
        this.end = Math.min(end,last);

        this.prev = this.start > 1;

        this.next = total > this.end * this.size;

    }
}
