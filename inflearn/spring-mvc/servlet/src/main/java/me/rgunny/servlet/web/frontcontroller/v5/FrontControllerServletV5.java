package me.rgunny.servlet.web.frontcontroller.v5;

import me.rgunny.servlet.web.frontcontroller.ModelView;
import me.rgunny.servlet.web.frontcontroller.MyView;
import me.rgunny.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import me.rgunny.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import me.rgunny.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import me.rgunny.servlet.web.frontcontroller.v5.adapter.ControllerV3HandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 어댑터 패턴 사용
 */
@WebServlet(name = "frontControllerServletV5",  urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {

    private final Map<String, Object> handlerMappingMap = new HashMap<>();
    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    public FrontControllerServletV5() {
        initHandlerMappingMap();
        initHandlerAdapters();
    }

    private void initHandlerMappingMap() {
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
    }

    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerV3HandlerAdapter());
    }

    /**
     * 핸들러 어댑터:
     *   중간에 어댑터 역할을 하는 어댑터가 추가되었는데 이름이 핸들러 어댑터이다.
     *   여기서 어댑터 역할을 해주는 덕분에 다양한 종류의 컨트롤러를 호출할 수 있다.
     * 핸들러:
     *   컨트롤러의 이름을 더 넓은 범위인 핸들러로 변경했다.
     *   그 이유는 이제 어댑터가 있기 때문에 꼭 컨트롤러의 개념 뿐만 아니라
     *   어떠한 것이든 해당하는 종류의 어댑터만 있으면 다 처리할 수 있기 때문이다.
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 0. HTTP 요청
        System.out.println("FrontControllerServletV5.service");

        // 1. 핸들러 조회 (핸들러 매핑 정보)

        // front-controller/v5/v3/members~
        Object handler = getHandler(request); // MemberFormControllerV3
        if (handler == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 2. 핸들러를 처리할 수 있는 핸들러 어댑터 조회 (핸들러 어댑터 목록)
        MyHandlerAdapter adapter = getHandlerAdapter(handler);

        // 3. handler(adapter)
        // 4. handler 호출
        // 5. ModelView 반환
        ModelView mv = adapter.handle(request, response, handler);

        // 6. viewResolver 호출
        // 7. MyView 반환
        String viewName = mv.getViewName(); // 논리 이름 new-form
        MyView view = viewResolver(viewName); // 물리 이름 /WEB-INF/views/new-form.jsp

        // 8. render(model) 호출
        view.render(mv.getModel(), request, response);

        // 9. HTTP 응답
    }

    private MyHandlerAdapter getHandlerAdapter(Object handler) {
        for (MyHandlerAdapter adapter : handlerAdapters) {
            if (adapter.support(handler)) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("Can not find handler adapter. handler=" + handler);
    }

    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return  handlerMappingMap.get(requestURI);
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

}
