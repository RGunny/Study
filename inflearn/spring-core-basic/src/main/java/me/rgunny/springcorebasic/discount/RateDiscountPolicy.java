package me.rgunny.springcorebasic.discount;

import me.rgunny.springcorebasic.member.Grade;
import me.rgunny.springcorebasic.member.Member;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
//@Qualifier("mainDiscountPolicy")
@Primary // @Autowired 시 여러 빈이 매칭되면 우선권을 가진다.
public class RateDiscountPolicy implements DiscountPolicy {

    private int discountPercent = 10;

    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP) {
            return price * discountPercent / 100;
        } else {
            return 0;
        }
    }
}
