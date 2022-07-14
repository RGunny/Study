package me.rgunny.springcorebasic;

import me.rgunny.springcorebasic.discount.DiscountPolicy;
import me.rgunny.springcorebasic.discount.RateDiscountPolicy;
import me.rgunny.springcorebasic.member.MemberService;
import me.rgunny.springcorebasic.member.MemberServiceImpl;
import me.rgunny.springcorebasic.member.MemoryMemberRepository;
import me.rgunny.springcorebasic.order.OrderService;
import me.rgunny.springcorebasic.order.OrderServiceImpl;

public class AppConfig {

    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    private MemoryMemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }
}
