package com.example.sample.zookeeper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ZookeeperRequestModel {
	private String znodeKey;
	private String znodeValue;
	private String znodeType;

	public String getZnodeKey() {
		return znodeKey;
	}
	public void setZnodeKey(String znodeKey) {
		this.znodeKey = znodeKey;
	}
	public String getZnodeValue() {
		return znodeValue;
	}
	public void setZnodeValue(String znodeValue) {
		this.znodeValue = znodeValue;
	}
	public String getZnodeType() {
		return znodeType;
	}
	public void setZnodeType(String znodeType) {
		this.znodeType = znodeType;
	}

}
