package com.example.recover.utils;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class LogAspect {

    // 拦截 controller 包下所有方法
    @Pointcut("execution(* com.example.recover.controller..*.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes != null
                ? attributes.getRequest()
                : null;

        // 获取方法信息
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = point.getSignature().getName();
        Object[] args = point.getArgs();

        // 处理请求参数
        List<Object> logArgs = Arrays.stream(args)
                .filter(arg -> !(arg instanceof HttpServletRequest))
                .filter(arg -> !(arg instanceof HttpServletResponse))
                .map(this::convertLogArgument)
                .collect(Collectors.toList());

        // 打印请求日志
        log.info("====> 请求开始 {}.{} | {} {} | 参数: {}",
                className,
                methodName,
                request != null ? request.getMethod() : "",
                request != null ? request.getRequestURI() : "",
                JSON.toJSONString(logArgs));

        Object result;
        try {
            // 执行原方法
            result = point.proceed();

            long cost = System.currentTimeMillis() - startTime;

            // 打印响应日志
            log.info("<==== 请求结束 {}.{} | 耗时: {}ms | 返回: {}",
                    className,
                    methodName,
                    cost,
                    JSON.toJSONString(result));

        } catch (Exception e) {
            long cost = System.currentTimeMillis() - startTime;
            log.error("<==== 请求异常 {}.{} | 耗时: {}ms | 异常: {}",
                    className,
                    methodName,
                    cost,
                    e.getMessage(),
                    e);
            throw e;
        }

        return result;
    }

    /**
     * 转换日志参数
     * <p>
     * MultipartFile 不能直接交给 FastJSON 序列化，
     * 这里只记录文件的基本信息。
     */
    private Object convertLogArgument(Object arg) {

        if (arg instanceof MultipartFile file) {

            return new MultipartFileLog(
                    file.getOriginalFilename(),
                    file.getSize(),
                    file.getContentType()
            );
        }

        return arg;
    }

    /**
     * MultipartFile 日志对象
     * private static class MultipartFileLog
     */
    private record MultipartFileLog(
            String fileName,
            long size,
            String contentType
    ) {
    }
}