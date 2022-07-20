package com.example.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class Constant {

    @AllArgsConstructor
    @Getter
    public enum ResponseCode {
        SUCCESS("0000","Success"),
        ERROR("1000","Error");

        String code;
        String message;
    }

    @AllArgsConstructor
    @Getter
    public enum SampleEnumCode {
        A_SAMPLE_CODE("SampleA","SampleA"),
        B_SAMPLE_CODE("SampleB","SampleB");

        String A1;
        String A2;
    }
}
