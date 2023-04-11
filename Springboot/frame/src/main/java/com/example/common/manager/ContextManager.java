package com.example.common.manager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/** Application Context Manager **/
@Component
@Getter @Setter
public class ContextManager implements ApplicationContextAware {
    /*------------------
    |   클래스 귀속 변수   |
     ------------------*/
    private static ApplicationContext APP_CONTEXT;

    /**
     * <pre>
     *  setApplicationContext
     * </pre>
     *
     * @param applicationContext the ApplicationContext object to be used by this object
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.APP_CONTEXT = applicationContext;
    }

    public static Object getBeanAsObject(String beanName) {
        return APP_CONTEXT.getBean(beanName);
    }

    public static <T> T getBean(String beanName) {
        return (T) APP_CONTEXT.getBean(beanName);
    }


}
