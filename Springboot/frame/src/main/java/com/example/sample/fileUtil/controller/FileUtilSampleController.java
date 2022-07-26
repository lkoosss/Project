package com.example.sample.fileUtil.controller;

import com.example.common.util.FileUtil;
import com.example.sample.fileUtil.model.FileUtilSampleRequestModel;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@ComponentScan
public class FileUtilSampleController {

    /**
     * <pre>
     * saveDataToFile
     * Data 저장
     * </pre>
     *
     * @param request
     * @param data
     * @return the boolean
     */
    @RequestMapping(value = "/fileUtil/saveDataToFile", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean saveDataToFile(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        byte[] saveData = data.getData().getBytes();
        String destinationPath = data.getDestinationPath();

        boolean result = FileUtil.saveDataToFile(saveData,destinationPath);
        return result;
    }

    /**
     * <pre>
     * loadDataFromFile
     * Data 조회
     * </pre>
     *
     * @param request
     * @param data
     * @return the string
     */
    @RequestMapping(value = "/fileUtil/loadDataFromFile", method = {RequestMethod.POST, RequestMethod.GET})
    public String loadDataFromFile(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String sourcePath = data.getSourcePath();

        String result = FileUtil.loadDataFromFile(sourcePath);
        return result;
    }

    /**
     * <pre>
     * compressFromFiles
     * File 압축(tar.gz)
     * </pre>
     *
     * @param request
     * @param data
     * @return the boolean
     */
    @RequestMapping(value = "/fileUtil/compressFromFiles", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean compressFromFiles(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String destinationPath = data.getDestinationPath();
        List<String> sourcePathList = data.getSourcePathList();

        boolean result = FileUtil.compressFromFiles(sourcePathList, destinationPath);
        return result;
    }

    /**
     * <pre>
     * compressFromDirectories
     * Directory 압축(tar.gz)
     * </pre>
     *
     * @param request
     * @param data
     * @return the boolean
     */
    @RequestMapping(value = "/fileUtil/compressFromDirectories", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean compressFromDirectories(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String sourcePath = data.getSourcePath();
        String destinationPath = data.getDestinationPath();

        boolean result = FileUtil.compressFromDirectory(sourcePath, destinationPath);
        return result;
    }

    /**
     * <pre>
     * decompressFromArchiveFile
     * Directory 압축해제(tar.gz)
     * </pre>
     *
     * @param request
     * @param data
     * @return the boolean
     */
    @RequestMapping(value = "/fileUtil/decompressFromArchiveFile", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean decompressFromArchiveFile(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String sourcePath = data.getSourcePath();
        String destinationPath = data.getDestinationPath();

        boolean result = FileUtil.decompressFromArchiveFile(sourcePath, destinationPath);
        return result;
    }

    /**
     * <pre>
     * encryptFromFile
     * 파일 암호화
     * </pre>
     *
     * @param request
     * @param data
     * @return the boolean
     */
    @RequestMapping(value = "/fileUtil/encryptFromFile", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean encryptFromFile(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String sourcePath = data.getSourcePath();
        String destinationPath = data.getDestinationPath();

        boolean result = FileUtil.encryptFromFile(sourcePath, destinationPath);
        return result;
    }

    /**
     * <pre>
     * decryptFromFile
     * 파일 복호화
     * </pre>
     *
     * @param request
     * @param data
     * @return the boolean
     */
    @RequestMapping(value = "/fileUtil/decryptFromFile", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean decryptFromFile(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String sourcePath = data.getSourcePath();
        String destinationPath = data.getDestinationPath();

        boolean result = FileUtil.decryptFromFile(sourcePath, destinationPath);
        return result;
    }

    /**
     * <pre>
     * loadDelimitedData
     * 파일 내용 구분자로 나눠 읽기
     * </pre>
     *
     * @param request
     * @param data
     */
    @RequestMapping(value = "/fileUtil/loadDelimitedData", method = {RequestMethod.POST, RequestMethod.GET})
    public void loadDelimitedData(HttpServletRequest request, @RequestBody FileUtilSampleRequestModel data) {
        String sourcePath = data.getSourcePath();
        String delimiter = data.getDelimiter();

        List<List<String>> loadData = FileUtil.loadDelimitedData(sourcePath, delimiter);
        System.out.println("loadData.toString() = " + loadData.toString());
    }
}
