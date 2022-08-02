package me.rgunny.lifecycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 초기화, 소명 인터페이스 단점 (InitializingBean, DisposableBean)
 * - 스프링 전용 인터페이스 -> 해당 코드가 스프링 전용 인터페이스에 의존
 * - 초기화, 소멸 메서드의 이름 변경 불가능 (afterProperties(), destroy())
 * - 개발자가 코드를 고칠 수 없는 외부 라이브러리에 적용 불가능
 * -> 인터페이스를 사용하는 초기화, 종료 방법은 스프링 초창기에 나온 방법들이고, 현재는 거의 사용하지 않는다.
 */
public class NetworkClient implements InitializingBean, DisposableBean {

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
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("NetworkClient.afterPropertiesSet");
        connect();
        call("초기화 연결 메세지");
    }

    // org.springframework.beans.factory.DisposableBean
    // 빈이 료될 때 호출
    @Override
    public void destroy() throws Exception {
        System.out.println("NetworkClient.destroy");
        disconnect();
    }
}
