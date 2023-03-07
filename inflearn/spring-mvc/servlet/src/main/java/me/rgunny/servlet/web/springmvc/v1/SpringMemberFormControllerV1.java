package me.rgunny.servlet.web.springmvc.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Controller :
 *  - 스프링이 자동으로 스프링 빈으로 등록. 내부에 @Component 가 있어 Component Scan 대상이 됨.
 *  - 스프링 MVC 에서 애노테이션 기반 컨트롤러로 인식 -> RequestMappingHandlerMapping 에서 handler 대상으로 인식하게 됨.
 *  (RequestHandlerMappingHandlerMapping.isHandler(Class<?> beanType))
 *
 * @RequestMapping :
 *  - 요청 정보를 매핑. 해당 URL 이 호출되면 메서드가 호출됨.
 *  - RequestMappingHandlerMapping
 *  - RequestMappingHandlerAdapter
 *
 * RequestMappingHandlerMapping :
 *  - 스프링 빈 중에서(빈 등록 필수) @RequestMapping 또는 @Controller 가 클래스 레벨에 붙어 있는 경우 매핑 정보로 인식.
 *  - 클라이언트 요청 url 에 맞는 handler(controller) 를 찾음.
 *
 * RequestMappingHandlerAdapter :
 *  - DispatcherServlet.getHandlerAdapter(Object handler) -> HandlerMapping 에서 찾은 handler 를 넘겨 HandlerAdapter 반환
 *  - HandlerAdapter.handle() -> handler 호출
 *  - RequestMappingHandlerAdapter.getModelAndView() -> handler(controller) 의 결과 값을 ModelAndView 생성하여 DispatcherServlet 으로 반환.
 *  - getModelAndView() 를 통해 Controller 의 결과 값을 ModelAndView 생성하여 DispatcherServlet 로 반환.
 */
@Controller
//@Component
//@RequestMapping
public class SpringMemberFormControllerV1 {

    @RequestMapping("/springmvc/v1/members/new-form")
    public ModelAndView process() {
        return new ModelAndView("new-form");
    }

}
