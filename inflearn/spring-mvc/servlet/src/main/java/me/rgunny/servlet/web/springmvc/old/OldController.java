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
 * 2.1 HandlerAdapter 의 supports() 를 순서대로 호출한다.ㄱ
 * 2.2 SimpleControllerHandlerAdapter 가 Controller 인터페이스를 지원하므로 대상이 된다.
 *
 * 3. 핸들러 어댑터 실행
 * 3.1 디스패처 서블릿이 조회한 SimpleControllerHandlerAdapter 를 실행하면서 핸들러 정보도 함께 넘겨준다.
 * 3.2 SimpleControllerHandlerAdapter 는 핸들러인 OldController 를 내부에서 실행하고 handle(), 그 결과(ModelAndView)를 반환한다.
 */
@Component("/springmvc/old-controller") // @Component: 이 컨트롤러는 /springmvc/old-controller 라는 이름의 스프링 빈으로 등록. 빈의 이름으로 URL을 매핑
public class OldController implements Controller {

    /**
     * @ ViewResolver 동작과정
     *
     * 1. 핸들러 어댑터 호출
     *  핸들러 어댑터를 통해 new-form 이라는 논리 뷰 이름을 획득.
     *
     * 2. ViewResolver 호출
     *  new-form 이라는 뷰 이름으로 viewResolver 를 순서대로 호출.
     *  BeanNameViewResolver 는 new-form 이라는 이름의 스프링 빈으로 등록된 뷰를 찾아야 하는데 없다. InternalResourceViewResolver 가 호출.
     *
     * 3. InternalResourceViewResolver (내부에서 자원 이동하여 찾는것 ex. servlet -> jsp)
     *  이 뷰 리졸버는 InternalResourceViewResolver.buildView() 를 통해 InternalResourceView 를 반환.
     *
     * 4. 뷰 - InternalResourceView
     *  InternalResourceView 는 JSP 처럼 포워드 forward() 를 호출해서 처리할 수 있는 경우에 사용.
     *  실행 경로 : InternalResourceView.renderMergedOutputModel
     *  -> RequestDispatcher rd = getRequestDispatcher(request, dispatcherPath);
     *  -> rd.forward(request, response);
     *
     * 5. view.render()
     *  view.render() 가 호출되고 InternalResourceView 는 forward() 를 사용해서 JSP를 실행.
     *
     * @ References
     * InternalResourceViewResolver 는 만약 JSTL 라이브러리가 있으면 InternalResourceView 를 상속받은 JstlView 를 반환.
     * JstlView 는 JSTL 태그 사용시 약간의 부가 기능이 추가.
     *
     * 다른 뷰는 실제 뷰를 렌더링하지만, JSP 의 경우 forward() 통해서 해당 JSP 로 이동(실행)해야 렌더링이 됨.
     * JSP 를 제외한 나머지 뷰 템플릿들은 forward() 과정 없이 바로 렌더링 됨.
     *
     * Thymeleaf 뷰 템플릿을 사용하면 ThymeleafViewResolver 를 등록해야 함.
     * 최근에는 라이브러리만 추가하면 스프링 부트가 이런 작업도 모두 자동화함.
     */
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        return new ModelAndView("new-form"); // 논리적 이름 new-form 을 넣어 viewResolver 에서 물리적 이름 /WEB-INF/views/new-form 반환
    }
}
