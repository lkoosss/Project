package com.example.web.tus.service;

import com.example.common.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class TusService {

    private final TusFileUploadService tusFileUploadService;


    public void fileDownload(HttpServletRequest req, HttpServletResponse res) {
        Path filePath = Paths.get("/home/ssoh/Sample/CentOS-7-x86_64-Everything-2003.iso");

        try {
            File file = filePath.toFile();
            byte[] fileArray = FileUtil.getAllBytes(filePath);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void fileUpload(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            this.tusFileUploadService.process(req, res);

            UploadInfo uploadInfo = tusFileUploadService.getUploadInfo(req.getRequestURI());

            // 업로드한 이력이 없으면 파일생성 (속이 빈 파일임)
            if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {
                this.createFile(tusFileUploadService.getUploadedBytes(req.getRequestURI()), uploadInfo.getFileName());
                this.tusFileUploadService.deleteUpload(req.getRequestURI());
            }

            // Checksum이 불일치  할 때
            if (res.getStatus() == 460) {
                this.tusFileUploadService.deleteUpload(req.getRequestURI());
            }
        } catch (IOException | TusException e) {
            log.error("exception was occurred. message={}", e.getMessage(), e);
        }
    }

    private void createFile(InputStream inputStream, String fileName) throws IOException {
        File file = new File("/work/temp/", fileName);
        FileUtils.copyInputStreamToFile(inputStream, file);
    }
}