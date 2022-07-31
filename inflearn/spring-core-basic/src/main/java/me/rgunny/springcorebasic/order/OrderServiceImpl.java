package me.rgunny.springcorebasic.order;

import me.rgunny.springcorebasic.annotation.MainDiscountPolicy;
import me.rgunny.springcorebasic.discount.DiscountPolicy;
import me.rgunny.springcorebasic.member.Member;
import me.rgunny.springcorebasic.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
//@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    /**
     * 수정자 주입을 포함한 나머지 주입 방식은 모두 생성자 이후에 호출되므로,
     * 필드에 final 키워드를 사용할 수 없다. 오직 생성자 주입 방식만 final 키워드를 사용할 수 있다.
     * -> final 키워드를 사용하면, 생성자에서 혹시라도 값이 설정되지 않는 오류를 컴파일 시점에 막아준다
     *
     * 생성자 주입 방식은 프레임워크에 의존하지 않고, 순수한 자바 언어의 특징을 잘 살리는 방법이기도 하다.
     * 기본으로 생성자 주입을 사용하고, 필수 값이 아닌 경우에는 수정자 주입 방식을 옵션으로 부여하면 된다. 생성자 주입과 수정자 주입을 동시에 사용할 수 있다.
     * 항상 생성자 주입을 선택하는 것이 공식적으로 권장된다.
     * 그리고 가끔 옵션이 필요한 경우 수정자 주입을 선택하고, 필드 주입은 사용하지 않는게 좋다(official).
     */
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    // @RequiredArgsConstructor 롬복 애노테이션이 final 키워드가 붙은 필드를 모아 파라미터로 받는 생성자를 자동으로 생성해준다.
    // 조회 빈이 2개 이상일 시, @Qualifier 로 자동 매칭을 시켜 빈을 주입할 수 있다.
    @Autowired
//    public OrderServiceImpl(MemberRepository memberRepository, @Qualifier("mainDiscountPolicy") DiscountPolicy discountPolicy) {
    public OrderServiceImpl(MemberRepository memberRepository, @MainDiscountPolicy DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    // for test
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }

}
