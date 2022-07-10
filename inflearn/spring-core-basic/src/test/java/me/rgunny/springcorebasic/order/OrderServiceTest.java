package me.rgunny.springcorebasic.order;

import me.rgunny.springcorebasic.member.Grade;
import me.rgunny.springcorebasic.member.Member;
import me.rgunny.springcorebasic.member.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderServiceTest {

    MemberServiceImpl memberService = new MemberServiceImpl();
    OrderServiceImpl orderService = new OrderServiceImpl();

    @Test
    void createOrder() {
        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "itemA", 10000);
        Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);
    }
}
