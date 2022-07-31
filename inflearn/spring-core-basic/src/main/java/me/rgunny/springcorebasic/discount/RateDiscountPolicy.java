package me.rgunny.springcorebasic.discount;

import me.rgunny.springcorebasic.annotation.MainDiscountPolicy;
import me.rgunny.springcorebasic.member.Grade;
import me.rgunny.springcorebasic.member.Member;
import org.springframework.stereotype.Component;

@Component
//@Qualifier("mainDiscountPolicy")
//@Primary // @Autowired 시 여러 빈이 매칭되면 우선권을 가진다.
@MainDiscountPolicy // @Qualifier 등록은 문자열이기 때문에 오타 등 실수에 대해 컴파일 시점에 체크가 불가능한다. -> 애노테이션 커스텀을 통해 컴파일 에러가 가능하게 만든다.
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
