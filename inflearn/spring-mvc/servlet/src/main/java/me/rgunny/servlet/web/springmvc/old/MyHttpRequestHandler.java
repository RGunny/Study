package me.rgunny.servlet.web.springmvc.old;

import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ http://localhost:8080/springmvc/request-handler 동작과정
 *
 * 1. 핸들러 매핑으로 핸들러 조회
 * 1.1 HandlerMapping 을 순서대로 실행해서, 핸들러를 찾는다.
 * 1.2 이 경우 빈 이름으로 핸들러를 찾아야하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는 BeanNameUrlHandlerMapping 가 실행에 성공하고 핸들러인 MyHttpRequestHandler 를 반환한다.
 *   (@Component("/springmvc/request-handler") 로 등록된 Spring Bean 이름인 /springmvc/request-handler 을 찾음)
 *
 * 2. 핸들러 어댑터 조회
 * 2.1 HandlerAdapter 의 supports() 를 순서대로 호출한다.
 * 2.2 HttpRequestHandlerAdapter 가 HttpRequestHandler 인터페이스를 지원하므로 대상이 된다.
 *
 * 3. 핸들러 어댑터 실행
 * 3.1 디스패처 서블릿이 조회한 HttpRequestHandlerAdapter 를 실행하면서 핸들러 정보도 함께 넘겨준다.
 * 3.2 HttpRequestHandlerAdapter 는 핸들러인 MyHttpRequestHandler 를 내부에서 실행하고 handle(), 그 결과(ModelAndView)를 반환한다.
 *
 */
@Component("/springmvc/request-handler")
public class MyHttpRequestHandler implements HttpRequestHandler {

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("MyHttpRequestHandler.handleRequest");
    }
}
