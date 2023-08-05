package org.zerock.b02.dto.upload;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 파일 업로드의 경우, MultipartFile API를 통해서 Controller에서 파라미터로 바로 받을 수 있음
 * 그런데, SwaggerUI와 같은 프레임워크로 테스트하는데 문제가 발생함 (TDD를 고려하는것도 중요)
 * 그래서 별도의 DTO를 통해 받는 방법으로 진행
 */
@Getter
@Setter
public class UploadFileDTO {

    //여러개의 첨부파일 -> List
    private List<MultipartFile> files;
}
