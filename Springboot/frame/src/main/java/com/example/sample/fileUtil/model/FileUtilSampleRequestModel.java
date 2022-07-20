package com.example.sample.fileUtil.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class FileUtilSampleRequestModel {
    private String data;
    private String destinationPath;
    private String sourcePath;
    private String delimiter;
    private List<String> sourcePathList = new ArrayList<String>();
}
