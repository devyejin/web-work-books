package org.zerock.b02.dto;


import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    //검색 종류는 문자열 하나로 받은 후 나중에 분리
    private String type; //검색 종류 t(제목), c(내용), w(글슨쓴이), tc, tw, cw(이건 왜 없지?), twc
    private String keyword;

    private String link;

    /**
     * Repository 검색을 호출 할 때 String[] types, Pageable 을 요구하기 때문에
     * 이 타입들을 반환하는 기능 구현
     */
    public String[] getTypes() {
        if(type == null || type.isEmpty())  return null; //문자열 길이가 0이면 isEmpty() true 반환

        return type.split("");
    }

    public Pageable getPageable(String ... props) {
        return PageRequest.of(this.page-1, this.size, Sort.by(props).descending()); //클라이언트는 1page요청하는데, JPA페이지는 0부터 시작해서
    }

    //검색 조건, 페이징 조건 등을 문자열로 구성하는 link
    public String getLink() {
        if(link == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("page="+this.page);
            builder.append("&size="+this.size);

            if(type != null && type.length() > 0) {
                builder.append("&type="+type);
            }

            if(keyword != null) {
                try {
                    builder.append("&keyword="+ URLEncoder.encode(keyword,"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            link = builder.toString();
        }
        return link;
    }
}
