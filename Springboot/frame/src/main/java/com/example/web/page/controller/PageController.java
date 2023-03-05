package com.example.web.page.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PageController {

    @RequestMapping("/main")
    public String viewSearch(HttpServletRequest request) {

        return request.getRequestURI();
    }
}