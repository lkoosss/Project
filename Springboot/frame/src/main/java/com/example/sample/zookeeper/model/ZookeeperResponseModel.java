package com.example.sample.zookeeper.model;

import lombok.Data;

import java.util.List;

@Data
public class ZookeeperResponseModel {
	private String znodeKey;
	private String znodeValue;
	private String znodeState;
	private List<String> znodeChild;
	private boolean result = true;
}
