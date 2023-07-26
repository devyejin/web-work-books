package org.zerock.b02.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.plaf.PanelUI;

/**
 * 스프링 프로젝트의 경우, jackson-databind 라이브러리를 통한 변환 필요
 * 스프링 부트 프로젝트의 경우, web 항목에 포함되어 별도의 라이브러리가 필요 없음
 *
 * RestAPI의 경우 response content-type : application/json
 */

@RestController

public class SampleJSONController {

    @GetMapping("/helloArr")
    public  String[] helloArr() {
        return new String[]{"AAA","BBB","CCC"};
    }
}
