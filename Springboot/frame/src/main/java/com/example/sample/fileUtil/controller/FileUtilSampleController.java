package com.example.sample.fileUtil.controller;

import com.example.common.util.FileUtil;
import com.example.sample.fileUtil.model.FileUtilSampleRequestModel;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

@RestController
@ComponentScan
public class FileUtilSampleController {

    ///////// Data 저장 /////////
    @RequestMapping(value = "/fileUtil/saveDataToFile", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean saveDataToFile(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        byte[] saveData = data.getData().getBytes();
        String destinationPath = data.getDestinationPath();

        boolean result = FileUtil.saveDataToFile(saveData,destinationPath);
        return result;
    }

    ///////// Data 조회 /////////
    @RequestMapping(value = "/fileUtil/loadDataFromFile", method = {RequestMethod.POST, RequestMethod.GET})
    public String loadDataFromFile(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String sourcePath = data.getSourcePath();

        String result = FileUtil.loadDataFromFile(sourcePath);
        return result;
    }

    ///////// File 압축(tar.gz) /////////
    @RequestMapping(value = "/fileUtil/compressFromFiles", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean compressFromFiles(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String destinationPath = data.getDestinationPath();
        List<String> sourcePathList = data.getSourcePathList();

        boolean result = FileUtil.compressFromFiles(sourcePathList, destinationPath);
        return result;
    }

    ///////// Directory 압축(tar.gz) /////////
    @RequestMapping(value = "/fileUtil/compressFromDirectories", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean compressFromDirectories(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String sourcePath = data.getSourcePath();
        String destinationPath = data.getDestinationPath();

        boolean result = FileUtil.compressFromDirectory(sourcePath, destinationPath);
        return result;
    }

    ///////// Directory 압축해제(tar.gz) /////////
    @RequestMapping(value = "/fileUtil/decompressFromArchiveFile", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean decompressFromArchiveFile(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String sourcePath = data.getSourcePath();
        String destinationPath = data.getDestinationPath();

        boolean result = FileUtil.decompressFromArchiveFile(sourcePath, destinationPath);
        return result;
    }

    ///////// 파일 암호화 /////////
    @RequestMapping(value = "/fileUtil/encryptFromFile", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean encryptFromFile(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String sourcePath = data.getSourcePath();
        String destinationPath = data.getDestinationPath();

        boolean result = FileUtil.encryptFromFile(sourcePath, destinationPath);
        return result;
    }

    ///////// 파일 복호화 /////////
    @RequestMapping(value = "/fileUtil/decryptFromFile", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean decryptFromFile(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String sourcePath = data.getSourcePath();
        String destinationPath = data.getDestinationPath();

        boolean result = FileUtil.decryptFromFile(sourcePath, destinationPath);
        return result;
    }

    @RequestMapping(value = "/fileUtil/loadDelimitedData", method = {RequestMethod.POST, RequestMethod.GET})
    public void loadDelimitedData(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String sourcePath = data.getSourcePath();
        String delimiter = data.getDelimiter();

        List<List<String>> loadData = FileUtil.loadDelimitedData(sourcePath, delimiter);
        System.out.println("loadData.toString() = " + loadData.toString());
    }
}
