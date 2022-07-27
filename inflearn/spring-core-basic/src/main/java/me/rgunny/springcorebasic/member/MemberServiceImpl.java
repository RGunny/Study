package me.rgunny.springcorebasic.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    // 의존관계 자동 주입
    // 생성자에 @Autowired 지정시, 스프링 컨테이너가 자동으로 해당 스프링 빈을 찾아서 주입한다.
    // 이 때, 기본 조회 전략은 타입이 같은 빈을 찾아서 주입한다. ex) getBean(MemberRepository.class)
    @Autowired // ac.getBean(MemberRepository.class) 와 유사한 형태로 등록됨 (simple한 설명)
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    // for test
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
