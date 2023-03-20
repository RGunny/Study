package me.rgunny.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LogTestController {

    //    private final Logger log = LoggerFactory.getLogger(LogTestController.class);
//    private final Logger log = LoggerFactory.getLogger(getClass()); // 롬복이 제공하는 애노테이션인 @Slf4j 인터페이스가 자동으로 지원함

    /**
     * LOG Level : TRACE > DEBUG > INFO > WARN > ERROR
     * 개발 서버 : DEBUG
     * 운영 서버 : INFO
     *
     * 로그 사용 시 장점
     *   쓰레드 정보, 클래스 이름 등 부가 정보를 볼 수 있고, 출력 모양 조정이 가능함
     *   로그 레벨에 따라 개발 / 운영 서버 상황에 따라 설정으로 조절 가능
     *   단순 콘솔 출력이 아닌, 파일 / 네트워크 등 로그를 별도 위치에 적재 가능 (특히, 파일 적재시 일별/용량에 따라 로그를 분할 가능)
     *   성능도 일반 System.out 보다 좋음 (내부 버퍼링, 멀티 쓰레드 등)
     */
    @RequestMapping("/log-test")
    public String logTest() {
        String name = "Spring";

        System.out.println("name = " + name);

        // 자바는 + 연산을 먼저 진행하여 값을 만든 후 결과를 log.trace 인자로 호출하기 때문에 (로그레벨이 아니더라도 연산이 실제로 발생) + 로 로깅하면 안됨
        log.trace("trace log={}"+ name); // X
        log.trace("trace log={}", name); // O
        log.debug("debug log={}", name);
        log.info("info log={}", name);
        log.warn("warn log={}", name);
        log.error("error log={}", name);

        return "ok";
    }
}
