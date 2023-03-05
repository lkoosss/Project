package com.example.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@Data
public class ResponseModel {
    protected String code;
    protected String message;

//    protected ResponseCode responseCode;
}
