package me.rgunny.servlet.web.frontcontroller.v3;

import me.rgunny.servlet.web.frontcontroller.ModelView;
import me.rgunny.servlet.web.frontcontroller.MyView;
import me.rgunny.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import me.rgunny.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import me.rgunny.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerServletV3", urlPatterns = "/front-controller/v3/*")
public class FrontControllerServletV3 extends HttpServlet {

    private Map<String, ControllerV3> controllerMap = new HashMap<>();

    public FrontControllerServletV3() {
        controllerMap.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save", new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members", new MemberListControllerV3());
    }

    /**
     * 서블릿 종속성 제거
     *  컨트롤러 입장에서 불필요한 HttpServletRequest, HttpServletResponse 를 제거하고, FrontController 에서 역할을 가져옴
     *  요청 파라미터 정보는 자바의 Map 으로 대신 넘기도록 하면 지금 구조에서는 컨트롤러가 서블릿 기술을 몰라도 동작할 수 있음
     *  그리고 request 객체를 Model 로 사용하는 대신에 별도의 Model 객체를 만들어서 반환
     *  --> 컨트롤러가 서블릿 기술을 전혀 사용하지 않도록 변경
     *  --> 구현 코드도 단순화, 쉬운 테스트 코드 작성
     * 뷰 이름 중복 제거
     *  컨트롤러에서 지정하는 뷰 이름 중복
     *  --> 컨트롤러는 뷰의 논리 이름을 반환하고, 실제 물리 위치의 이름은 프론트 컨트롤러에서 처리하도록 단순화
     *  --> 향후 뷰의 폴더 위치가 함께 이동해도 프론트 컨트롤러만 고치면 됨
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("FrontControllerServletV3.service");

        // front-controller/v3/members~
        String requestURI = request.getRequestURI();

        ControllerV3 controller = controllerMap.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // paramMap
        Map<String, String> paramMap = createParamMap(request);
        ModelView mv = controller.process(paramMap);

        String viewName = mv.getViewName(); // 논리 이름 new-form
        MyView view = viewResolver(viewName); // 물리 이름 /WEB-INF/views/new-form.jsp

        view.render(mv.getModel(), request, response);
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
