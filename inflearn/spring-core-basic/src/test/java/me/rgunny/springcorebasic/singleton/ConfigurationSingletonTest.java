package me.rgunny.springcorebasic.singleton;

import me.rgunny.springcorebasic.AppConfig;
import me.rgunny.springcorebasic.member.MemberRepository;
import me.rgunny.springcorebasic.member.MemberServiceImpl;
import me.rgunny.springcorebasic.order.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationSingletonTest {

    @Test
    @DisplayName("서로 다른 두 개의 빈에서 같은 new 호출 시 싱글톤이 꺠지는 지 않음을 확인")
    void configurationTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        // 테스트를 위한 구체타입 반환
        MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);
        MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class);

        MemberRepository memberRepository1 = memberService.getMemberRepository();
        MemberRepository memberRepository2 = orderService.getMemberRepository();

        System.out.println("memberService -> memberRepository1 = " + memberRepository1);
        System.out.println("orderService -> memberRepository2 = " + memberRepository2);
        System.out.println("memberRepository = " + memberRepository);

        // 모두 같은 인스턴스를 참고하고 있다.
        assertThat(memberRepository1).isSameAs(memberRepository2).isSameAs(memberRepository);
    }
}
