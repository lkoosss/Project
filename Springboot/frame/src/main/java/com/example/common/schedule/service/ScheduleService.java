package com.example.common.schedule.service;

import lombok.RequiredArgsConstructor;
import me.desair.tus.server.TusFileUploadService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final TusFileUploadService tusFileUploadService;

    public void delExpireUploadFile() {
        try {
            this.tusFileUploadService.cleanup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
