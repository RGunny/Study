package me.rgunny.lifecycle;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class BeanLifeCycleTest {

    /**
     * 스프링 빈 라이프사이클
     *   : `객체생성` -> `의존관계 주입`
     *   스프링 빈은 객체를 생성하고, 의존관계 주입이 다 끝난 다음에야 필요한 데이터를 사용할 수 있는 준비가 완료된다.
     *   따라서 초기화 작업은 의존관계 주입이 모두 완료된 후 호춯애햐 한다.
     *   개발자가 의존관계 주입이 완료된 시점을 아는 방법은?
     *   -> 스프링은 의존관계 주입이 완료되면 스프링 빈에게 콜백 메서드를 통해 초기화 시점을 알려주는 다양한 기능을 제공
     *   -> 스프링은 스프링 컨테이너가 종료되기 직전에 소멸 콜백을 준다. (일반적 싱글톤)
     *   -> 따라서 안전하게 종료 작업 진행 가능
     *
     *  스프링 빈의 이벤트 라이프 사이클 (싱글톤)
     *    : 스프링 컨테이너 생성 -> 스프링 빈 생성(생성자 주입) -> 의존관계 주입(필드, 수정자 주입) -> 초기화 콜백 -> 사용 -> 소멸전 콜백 -> 스프링 종료
     *    초기화 콜백: 빈이 생성되고, 빈의 의존관계 주입이 완료된 후 호출
     *    소면전 콜백: 빈이 소멸되기 직전에 호출
     *
     * 스프링은 크게 3가지 방법으로 빈 생명주기 콜백을 지원
     * - 인터페이스(InitializingBean, DisposableBean)
     * - 설정 정보에 초기화 메서드, 종료 메서드 지정
     * - @PostConstruct, @PreDestroy 애노테이션 지원
    */
    @Test
    void lifeCycleTest() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LiceCycleConfig.class);
        NetworkClient client = ac.getBean(NetworkClient.class);
        ac.close(); // 기본 ApplicationContext 인터페이스에서 제공하지 않음
    }

    @Configuration
    static class LiceCycleConfig {

        @Bean
        public NetworkClient networkClient() {
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("http://hello-spring.dev");
            return networkClient;
        }
    }

}
