package me.rgunny.servlet.web.v4;

import java.util.Map;

public interface ControllerV4 {

    /**
     * 기본적인 구조는 V3와 같음.
     * 대신, interface 에 ModelView 가 없고, model 객체는 파라미터로 전달되기 때문에 그냥 사용하면 됨.
     * 결국, 컨트롤러가 ModelView 를 반환하지 않고, ViewName 만 반환.
     * @param paramMap
     * @param model
     * @return viewName
     */
    String process(Map<String, String> paramMap, Map<String, Object> model);
}
