package me.rgunny.springcorebasic.singleton;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService statefulService1 = ac.getBean(StatefulService.class);
        StatefulService statefulService2 = ac.getBean(StatefulService.class);

        // ThreadA: 사용자A 10,000원 주문
        statefulService1.order("userA", 10000);
        // ThreadB: 사용자B 20,000원 주문
        statefulService2.order("userB", 20000);

        // ThreadA: 사용자A 주문 금액 조회
        int priceA = statefulService1.getPrice();
        // ThreadA: 사용자A는 10,000원을 기대했지만, 기대와 다르게 20,000원 출력
        // -> StatefulService의 price 필드는 공유되는 필드인데, 특정 클라이언트가 값을 변경하는 중
        // -> 스프링 빈은 항상 무상태(stateless)로 설계하자
        System.out.println("priceA = " + priceA);
        assertThat(statefulService1.getPrice()).isNotEqualTo(10000);
        assertThat(statefulService1.getPrice()).isEqualTo(20000);
    }

    static class TestConfig {

        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }


}