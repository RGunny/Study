package me.rgunny.servlet.web.frontcontroller.v5;

import me.rgunny.servlet.web.frontcontroller.ModelView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MyHandlerAdapter {

    /**
     * 어댑터가 해당 컨트롤러를 처리할 수 있는지 판단
     *
     * @param handler 컨트롤러
     * @return 컨트롤러 지원이 가능한 지
     */
    boolean support(Object handler);

    /**
     * 어댑터는 실제 컨트롤러를 호출하고, 그 결과로 ModelView 를 반환해야 한다.
     * 실제 컨트롤러가 ModelView 를 반환하지 못하면, 어댑터가 ModelView 를 직접 생성해서라도 반환해야 한다.
     * 이전에는 프론트 컨트롤러가 실제 컨트롤러를 호출했지만 이제는 이 어댑터를 통해서 실제 컨트롤러가 호출된다.
     */
    ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException ;
}
