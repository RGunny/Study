package me.rgunny.springcorebasic.common;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;

/**
 * 적용 대상이 인터파에스가 아닌 클래스 -> TARGET_CLASS
 * 적용 대상이 인터페이스 -> INTERFACES
 * TARGET_CLASS
 *   : CGLIB 라이브러리로 내 클래스를 상속 받은 가짜 프록시 객체를 생성해 주입
 *   MyLogger 를 상속받은 가짜 프록시 클래스를 만들어두고, HTTP request 와 상관 없이 가짜 프록시 클래스를 다른 빈에 미리 주입해 둘 수 있다.
 *   스프링 컨테이너와 의존관계 주입도 myLogger 라는 이름으로 가짜 프록시 객체가 등록된다.
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MyLogger {

    private String uuid;
    private String requestURL;

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public void log(String message) {
        System.out.println("{" + uuid + "]" + "[" + requestURL + "] " + message);
    }

    @PostConstruct
    public void init() {
        uuid = UUID.randomUUID().toString();
        System.out.println("{" + uuid + "] request scope bean create: " + this);
    }

    @PreDestroy
    public void close() {
        System.out.println();
        System.out.println("{" + uuid + "] request scope bean close: " + this);
    }
}
