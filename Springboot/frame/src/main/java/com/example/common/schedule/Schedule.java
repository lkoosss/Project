package com.example.common.schedule;

import com.example.common.value.Constant;
import com.example.common.value.Constant.LogMarker;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class Schedule {

    private final TusFileUploadService tusFileUploadService;

    @Scheduled(cron = "0 * * * * *")
    public void delExpireUploadFile() {
        log.info(LogMarker.schedule, "delExpireUploadFile start");
        try {
            this.tusFileUploadService.cleanup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info(LogMarker.schedule, "delExpireUploadFile end");
    }
}
