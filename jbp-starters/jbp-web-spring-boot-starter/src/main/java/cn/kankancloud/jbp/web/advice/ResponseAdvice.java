package cn.kankancloud.jbp.web.advice;

import cn.kankancloud.jbp.core.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    private static final String SPRING_DOC_STR = "springdoc";
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 排除拦截swagger相关api
        return !returnType.getDeclaringClass().getName().contains(SPRING_DOC_STR);
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof Result) {
            return body;
        } else if (body instanceof String) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            // 如果controller方法中返回的是String类型，但是加了@ResponseBody注解，那么在ResponseBodyAdvice中拦截到String类型并且处理完后需要最后返回一个String类型（可以转换成json字符串），否则会报错。
            return objectMapper.writeValueAsString(Result.success(body));
        }

        return Result.success(body);
    }
}
