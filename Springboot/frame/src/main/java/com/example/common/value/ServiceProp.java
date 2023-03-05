package com.example.common.value;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "service")
public class ServiceProp {

    /** A 설정 **/
    public CProp cProp;

    /** B 설정 **/
    public DProp dProp;


    /** C 설정 **/
    @Getter @Setter
    public static class CProp {
        public int C1;
        public String C2;
        public boolean C3;
        public List<String> C4;
        public List<String> C5;
        public CGroup cGroup;

        @Getter @Setter
        public static class CGroup {
            public int C6;
        }
    }

    /** D 설정 **/
    @Getter @Setter
    public static class DProp {
        public int D1;
        public String D2;
        public boolean D3;
        public List<String> D4;
        public List<String> D5;
        public DGroup dGroup;

        @Getter @Setter
        public static class DGroup {
            public int D6;
        }
    }
}
