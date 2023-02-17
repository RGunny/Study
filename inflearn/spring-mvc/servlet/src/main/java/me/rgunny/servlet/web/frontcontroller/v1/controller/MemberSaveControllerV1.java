package me.rgunny.servlet.web.frontcontroller.v1.controller;

import me.rgunny.servlet.domain.member.Member;
import me.rgunny.servlet.domain.member.MemberRepository;
import me.rgunny.servlet.web.frontcontroller.v1.ControllerV1;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberSaveControllerV1 implements ControllerV1 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // parameter 를 받음
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        // 비즈니스 로직 호출
        Member member = new Member(username, age);
        memberRepository.save(member);

        // model 에 데이터를 보관
        request.setAttribute("member", member);

        // view 로 데이터 전달
        String viewPath = "/WEB-INF/views/save-result.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
