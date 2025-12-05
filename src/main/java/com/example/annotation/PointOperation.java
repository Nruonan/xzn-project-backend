package com.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 积分操作注解
 * 用于标记需要处理积分的方法
 * @author Nruonan
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PointOperation {
    
    /**
     * 操作类型: post(发帖), comment(评论), like(点赞), collect(收藏)
     * @return 操作类型
     */
    String value();
    
    /**
     * 业务ID参数名
     * 用于获取帖子ID、评论ID等
     * @return 参数名
     */
    String idParam() default "tid";
    
    /**
     * 是否在方法执行前检查积分规则
     * @return true表示执行前检查
     */
    boolean checkBefore() default true;
}