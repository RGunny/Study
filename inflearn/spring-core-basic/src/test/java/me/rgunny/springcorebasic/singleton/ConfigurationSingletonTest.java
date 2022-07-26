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

    @Test
    @DisplayName("@Configuration 설정 유무에 따른 싱글톤 보장 확인")
    void configurationDeepTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        AppConfig bean = ac.getBean(AppConfig.class);

        // bean = class me.rgunny.springcorebasic.AppConfig$$EnhancerBySpringCGLIB$$520df95c
        // -> 내가 만든 클래스가 아닌, 스프링이 CGLIB 라는 바이트코드 조작 라이브러리를 사용해서 AppConfig 클래스를 상속받은 임의의 다른 클래스를 만들고,
        // 그 다른 클래스를 스프링 빈으로 등록한 것이다.
        // -> 이 임의의 다른 클래스가 싱글톤이 보장되도록 해준다.
        // @Configuration을 사용하지 않고, @Bean으로만 생성하게되면 CGLIB가 아닌, 기존 순수한 클래스의 빈이 생성되지만, 싱글톤을 보장하지 않게 된다.
        System.out.println("bean = " + bean.getClass());
    }
}
