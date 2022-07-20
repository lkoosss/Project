package com.example.sample.constant.controller;

import com.example.common.constant.Constant;
import com.example.common.util.FileUtil;
import com.example.sample.fileUtil.model.FileUtilSampleRequestModel;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@ComponentScan
public class ConstantSampleController {

    @RequestMapping(value = "/constant", method = {RequestMethod.POST, RequestMethod.GET})
    public void saveDataToFile(HttpServletRequest request) {
        String test = String.valueOf(Constant.ResponseCode.SUCCESS);
        System.out.println("test = " + test);
    }
}
