package me.rgunny.springcorebasic.order;

import me.rgunny.springcorebasic.discount.DiscountPolicy;
import me.rgunny.springcorebasic.discount.FixDiscountPolicy;
import me.rgunny.springcorebasic.member.Member;
import me.rgunny.springcorebasic.member.MemberRepository;
import me.rgunny.springcorebasic.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository = new MemoryMemberRepository();
    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
