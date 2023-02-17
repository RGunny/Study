package me.rgunny.servlet.web.frontcontroller.v2.controller;

import me.rgunny.servlet.domain.member.Member;
import me.rgunny.servlet.domain.member.MemberRepository;
import me.rgunny.servlet.web.frontcontroller.MyView;
import me.rgunny.servlet.web.frontcontroller.v2.ControllerV2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberSaveControllerV2 implements ControllerV2 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // parameter 를 받음
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        // 비즈니스 로직 호출
        Member member = new Member(username, age);
        memberRepository.save(member);

        // model 에 데이터를 보관
        request.setAttribute("member", member);

        return new MyView("/WEB-INF/views/save-result.jsp");
    }
}
