package me.rgunny.servlet.web.v4.controller;

import me.rgunny.servlet.web.v4.ControllerV4;

import java.util.Map;

public class  MemberFormControllerV4 implements ControllerV4 {

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        return "new-form";
    }
}
