package com.example.web.tus.config;

import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.checksum.ChecksumExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Configuration
public class TusConfig {

    String tusStoragePath = "/work/temp";

    Long tusExpirationPeriod = 12000L;

    @PreDestroy
    public void Destroy() throws IOException {

    }

    @Bean
    public TusFileUploadService tus() {
        return new TusFileUploadService().withStoragePath(tusStoragePath)
                                            .withDownloadFeature()
                                            .withUploadExpirationPeriod(tusExpirationPeriod)
                                            .withUploadURI("/tusMain/upload");
//                                            .disableTusExtension("checksum");
    }
}
