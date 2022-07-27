package me.rgunny.springcorebasic;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @ComponentScan 은 @Component 가 붙은 모든 클래스를 스프링 빈으로 등록한다.
 * 이 때, 스프링 빈의 기본 이름은 클래스명을 사용하되, 맨 앞글자만 소문자를 사용한다. (lower camel case)
 */
@Configuration
@ComponentScan(
        // @Configuration 이 붙은 설정 정보가 자동 등록되기 때문에, 기존 코드의 영향도 없이 테스트하기 위해 excludeFilters를 이용해 설정정보는 컴포넌트 스캔 대상에서 제외
        // @Configuration 내부에 @Component 애노테이션이 있어 스캔 대상이다.
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {


}
