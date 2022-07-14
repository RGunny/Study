package me.rgunny.springcorebasic;

import me.rgunny.springcorebasic.member.Grade;
import me.rgunny.springcorebasic.member.Member;
import me.rgunny.springcorebasic.member.MemberService;
import me.rgunny.springcorebasic.order.Order;
import me.rgunny.springcorebasic.order.OrderService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OrderApp {

    public static void main(String[] args) {
//        AppConfig appConfig = new AppConfig();
//        MemberService memberService = appConfig.memberService();
//        OrderService orderService = appConfig.orderService();

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
        OrderService orderService = applicationContext.getBean("orderService", OrderService.class);

        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "itemA", 10000);

        System.out.println("order = " + order);
        System.out.println("order.calculatePrice() = " + order.calculatePrice());

    }
}
