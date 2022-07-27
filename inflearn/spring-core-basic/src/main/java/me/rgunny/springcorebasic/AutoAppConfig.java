package me.rgunny.springcorebasic;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @ComponentScan 은 @Component 가 붙은 모든 클래스를 스프링 빈으로 등록한다.
 * 이 때, 스프링 빈의 기본 이름은 클래스명을 사용하되, 맨 앞글자만 소문자를 사용한다. (lower camel case)
 *
 * @ComponentScan baskPackages, basePackageClasses 등 지정 안 할 시, deafult 패키지 설정은 해당 @ComponentScan 사용 클래스 패키지 기준
 * -> 패키지 위치를 지정하지 않고, 설정 정보 클래스의 위치를 프로젝트 최상단(루트)에 두어 default 사용 권장.
 * 최근 스프링 부트도 이 방법을 기본으로 제공
 * -> 스프링 부트 대표 시작 정보인 @SpringBootApplication 을 프로젝스 시작 위치에 두는 것이 관례. (자동으로 생성됨)
 * -> 해당 애노테이션 내부에 @ComponentScan 이 들어있음 (필터 포함)
 * -> 스프링 부트 사용 시, @ComponentScan 을 사용할 필요가 굳이 없다.
 * -> 애노테이셔이 애노테이션을 들고 있는 것 등은의 인식은 자바 언어가 아닌, 스프링이 지원하는 기능 (애노테이션에는 상속관계가 없다)
 *
 * 컴포넌트 스캔 기본 대상 및 해당 애노테이션에 대한 스프링 부가기능
 * - @Component : 컴포넌트 스캔에서 사용
 * - @Controller : 스프링 MVC 컨트롤러에서 사용, 스프링 MVC 컨트롤러로 인식
 * - @Service : 스프링 비즈니스 로직에서 사용, 특별한 처리 x, 핵심 비즈니스 로직이 있을 계층으로 개발자의 파악 용도
 * - @Repository : 스프링 데이터 접근 계층에서 사용, 스프링 데이터 접근 계층으로 인식 및 데이터 계층의 예외를 스프링 예외로 변환
 * - @Configuration : 스프링 설정 정보에서 사용, 스프링 빈이 싱글톤을 유지하도록 추가 처리
 */
@Configuration
@ComponentScan(
        // 탐색할 패키지 시작위치 지정, 해당 패키지 포함 하위 패키지 모두 탐색
//        basePackages = "me.rgunny.springcorebasic.member",
        // 지정한 클래스의 패키지를 탐색 시작 위치로 지정 -> me.rgunny.springcorebasic
//        basePackageClasses = AutoAppConfig.class,
        // @Configuration 이 붙은 설정 정보가 자동 등록되기 때문에, 기존 코드의 영향도 없이 테스트하기 위해 excludeFilters를 이용해 설정정보는 컴포넌트 스캔 대상에서 제외
        // @Configuration 내부에 @Component 애노테이션이 있어 스캔 대상이다.
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {


}
