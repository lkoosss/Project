package com.example.sample.fileUtil.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileUtilSampleRequestModel {
    private String data;
    private String destinationPath;
    private String sourcePath;
    private List<String> sourcePathList = new ArrayList<String>();

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public List<String> getSourcePathList() {
        return sourcePathList;
    }

    public void setSourcePathList(List<String> sourcePathList) {
        this.sourcePathList = sourcePathList;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
}
