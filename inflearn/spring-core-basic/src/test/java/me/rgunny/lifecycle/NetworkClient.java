package me.rgunny.lifecycle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 초기화, 소명 인터페이스 단점 (InitializingBean, DisposableBean)
 * - 스프링 전용 인터페이스 -> 해당 코드가 스프링 전용 인터페이스에 의존
 * - 초기화, 소멸 메서드의 이름 변경 불가능 (afterProperties(), destroy())
 * - 개발자가 코드를 고칠 수 없는 외부 라이브러리에 적용 불가능
 * -> 인터페이스를 사용하는 초기화, 종료 방법은 스프링 초창기에 나온 방법들이고, 현재는 거의 사용하지 않는다.
 *
 * 설정 정보에 초기화 메서드, 종료 메서드 지정 특징 @Bean(initMethod = "init", destroyMethod = "close")
 * - 메서드 이름을 자유롭게 변경 가능
 * - 스프링 빈이 스프링 코드에 의존하지 않음
 * - 코드가 아닌, 설정 정보를 사용하기 때문에 코드를 고칠 수 없는 외부 라이브러리에도 초기화, 종료 메서드를 적용할 수 있다.
 *
 * @PostConstruct, @PreDestroy 애노테이션 특징
 * - 최신 스프링에서 가장 권장하는 방법
 * - 애노테이션 하나만 붙이면 되므로 매우 편리
 * - `javax.annotation.PostConstruct` -> javax.~ 패키지는 스프링에 종속적인 기술이 아닌, `JSR-250` 이라는 자바 표준이다. -> 스프링이 아닌 다른 컨테이너에서도 동작
 * - 컴포넌트 스캔과 잘 어울림
 * - 유일한 단점: 외부 라이브러리에는 적용하지 못함. 외부 라이브러리를 초기화, 종료 해야 하면 @Bean 의 기능을 사용해야 함(@Bean initMethod, destroyMethod)
 */
//public class NetworkClient implements InitializingBean, DisposableBean {
public class NetworkClient {

    private String url;

    public NetworkClient() {
        System.out.println("생성자 호출, url: " + url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // 서비스 시작시 호출
    public void connect() {
        System.out.println("connect:  " + url);
    }

    public void call(String message) {
        System.out.println("call: " + url + " message: " + message);
    }

    // 서비스 종료시 호출
    public void disconnect() {
        System.out.println("close: " + url);
    }

    // org.springframework.beans.factory.InitializingBean
    // 의존관계 주입 종료 후 호출
//    @Override
//    public void afterPropertiesSet() throws Exception {
    @PostConstruct
    public void init() {
        System.out.println("NetworkClient.init");
        connect();
        call("초기화 연결 메세지");
    }

    // org.springframework.beans.factory.DisposableBean
    // 빈이 료될 때 호출
//    @Override
//    public void destroy() throws Exception {
    @PreDestroy
    public void close() {
        System.out.println("NetworkClient.close");
        disconnect();
    }
}
