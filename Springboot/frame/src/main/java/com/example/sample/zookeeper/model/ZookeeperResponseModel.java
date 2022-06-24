package com.example.sample.zookeeper.model;

import java.util.List;

public class ZookeeperResponseModel {
	private String znodeKey;
	private String znodeValue;
	private String znodeState;
	private List<String> znodeChild;
	private boolean result = true;

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

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getZnodeState() {
		return znodeState;
	}

	public void setZnodeState(String znodeState) {
		this.znodeState = znodeState;
	}

	public List<String> getZnodeChild() {
		return znodeChild;
	}

	public void setZnodeChild(List<String> znodeChild) {
		this.znodeChild = znodeChild;
	}
}
