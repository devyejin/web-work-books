package org.zerock.b02.dto;

import lombok.*;

import java.util.List;

/**
 * 응답 내려줄 때, 요청 할 때의 page, size 등 기존 정보 유지해야되니까
 */
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
        if (total <= 0) return;

        this.dtoList = dtoList;

        this.page = pageRequestDTO.getPage(); //요청에서 뽑아내기
        this.size = pageRequestDTO.getSize();

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
