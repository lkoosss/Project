package com.example.web.tus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class TusService {

    private final TusFileUploadService tusFileUploadService;


    public ResponseEntity<Object> fileDownload() {
        String pathString = "/work/temp/97_MB.mp4";

        try {
            Path filePath = Paths.get(pathString);
            Resource resource = new InputStreamResource(Files.newInputStream(filePath));

            File file = new File(pathString);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(file.getName()).build());

            return new ResponseEntity<Object>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object >(null, HttpStatus.CONFLICT);
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