package cn.kankancloud.jbp.web.springdoc;

import cn.kankancloud.jbp.core.Result;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springdoc.core.ReturnTypeParser;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * 自定义返回值解析（统一追加格式）
 * <a href="https://blog.csdn.net/catcher92/article/details/118926032">自定义返回值解析</a>
 */
public class CommonReturnTypeParser implements ReturnTypeParser {

    @Override
    public Type getReturnType(MethodParameter methodParameter) {
        Type returnType = ReturnTypeParser.super.getReturnType(methodParameter);
        Annotation[] annotations = Objects.requireNonNull(methodParameter.getMethod())
                .getDeclaringClass()
                .getAnnotations();
        if (Arrays.stream(annotations)
                .noneMatch(RestController.class::isInstance)) {
            return returnType;
        }

        if (returnType == void.class || returnType == Void.class) {
            return TypeUtils.parameterize(Result.class, Void.class);
        }

        Class<?> rawType = TypeUtils.getRawType(returnType, null);
        if (rawType.isAssignableFrom(Result.class)) {
            return returnType;
        }

        return TypeUtils.parameterize(Result.class, returnType);
    }

}
