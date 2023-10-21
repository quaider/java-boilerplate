package cn.kankancloud.jbp.core.secure;

import cn.kankancloud.jbp.core.exception.BizUnAuthorizeException;
import cn.kankancloud.jbp.core.secure.permission.PermissionChecker;
import cn.kankancloud.jbp.core.util.Bools;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

@Aspect
public class AuthorizeInterceptor implements ApplicationContextAware {

    private static final ExpressionParser EL_PARSER = new SpelExpressionParser();

    @Around("@annotation(cn.kankancloud.jbp.core.secure.Authorize) || @within(cn.kankancloud.jbp.core.secure.Authorize)")
    public Object intercept(ProceedingJoinPoint point) throws Throwable {
        if (hasAuth(point)) {
            return point.proceed();
        }

        throw new BizUnAuthorizeException();
    }

    private boolean hasAuth(ProceedingJoinPoint point) {
        MethodSignature ms = (MethodSignature) point.getSignature();
        Method method = ms.getMethod();
        Authorize authorize = method.getAnnotation(Authorize.class);
        String condition = authorize.value();
        if (StringUtils.isBlank(condition)) {
            return false;
        }

        Expression expression = EL_PARSER.parseExpression(condition);
        // 方法参数值
        Object[] args = point.getArgs();
        StandardEvaluationContext context = getEvaluationContext(method, args);

        return Bools.isTrue(expression.getValue(context, Boolean.class));
    }

    /**
     * 获取方法上的参数
     *
     * @param method 方法
     * @param args   变量
     * @return {SimpleEvaluationContext}
     */
    private StandardEvaluationContext getEvaluationContext(Method method, Object[] args) {
        // 初始化Spring el表达式上下文
        StandardEvaluationContext context = new StandardEvaluationContext(new AuthorizeExpression(applicationContext.getBean(PermissionChecker.class)));
        context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        for (int i = 0; i < args.length; i++) {
            // 获取方法参数
            MethodParameter methodParam = getMethodParameter(method, i);
            // 设置方法 参数名和值 为spring el变量
            context.setVariable(methodParam.getParameterName(), args[i]);
        }

        return context;
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static MethodParameter getMethodParameter(Method method, int parameterIndex) {
        MethodParameter methodParameter = new SynthesizingMethodParameter(method, parameterIndex);
        methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
        return methodParameter;
    }
}
