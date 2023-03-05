package com.example.app.lifecycle.aop;

import com.example.common.model.RequestModel;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ApiAop {


    /**
     * <pre>
     *  webControllerAop
     *  - 타겟 메소드 호출 전 & 후 동작
     * </pre>
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.example..*Controller.*(..))")
    public Object webControllerApp(ProceedingJoinPoint joinPoint) throws Throwable {
        ////////// 필요 시 구현 //////////
        return joinPoint.proceed();
    }

    /**
     * <pre>
     *  setProgramId
     * </pre>
     *
     * @param signature
     * @param data
     */
    private void setProgramId(Signature signature, RequestModel data) {
        String packageName = signature.getDeclaringTypeName();
        String className = packageName.substring(packageName.lastIndexOf(".") + 1, packageName.length());
        String methodName = signature.getName();

        // ProgramId 설정
        data.setProgramId(className + "." + methodName);
    }
}
