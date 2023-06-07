package com.example.common.schedule;

import com.example.common.schedule.service.ScheduleService;
import com.example.common.value.Constant;
import com.example.common.value.Constant.LogMarker;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class Schedule {

    @Autowired
    private ScheduleService scheduleService;

    @Scheduled(cron = "0 * * * * *")
    public void delExpireUploadFile() {
        log.info(LogMarker.schedule, "delExpireUploadFile start");
        this.scheduleService.delExpireUploadFile();
        log.info(LogMarker.schedule, "delExpireUploadFile end");
    }
}
