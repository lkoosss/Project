package com.example.web.tus.controller;

import com.example.web.tus.service.TusService;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@Slf4j
public class TusController {

    @Autowired
    private TusService tusService;


    @GetMapping("/tusMain")
    public ModelAndView tus(HttpServletRequest req) {
        return new ModelAndView("/tus/tusMain");
    }

    @RequestMapping(value = "/tusMain/download", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<Object> fileDownload(HttpServletRequest req) {
        return tusService.fileDownload();
    }

    @RequestMapping(value = {"/tusMain/upload", "/tusMain/upload/**"}, method = {RequestMethod.GET, RequestMethod.POST,RequestMethod.PATCH, RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity fileUpload(HttpServletRequest req, HttpServletResponse res) throws IOException {
        tusService.fileUpload(req, res);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");

        return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
    }

    @RequestMapping(value = "/tusMain/checksum", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String checksum(HttpServletRequest req) {

        return tusService.checksum();
    }
}
