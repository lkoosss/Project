package com.example.web.mxgraph.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
public class mxgraphController {

    @GetMapping("/mxgraph")
    public ModelAndView mxgraphPage(HttpServletRequest req, HttpServletResponse res) {
        return new ModelAndView("/mxgraph/mxgraphMain");
    }
}
