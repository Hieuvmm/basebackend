package com.vworks.wms.common_lib.aop;

import com.vworks.wms.common_lib.utils.DateTimeFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.text.SimpleDateFormat;
import java.util.Date;

@Aspect
@Component
@Slf4j
public class AopLoggingAspect {
    @Around("execution(* com.vworks.wms.*.controller..*(..)))")
    public Object profileAllMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        //Get intercepted method details
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        String[] packageParts = methodSignature.getDeclaringTypeName().split("\\.");
        String moduleName = packageParts.length > 3 ? packageParts[3] : packageParts[packageParts.length - 1];

        final StopWatch stopWatch = new StopWatch();

        //Measure method execution time
        SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormatUtil.HH_MM_SS_SSS.getValue());
        stopWatch.start();
        String startTime = sdf.format(new Date());
        Object result = proceedingJoinPoint.proceed();
        String endTime = sdf.format(new Date());
        stopWatch.stop();

        //Log method execution time
        log.info(getClass().getSimpleName() + " for [" + moduleName + "] => Execution time of [" + className + "].[" + methodName
                + "] :: " + stopWatch.getTotalTimeMillis() + " ms - [" + startTime + "," + endTime + "]");

        return result;
    }
}