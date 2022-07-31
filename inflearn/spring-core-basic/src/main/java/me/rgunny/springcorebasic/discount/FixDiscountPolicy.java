package me.rgunny.springcorebasic.discount;

import me.rgunny.springcorebasic.member.Grade;
import me.rgunny.springcorebasic.member.Member;
import org.springframework.stereotype.Component;

@Component
//@Qualifier("fixDiscountPolicy")
public class FixDiscountPolicy implements DiscountPolicy {

    private int discountFixAmount = 1000; // 1000원 고정 할인

    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP) {
            return discountFixAmount;
        } else {
            return 0;
        }
    }
}
