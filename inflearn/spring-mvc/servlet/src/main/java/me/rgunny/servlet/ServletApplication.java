package me.rgunny.servlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan // 서블릿 자동 등록
@SpringBootApplication
public class ServletApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServletApplication.class, args);
    }

    /**
     * @ ViewResolver - InternalResourceViewResolver
     * 스프링 부트는 InternalResourceViewResolver 라는 뷰 리졸버를 자동으로 등록하는데,
     * 이때 application.properties 에 등록한 spring.mvc.view.prefix , spring.mvc.view.suffix 설정 정보를 사용해서 등록한다.
     * <p>
     * 해당 빈 설정 이후 ViewResolver 가 ModelAndView 가 반환하는 논리명을 prefix, suffix 를 붙여 물리명으로 반환함.
     * @ Spring Boot 가 자동등록하는 ViewResolver 대표 (더많음)
     * 1 = BeanNameViewResolver : 빈 이름으로 뷰를 찾아서 반환한다. (예: 엑셀 파일 생성 기능에 사용)
     * 2 = InternalResourceViewResolver : JSP를 처리할 수 있는 뷰를 반환한다.
     */
    /*
    @Bean
    ViewResolver internalResourceViewResolver() {
        return new InternalResourceViewResolver("/WEB-INF/views", ".jsp");
    }
    */

    /*
    @Bean
    SpringMemberFormControllerV1 springMemberFormControllerV1() {
        return new SpringMemberFormControllerV1();
    }
    */
}
