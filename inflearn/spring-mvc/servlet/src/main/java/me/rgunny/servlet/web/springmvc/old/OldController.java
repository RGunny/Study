package me.rgunny.servlet.web.springmvc.old;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HandlerMapping(핸들러 매핑)
 *  핸들러 매핑에서 이 컨트롤러를 찾을 수 있어야 함.
 *  ex) 스프링 빈의 이름으로 핸들러를 찾을 수 있는 핸들러 매핑이 필요.
 *
 * HandlerAdapter(핸들러 어댑터)
 *  핸들러 매핑을 통해서 찾은 핸들러를 실행할 수 있는 핸들러 어댑터가 필요.
 *  ex) Controller 인터페이스를 실행할 수 있는 핸들러 어댑터를 찾고 실행해야 함.
 *
 *
 * @ 컨트롤러 호출 과정
 *
 * HandlerMapping
 *  0 = RequestMappingHandlerMapping :  애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용
 *  1 = BeanNameUrlHandlerMapping : 스프링 빈의 이름으로 핸들러를 찾는다.
 *
 * HandlerAdapter
 *  0 = RequestMappingHandlerAdapter : 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용
 *  1 = HttpRequestHandlerAdapter : HttpRequestHandler 처리
 *  2 = SimpleControllerHandlerAdapter : : Controller 인터페이스(애노테이션 X, 과거에 사용) 처리
 *
 * 핸들러 매핑도, 핸들러 어댑터도 모두 순서대로 찾고 만약 없으면 다음 순서로 넘어간다.
 *
 * @ http://localhost:8080/springmvc/old-controller 동작과정
 *
 * 1. 핸들러 매핑으로 핸들러 조회
 * 1.1 HandlerMapping 을 순서대로 실행해서, 핸들러를 찾는다.
 * 1.2 이 경우 빈 이름으로 핸들러를 찾아야하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는 BeanNameUrlHandlerMapping 가 실행에 성공하고 핸들러인 OldController 를 반환한다.
 *  (@Component("/springmvc/old-controller") 로 등록된 Spring Bean 이름인 /springmvc/old-controller 을 찾음)
 *
 * 2. 핸들러 어댑터 조회
 * 2.1 HandlerAdapter 의 supports() 를 순서대로 호출한다.
 * 2.2 SimpleControllerHandlerAdapter 가 Controller 인터페이스를 지원하므로 대상이 된다.
 *
 * 3. 핸들러 어댑터 실행
 * 3.1 디스패처 서블릿이 조회한 SimpleControllerHandlerAdapter 를 실행하면서 핸들러 정보도 함께 넘겨준다.
 * 3.2 SimpleControllerHandlerAdapter 는 핸들러인 OldController 를 내부에서 실행하고 handle(), 그 결과(ModelAndView)를 반환한다.
 */
@Component("/springmvc/old-controller") // @Component: 이 컨트롤러는 /springmvc/old-controller 라는 이름의 스프링 빈으로 등록. 빈의 이름으로 URL을 매핑
public class OldController implements Controller {

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        return null;
    }
}
