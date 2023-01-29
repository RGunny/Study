package me.rgunny.servlet.basic.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rgunny.servlet.basic.HelloData;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "responseJsonServlet", urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Content-Type: application/json
        response.setContentType("application/json"); // HTTP 응답으로 JSON을 반환할 때는 content-type을 application/json 로 지정해야 함
        response.setCharacterEncoding("utf-8");

        HelloData helloData = new HelloData();
        helloData.setUsername("ryu");
        helloData.setAge(30);

        // {"username":"ryu", "age":20}
        String result = objectMapper.writeValueAsString(helloData); // Jackson 라이브러리가 제공하는 objectMapper.writeValueAsString() 를 사용하면 객체를 JSON 문자로 변경할 수 있음
        response.getWriter().write(result);
    }
}
