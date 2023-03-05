package com.example.sample.api.controller;

import com.example.common.value.Constant.LogMarker;
import com.example.common.value.SystemProp;
import com.example.sample.api.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ApiController {

    @Autowired
    ApiService apiService;

    @GetMapping("/systemProp")
    public void printSystemProp() {
        log.info(LogMarker.api, "printSystemProp run");

        this.apiService.printSystemProp();
        log.info(LogMarker.api, "printSystemProp end");
    }

}
