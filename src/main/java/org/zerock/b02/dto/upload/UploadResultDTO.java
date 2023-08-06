package org.zerock.b02.dto.upload;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResultDTO {

    private String uuid;
    private String fileName;
    private boolean img;

    public String getLink() { //JSON처리시 link 속성으로 자동 처리

        //썸네일이 존재하는 경우, 없는 경우
        return img ? "s_"+uuid+"_"+fileName : uuid +"_"+fileName;
    }
}
