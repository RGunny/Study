package me.rgunny.servlet.web.frontcontroller;

import java.util.HashMap;
import java.util.Map;

/**
 * 서블릿 종속성 제거를 위한 Model 객체
 * 1. 컨트롤러에서 서블릿에 종속적인 HttpServletRequest 사용을 벗어남
 * 2. Model 도 request.setAttribute() 를 통해 데이터를 저장하고 뷰에 전달하는 역할 제거
 */
public class ModelView {

    private String viewName;
    private Map<String, Object> model = new HashMap<>();

    public ModelView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}
