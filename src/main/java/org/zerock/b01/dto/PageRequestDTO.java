package org.zerock.b01.dto;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 페이징 관련 정보(page,size)
 * 검색 종류(type)
 * 키워드(keyword)
 */


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;
    private String type; //검색 종류 t,c,w ,tc,tw,twc (문자열 하나로 처리후 추후 분리)
    private String keyword;

    private String link;

    /*
    현재 검색 조건들을 BoardRepository에서 String[] 으로 처리하므로 type문자열을 배열로 반환해주는 기능
     */
    public String[] getTypes() {
        if(type == null || type.isEmpty()) {
            return null;
        }
        return type.split("");
    }

    /*
    페이징 처리를 위해 Pageable 타입 반환 메서드 필요
     */
    public Pageable getPageable(String...props) {
        return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending());
    }

    /*
    검색 조건, 페이징 조건등을 문자열로 구성
     */
    public String getLink() {
        if (link == null) {
            StringBuilder builder = new StringBuilder();

            builder.append("page=" + this.page);
            builder.append(("&size=" + this.size));


            if (type != null && type.length() > 0) {
                builder.append("&type=" + this.type);
            }

            if (keyword != null) {
                try {
                    builder.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            link = builder.toString();
        }
        return link;


    }


}
