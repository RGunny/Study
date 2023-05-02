package me.rgunny.thymeleaf.basic;

import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/basic")
public class BasicController {

    /**
     * Escape
     *  : HTML 은 < > 와 같은 특수 문자를 기반으로 정의되므로, 화면 생성 시 출력 데이터에 이러한 특수 문자가 있으면 주의해서 사용해야 함
     *  ex)
     *  웹 브라우저: Hello <b>Spring!</b>
     *  소스보기: Hello &lt;b&gt;Spring!&lt;/b&gt;
     *
     * HTML 엔티티
     * 웹브라우저는 < 를 HTML 테그의 시작으로 인식
     * ->  < 를 테그의 시작이 아닌 문자로 표현할수 있는 방법이 필요
     *   : HTML 엔티티
     *   : HTML 에서 사용하는 특수 문자를 HTML 엔티티로 변경하는 것을 이스케이프(escape)
     *
     * 타임리프 제공 escape
     *   th:text
     *   [[...]]
     */
    @GetMapping("text-basic")
    public String textBasic(Model model) {
        model.addAttribute("data", "Hello <b> Spring MVC2! </b>");
        return "basic/text-basic";
    }

    /**
     * Unescape (thymeleaf 기능)
     *   th:text th:utext
     *   [[...]] [(...)]
     */
    @GetMapping("text-unescaped")
    public String textUnescaped(Model model) {
        model.addAttribute("data", "Hello <b> Spring MVC2! </b>");
        return "basic/text-unescaped";
    }
}
