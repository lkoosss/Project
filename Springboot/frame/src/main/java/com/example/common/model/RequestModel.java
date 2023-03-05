package com.example.common.model;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Marker;

@Getter @Setter
public class RequestModel {
    private Marker marker;          // 유니크 값을 가지는 Marker (callKey 또는 랜덤생성)
    protected String tid;           // 트랜잭션 ID
    protected String programId;     // 요청 프로그램 ID
}
