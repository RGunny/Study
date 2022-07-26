package me.rgunny.springcorebasic;

import me.rgunny.springcorebasic.discount.DiscountPolicy;
import me.rgunny.springcorebasic.discount.RateDiscountPolicy;
import me.rgunny.springcorebasic.member.MemberService;
import me.rgunny.springcorebasic.member.MemberServiceImpl;
import me.rgunny.springcorebasic.member.MemoryMemberRepository;
import me.rgunny.springcorebasic.order.OrderService;
import me.rgunny.springcorebasic.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // @Bean memberService -> new MemoryMemberRepository()
    // @Bean orderService -> new MemoryMemberRepository()
    // -> 각각 다른 2개의 빈이 MemoryMemberRepository 를 생성하며 싱글톤이 꺠지는 것처럼 보이는데, 스프링 컨테이너는 어떻게 이를 해결할까?

    // 예상
    // call AppConfig.memberService
    // call AppConfig.memberRepository
    // call AppConfig.orderService
    // call AppConfig.memberRepository

    // 실제 결과
    // call AppConfig.memberService
    // call AppConfig.memberRepository
    // call AppConfig.orderService
    // -> 스프링이 싱글톤을 보장해줌을 확인

    @Bean
    public MemberService memberService() {
        System.out.println("call AppConfig.memberService");
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemoryMemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }
}
