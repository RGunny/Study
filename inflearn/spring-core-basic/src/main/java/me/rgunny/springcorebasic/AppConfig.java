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

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemoryMemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }
}
