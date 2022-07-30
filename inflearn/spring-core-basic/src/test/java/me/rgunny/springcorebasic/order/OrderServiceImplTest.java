package me.rgunny.springcorebasic.order;

import me.rgunny.springcorebasic.discount.FixDiscountPolicy;
import me.rgunny.springcorebasic.member.Grade;
import me.rgunny.springcorebasic.member.Member;
import me.rgunny.springcorebasic.member.MemberRepository;
import me.rgunny.springcorebasic.member.MemoryMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceImplTest {

    @Test
    @DisplayName("생성자 주입 테스트")
    void createOrder() {
//        OrderServiceImpl orderService = new OrderServiceImpl(); // 주입 데이터 누락 시, 컴파일 에러 발생

        MemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1L, "name", Grade.VIP));

        OrderService orderService = new OrderServiceImpl(memberRepository, new FixDiscountPolicy());
        Order order = orderService.createOrder(1L, "itemA", 10000);

        assertThat(order.getDiscountPrice()).isEqualTo(1000);
    }
}