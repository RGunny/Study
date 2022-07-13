package me.rgunny.springcorebasic;

import me.rgunny.springcorebasic.discount.FixDiscountPolicy;
import me.rgunny.springcorebasic.member.MemberService;
import me.rgunny.springcorebasic.member.MemberServiceImpl;
import me.rgunny.springcorebasic.member.MemoryMemberRepository;
import me.rgunny.springcorebasic.order.OrderService;
import me.rgunny.springcorebasic.order.OrderServiceImpl;

public class AppConfig {

    public MemberService memberService() {
        return new MemberServiceImpl(new MemoryMemberRepository());
    }

    public OrderService orderService() {
        return new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
    }
}
