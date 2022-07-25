package me.rgunny.springcorebasic.singleton;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

public class StatelessServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatelessService statelessService1 = ac.getBean(StatelessService.class);
        StatelessService statelessService2 = ac.getBean(StatelessService.class);

        // ThreadA: 사용자A 10,000원 주문
        int userAPrice = statelessService1.order("userA", 10000);
        // ThreadB: 사용자B 20,000원 주문
        int userBPrice = statelessService2.order("userB", 20000);

        assertThat(userAPrice).isEqualTo(10000);
        assertThat(userBPrice).isEqualTo(20000);
    }

    static class TestConfig {

        @Bean
        public StatelessService statelessService() {
            return new StatelessService();
        }
    }

}
