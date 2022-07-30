package me.rgunny.springcorebasic.scan;

import me.rgunny.springcorebasic.AutoAppConfig;
import me.rgunny.springcorebasic.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class AutoAppConfigTest {


    /**
     * `AppConfig`에 같은 이름의 수동빈을 등록하면 자동빈보다 수동빈이 우선권을 가져, 수동빈이 자동빈을 오버라이딩 해버린다.
     *
     *  수동 빈 등록 시 로그
     * Overriding bean definition for bean 'memoryMemberRepository' with a different
     * definition: replacing
     *
     * 하지만, 의도치 않은 경우가 대부분이고 찾기가 힘들어
     * 최근 스프링 부트에서는 수동 빈 등록과 자동 빈 등록이 충돌나면 오류가 발생하도록 기본 값을 바꾸었다.
     *
     * 수동 빈 등록, 자동 빈 등록 오류시 스프링 부트 에러 (@SpringBootApplication 실행 시 컴파일 에러 발생)
     *   Consider renaming one of the beans or enabling overriding by setting
     * spring.main.allow-bean-definition-overriding=true
     */
    @Test
    @DisplayName("컴포넌트 스캔과 의존관계 자동 주입 확인")
    void basicScan() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class);

        MemberService memberService = ac.getBean(MemberService.class);

        assertThat(memberService).isInstanceOf(MemberService.class);
    }
}
