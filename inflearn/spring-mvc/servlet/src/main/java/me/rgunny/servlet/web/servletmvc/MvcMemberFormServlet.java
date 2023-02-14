package me.rgunny.servlet.web.servletmvc;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * http://localhost:8080/servlet-mvc/members/new-form
 */
@WebServlet(name = "mvcMemberFormServlet", urlPatterns = "/servlet-mvc/members/new-form")
public class MvcMemberFormServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String viewPath = "/WEB-INF/views/new-form.jsp"; // WAS 서버 룰 : /WEB-INF 경로 아래 자원들은 외부에서 직접 호출 불가능 (컨트롤러를 통해 호출 가능)
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath); // controller -> view
        dispatcher.forward(request, response); // 다른 servlet 이나 jsp 로 이동할 수 있는 기능 (서버 내부에서 다시 호출) -> redirect 와 달리 클라이언트가 인지 x
        // redirect 는 실제 클라이언트(웹 브라우저)에 응답이 나갔다가 client 가 redirect 경로로 다시 요청 (URL 경로도 실제 변경됨)
    }
}
