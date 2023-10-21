package cn.kankancloud.jbp.web.advice;

import cn.kankancloud.jbp.core.Result;
import cn.kankancloud.jbp.core.exception.BizAuthenticateException;
import cn.kankancloud.jbp.core.exception.BizException;
import cn.kankancloud.jbp.core.exception.BizUnAuthorizeException;
import cn.kankancloud.jbp.core.exception.ErrorCodeI;
import cn.kankancloud.jbp.web.util.RequestUtil;
import cn.kankancloud.jbp.web.validation.FieldValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    private static final String BAD_REQUEST_MSG = "参数校验失败";

    /**
     * 处理 form data方式调用接口校验失败抛出的异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Result<Object> bindExceptionHandler(BindException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<FieldValidationError> errors = fieldErrors.stream()
                .map(f -> new FieldValidationError(f.getField(), f.getDefaultMessage()))
                .toList();

        return Result.failed(ErrorCodeI.VALIDATION_FAILED, BAD_REQUEST_MSG).withErrors(errors);
    }

    /**
     * 处理 json 请求体调用接口校验失败抛出的异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<FieldValidationError> errors = fieldErrors.stream()
                .map(f -> new FieldValidationError(f.getField(), f.getDefaultMessage()))
                .toList();

        return Result.failed(ErrorCodeI.VALIDATION_FAILED, BAD_REQUEST_MSG).withErrors(errors);
    }

    /**
     * 处理单个参数校验失败抛出的异常
     */
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseBody
    public Result<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        List<FieldValidationError> errors = constraintViolations.stream()
                .map(f -> new FieldValidationError(f.getPropertyPath().toString(), f.getMessage()))
                .toList();

        return Result.failed(ErrorCodeI.VALIDATION_FAILED, BAD_REQUEST_MSG).withErrors(errors);
    }

    /**
     * 处理业务处理异常
     */
    @ExceptionHandler(BizException.class)
    @ResponseBody
    public Result<Object> handleBizException(BizException e) {
        if (e instanceof BizAuthenticateException || e instanceof BizUnAuthorizeException) {
            HttpServletResponse response = RequestUtil.getResponse();
            // 401 unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return Result.failed(e.getCode(), e.getMessage());
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<Object> handleUnknownException(Exception e, HttpServletRequest request) {
        log.error("服务端处理异常, url={}, error={}", request.getRequestURI(), e.getMessage(), e);
        return Result.failed(ErrorCodeI.INTERNAL, "服务端异常");
    }
}
