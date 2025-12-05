package com.example.aspect;

import cn.hutool.core.bean.BeanUtil;
import com.example.annotation.PointOperation;
import com.example.config.user.UserContext;
import com.example.entity.dao.TopicDO;
import com.example.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 积分操作切面
 * 用于处理积分检查和添加逻辑
 * @author Nruonan
 */
@Aspect
@Component
@Slf4j
public class PointOperationAspect {
    
    @Autowired
    private PointService pointService;
    
    /**
     * 环绕通知，处理带有PointOperation注解的方法
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("@annotation(pointOperation)")
    public Object around(ProceedingJoinPoint joinPoint, PointOperation pointOperation) throws Throwable {
        // 获取当前用户ID
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            // 用户未登录，直接执行原方法
            return joinPoint.proceed();
        }
        
        // 获取操作类型
        String operationType = pointOperation.value();
        
        // 如果操作类型是interact，需要从方法参数中获取具体的操作类型
        if ("interact".equals(operationType)) {
            operationType = getInteractType(joinPoint);
        }
        // 执行原方法
        Object result = joinPoint.proceed();

        if (("like".equals(operationType) || "collect".equals(operationType)) && result.toString().contains("取消")) {
            return result;
        }
        // 方法执行成功后，添加积分
        String refId = getBusinessId(joinPoint, pointOperation.idParam(), operationType, result);
        String remark = getOperationTypeName(operationType); // 使用操作类型名称作为备注

        boolean addResult = pointService.addPoint(userId, operationType, refId, remark);
        if (!addResult) {
            // 积分添加失败，记录日志但不影响原方法执行
            log.error("添加积分失败: userId={}, type={}, refId={}", userId, operationType, refId);
        }
        
        return result;
    }
    
    /**
     * 获取业务ID
     * @param joinPoint 连接点
     * @param idParamName ID参数名
     * @param operationType 操作类型
     * @param result 方法执行结果
     * @return 业务ID
     */
    private String getBusinessId(ProceedingJoinPoint joinPoint, String idParamName, String operationType, Object result) {
        // 对于发帖操作，从返回结果中获取帖子ID
        if ("post".equals(operationType)) {
            return getPostIdFromResult(joinPoint, result);
        }
        
        // 对于interact操作，从Interact对象中获取tid
        if ("like".equals(operationType) || "collect".equals(operationType)) {
            return getInteractId(joinPoint);
        }
        
        // 对于comment操作，从参数中获取评论ID或帖子ID
        if ("comment".equals(operationType)) {
            return getCommentId(joinPoint);
        }
        
        // 对于其他操作，从方法参数中获取ID
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取方法参数
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        // 查找ID参数
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(idParamName)) {
                if (args[i] != null) {
                    // 如果参数是Integer或Long等数字类型，直接转换为字符串
                    if (args[i] instanceof Integer || args[i] instanceof Long) {
                        return args[i].toString();
                    }
                    // 如果参数是字符串，直接返回
                    if (args[i] instanceof String) {
                        return (String) args[i];
                    }
                    // 其他情况，尝试通过反射获取ID字段
                    try {
                        Method getIdMethod = args[i].getClass().getMethod("getId");
                        Object id = getIdMethod.invoke(args[i]);
                        if (id != null) {
                            return id.toString();
                        }
                    } catch (Exception e) {
                        // 忽略异常，继续尝试其他方式
                    }
                }
            }
        }
        
        // 如果没有找到ID参数，尝试从返回值中获取
        if (result != null) {
            // 如果返回值是Integer或Long等数字类型，直接转换为字符串
            if (result instanceof Integer || result instanceof Long) {
                return result.toString();
            }
            // 如果返回值是字符串，直接返回
            if (result instanceof String) {
                return (String) result;
            }
        }
        
        return "";
    }
    
    /**
     * 从interact参数中获取帖子ID
     * @param joinPoint 连接点
     * @return 帖子ID，如果state为false则返回空字符串
     */
    private String getInteractId(ProceedingJoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取方法参数
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        // 查找interact参数和state参数
        Object interactObj = null;
        Boolean state = null;
        
        for (int i = 0; i < parameters.length; i++) {
            if ("interact".equals(parameters[i].getName()) && args[i] != null) {
                interactObj = args[i];
            } else if ("state".equals(parameters[i].getName()) && args[i] != null) {
                state = (Boolean) args[i];
            }
        }
        
        // 如果state为false，表示取消操作，不加分，返回空字符串
        if (state != null && !state) {
            return "";
        }
        
        // 获取interact对象中的tid
        if (interactObj != null) {
            try {
                // 获取tid字段
                Method getTidMethod = interactObj.getClass().getMethod("getTid");
                Object tid = getTidMethod.invoke(interactObj);
                if (tid != null) {
                    return tid.toString();
                }
            } catch (Exception e) {
                System.err.println("获取interact的tid失败: " + e.getMessage());
            }
        }
        
        return "";
    }
    
    /**
     * 从评论参数中获取ID
     * @param joinPoint 连接点
     * @return ID
     */
    private String getCommentId(ProceedingJoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取方法参数
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        // 查找评论相关参数
        for (int i = 0; i < parameters.length; i++) {
            if (args[i] != null) {
                // 如果参数是AddCommentReqDTO，获取其中的tid或cid
                if (args[i].getClass().getSimpleName().equals("AddCommentReqDTO")) {
                    try {
                        // 优先获取评论ID
                        Method getCidMethod = args[i].getClass().getMethod("getCid");
                        Object cid = getCidMethod.invoke(args[i]);
                        if (cid != null && (cid instanceof Integer || cid instanceof Long)) {
                            return cid.toString();
                        }
                        
                        // 如果没有评论ID，获取帖子ID
                        Method getTidMethod = args[i].getClass().getMethod("getTid");
                        Object tid = getTidMethod.invoke(args[i]);
                        if (tid != null) {
                            return tid.toString();
                        }
                    } catch (Exception e) {
                        System.err.println("获取评论ID失败: " + e.getMessage());
                    }
                }
                // 如果参数是整数，可能是评论ID或帖子ID
                else if (args[i] instanceof Integer || args[i] instanceof Long) {
                    return args[i].toString();
                }
            }
        }
        
        return "";
    }
    
    /**
     * 从发帖结果中获取帖子ID
     * @param joinPoint 连接点
     * @param result 方法执行结果
     * @return 帖子ID
     */
    private String getPostIdFromResult(ProceedingJoinPoint joinPoint, Object result) {
        // createTopic方法现在返回Integer类型的帖子ID
        if (result != null && result instanceof Integer) {
            return result.toString();
        }
        
        // 如果返回值为null或其他类型，返回空字符串
        return "";
    }
    
    /**
     * 获取操作类型名称
     * @param operationType 操作类型
     * @return 操作类型名称
     */
    private String getOperationTypeName(String operationType) {
        switch (operationType) {
            case "post":
                return "发帖";
            case "comment":
                return "评论";
            case "like":
                return "点赞";
            case "collect":
                return "收藏";
            default:
                return operationType;
        }
    }
    
    /**
     * 从interact方法参数中获取具体的操作类型
     * @param joinPoint 连接点
     * @return 具体的操作类型（like或collect）
     */
    private String getInteractType(ProceedingJoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取方法参数
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        // 查找interact参数
        for (int i = 0; i < parameters.length; i++) {
            if ("interact".equals(parameters[i].getName()) && args[i] != null) {
                // 直接调用Interact对象的getType方法
                try {
                    Object interactObj = args[i];
                    Method getTypeMethod = interactObj.getClass().getMethod("getType");
                    Object type = getTypeMethod.invoke(interactObj);
                    return type.toString();
                } catch (Exception e) {
                    System.err.println("获取interact类型失败: " + e.getMessage());
                    return "like"; // 默认返回like
                }
            }
        }
        
        // 如果没有找到interact参数，默认返回like
        return "like";
    }
}