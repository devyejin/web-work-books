package com.zerock.api02.dto;


import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;
    @Builder.Default
    private int size = 10;

    private String type; // 검색 종류 :  t,c,w,tc,tw,twc
    private String keyword;

    //추가된 내용
    private LocalDate from; //기간별 검색 조회
    private LocalDate to;
    private Boolean completed; //완료여부

    public String[] getTypes() {

        if(type == null || type.isEmpty()) {
            return null;
        }

        return type.split("");
    }

    public Pageable getPageable(String...props) {
        return PageRequest.of(this.page-1,this.size, Sort.by(props).descending());
    }

    private String link;

    public String getLink() {

        if(link == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("page="+this.page);
            builder.append("&size="+this.size);

            if( type != null && type.length() > 0) {
                builder.append("&type="+type);
            }

            if(keyword != null) {
                try {
                    builder.append("&keyword=" + URLEncoder.encode(keyword,"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    log.error("URLEncorder error occur!!");
                }
            }
            link = builder().toString();
        }
        return link;
    }
}
