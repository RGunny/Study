package me.rgunny.springcorebasic.scan.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComponentFilterAppConfigTest {

    @Test
    @DisplayName("ComponentScan Include, Exclude 테스트")
    void filterScan() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(ComponentFilterAppConfig.class);

        BeanA beanA = ac. getBean("beanA", BeanA.class);

        assertThat(beanA).isNotNull();
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> ac.getBean("beanB", BeanB.class)
        );

    }

    /**
     * `includeFilters`에 `MyIncludeFilter` 애노테이션을 추가하여 BeanA가 스프링 빈에 등록됨,
     * `excludeFilters`에 `MyExcludeFilter` 애노테이션을 추가하여 BeanB가 스프링 빈에 등록되지 않음.
     *
     * FilterType 옵션
     * - ANNOTATION: 기본값, 애노테이션을 인식해서 동작한다. ex) org.example.SomeAnnotation
     * - ASSIGNABLE_TYPE: 지정한 타입과 자식 타입을 인식해서 동작한다. ex) org.example.SomeClass
     * - ASPECTJ: AspectJ 패턴 사용
     * ex) org.example..*Service+
     * - REGEX: 정규 표현식
     * ex) org\.example\.Default.*
     * - CUSTOM: TypeFilter 이라는 인터페이스를 구현해서 처리
     * ex) org.example.MyTypeFilter
     */
    @Configuration
    @ComponentScan(
            includeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyIncludeComponent.class),
            excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyExcludeComponent.class)
    )
    static class ComponentFilterAppConfig {

    }

}
