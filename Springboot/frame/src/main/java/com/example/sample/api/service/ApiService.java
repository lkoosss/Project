package com.example.sample.api.service;

import com.example.common.value.Constant;
import com.example.common.value.ServiceProp;
import com.example.common.value.SystemProp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
@Service
public class ApiService {

    @Autowired
    SystemProp systemProp;

    @Autowired
    ServiceProp serviceProp;

    public void printSystemProp() {
        this.printField(this.serviceProp, this.serviceProp.baseProp);
    }

    private void printField(Object parentObj, Object childObj) {
        List<Field> fieldList = FieldUtils.getAllFieldsList(childObj.getClass());
        log.info("");
        try {
            for (Field field : fieldList) {
                String className = parentObj.getClass().getSimpleName();
                String fieldName = field.getName();
                Object value = FieldUtils.readField(childObj, fieldName, true);
                log.info(Constant.LogMarker.api, "{}: {}={}", className, field.getName(), value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
