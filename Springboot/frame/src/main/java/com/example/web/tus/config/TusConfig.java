package com.example.web.tus.config;

import me.desair.tus.server.TusFileUploadService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Configuration
public class TusConfig {

    String tusStoragePath = "/work/temp";

    Long tusExpirationPeriod = 120000L;

    @PreDestroy
    public void Destroy() throws IOException {

    }

    @Bean
    public TusFileUploadService tus() {
        return new TusFileUploadService().withStoragePath(tusStoragePath)
                                            .withDownloadFeature()
                                            .withUploadExpirationPeriod(tusExpirationPeriod)
                                            .withUploadURI("/tusMain/upload");
    }
}
