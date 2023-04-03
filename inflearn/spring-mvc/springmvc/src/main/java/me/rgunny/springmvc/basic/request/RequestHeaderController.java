package me.rgunny.springmvc.basic.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Slf4j
@RestController
public class RequestHeaderController {

    /**
     * MultiValueMap
     *  map 과 유사한데, 하나의 키에 여러 값을 받을 수 있음.
     *  HTTP header,  HTTP 쿼리 파라미터와 같이 하나의 키에 여러 값을 받을 때 사용
     *  ex) keyA=value1&keyA=value2 (같은 헤더에 같은 값을 받을 때 사용)
     *  --> 리스트로 반한됨.
     */
    @RequestMapping("/headers")
    public String headers(HttpServletRequest request,
                          HttpServletRequest response,
                          HttpMethod httpMethod, // enum get post ...
                          Locale locale, // 언어 정보, localeResolver 로 확장 가능
                          @RequestHeader MultiValueMap<String, String> headerMap, // 모든 http 헤더를 MultiValueMap 형식으로 조회
                          @RequestHeader("host") String host, // 필수 헤더 단일
                          @CookieValue(value = "myCookie", required = false) String cookie) {

//        List<String> values = headerMap.get("keyA");

        log.info("request={}", request);
        log.info("response={}", response);
        log.info("httpMethod={}", httpMethod);
        log.info("locale={}", locale);
        log.info("headerMap={}", headerMap);
        log.info("header host={}", host);
        log.info("myCookie={}", cookie);

        return "ok";
    }
}
