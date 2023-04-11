package com.example.common.value;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/** System 설정 (system-properties.yml) **/
@Component
@Getter @Setter
@ConfigurationProperties(prefix = "system")
public class SystemProp {

}
